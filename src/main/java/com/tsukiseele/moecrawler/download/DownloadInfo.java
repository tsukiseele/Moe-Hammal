package com.tsukiseele.moecrawler.download;
import com.tsukiseele.moecrawler.utils.FileUtil;
import okhttp3.Headers;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DownloadInfo implements Serializable {
	// 无状态
	public static final int STATE_NONE = -1;
	// 正在开始
	public static final int STATE_START = 0;
	// 下载中
	public static final int STATE_LOADING = 1;
	// 下载暂停
	public static final int STATE_CANCEL = 2;
	// 下载等待
	public static final int STATE_WAIT = 3;
	// 下载完成
	public static final int STATE_SUCCESS = 4;
	// 下载出错
	public static final int STATE_ERROR = 5;
	
	// 源文件连接
	public final String url;
	// 下载路径
	public final String path;
	public final String dir;
	public final String fileName;
	// 当前状态
	public int state = STATE_NONE;
	// 当前长度
	public long currentLength;
	// 文件总长度
	public long totalLength;
	// 当前重试次数
	public int retryCount;
	// 错误信息
	public Exception exception;

	public Headers headers = new Headers.Builder().build();
	
	private transient DownloadCallback callback;
	
	public DownloadInfo(String url, String dir, String fileName) {
		this.url = url;
		this.dir = dir;
		this.fileName = fileName;
		this.path = dir + File.separator + fileName;
	}

	public DownloadInfo(String url, String dir) throws UnsupportedEncodingException {
		this(url, dir, false);
	}

	public DownloadInfo(String url, String dir, boolean isWindowsFilename) throws UnsupportedEncodingException {
		this(url, dir, isWindowsFilename, "UTF-8");
	}

	public DownloadInfo(String url, String dir, boolean isWindowsFilename, String charset) throws UnsupportedEncodingException {
		this(url, dir, isWindowsFilename
				? FileUtil.getWindowsFilename(URLDecoder.decode(FileUtil.getUrlFilename(url), charset))
				: URLDecoder.decode(FileUtil.getUrlFilename(url), charset));
	}

	public void notifyStart() {
		state = STATE_START;
		if (callback != null) callback.onStart(this);
	}
	
	public void notifyWait() {
		state = STATE_WAIT;
		if (callback != null) callback.onWait(this);
	}
	
	public void notifyResume() {
		state = STATE_START;
		if (callback != null) callback.onResume(this);
	}
	
	public void notifyCancel() {
		state = STATE_CANCEL;
		if (callback != null) callback.onCancel(this);
		
	}
	public void notifyProgress() {
		state = STATE_LOADING;
		if (callback != null) callback.onProgress(this);
		
	}
	public void notifySuccess() {
		state = STATE_SUCCESS;
		if (callback != null) callback.onSuccess(this);
		
	}
	public void notifyError() {
		state = STATE_ERROR;
		if (callback != null) callback.onError(this);
	}
	// 判断当前状态
	public boolean isState(int state) {
		return this.state == state;
	}
	// 是否支持断点续传
	public boolean isContinuingly() {
		return totalLength > 0;
	}

	public void setDownloadCallback(DownloadCallback callback) {
		this.callback = callback;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}
}
