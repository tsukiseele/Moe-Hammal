package com.tsukiseele.moecrawler.download;

import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.utils.FileUtil;
import com.tsukiseele.moecrawler.utils.LogUtil;
import com.tsukiseele.moecrawler.utils.OkHttpUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements Runnable {
	// 下载状态信息
	private DownloadInfo info;
	
	// 是否已取消
	private boolean isCancel;
	
	public DownloadTask(String url, String path) throws UnsupportedEncodingException {
		this(new DownloadInfo(url, path));
	}

	public DownloadTask(String url, String path, boolean isWindowFile) throws UnsupportedEncodingException {
		this(new DownloadInfo(url, path, isWindowFile));
	}

	public DownloadTask(DownloadInfo info) {
		this.info = info;
	}


	public void addRequestHeader(String key, String value) {
		info.headers.newBuilder().set(key, value);
	}

	public void addRequestHeaders(Headers headers) {
		info.headers = headers;
	}

	@Override
	public void run() {
		BufferedInputStream bis = null;
		RandomAccessFile raf = null;
		try {
			info.notifyStart();
			isCancel = false;
			raf = new RandomAccessFile(info.path, "rw");
			
			Request.Builder request = new Request.Builder()
					.url(info.url)
					.headers(info.headers)
					.get();
			// 如果该文件支持断点续传
			if (info.totalLength > 0) {
				request.header("Range", "bytes=" + info.currentLength + "-");
				raf.seek(info.currentLength);
				info.notifyResume();
			} else {
				info.currentLength = 0;
			}
			Response response = OkHttpUtil.getOkHttpClient().newCall(request.build()).execute();

			if (response.isSuccessful()) {
				bis = new BufferedInputStream(response.body().byteStream());
				info.totalLength = response.body().contentLength();
				int len;
				byte[] buff = new byte[8192];
				while ((len = bis.read(buff)) != -1) {
					raf.write(buff, 0, len);
					info.currentLength += len;
					info.notifyProgress();
					if (isCancel) {
						FileUtil.close(raf);
						FileUtil.close(bis);
						info.notifyCancel();
						return;
					}
				}
				info.notifySuccess();
				LogUtil.i(DownloadTask.class.getCanonicalName(), "请求成功\n" + response.headers());
			} else {
				info.exception = new ConnectException("request fail: " + response.headers());
				info.notifyError();
			}
		} catch (IOException e) {
			info.exception = e;
			info.notifyError();
			LogUtil.e(DownloadTask.class.getCanonicalName(), e.toString());
		} finally {
			FileUtil.close(bis);
			FileUtil.close(raf);
		}
	}
	public DownloadInfo getInfo() {
		return info;
	}

	public void cancel() {
		isCancel = true;
	}

	public boolean isState(int state) {
		return this.info.state == state;
	}

	// 通知等待
	public void notifyWait() {
		this.info.notifyWait();
	}

	public DownloadTask addDownloadCallback(DownloadCallback callback) {
		this.info.setDownloadCallback(callback);
		return this;
	}
	public void removeDownloadCallback() {
		this.info.setDownloadCallback(null);
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return info.hashCode();
	}
}
