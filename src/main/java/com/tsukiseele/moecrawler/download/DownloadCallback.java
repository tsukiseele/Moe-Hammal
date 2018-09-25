package com.tsukiseele.moecrawler.download;

import java.io.Serializable;

public interface DownloadCallback extends Serializable {
	// 开始
	void onStart(DownloadInfo info);
	// 进度
	void onProgress(DownloadInfo info);
	// 取消
	void onCancel(DownloadInfo info);
	// 等待
	void onWait(DownloadInfo info);
	// 完成
	void onSuccess(DownloadInfo info);
	// 错误
	void onError(DownloadInfo info);
	// 继续
	void onResume(DownloadInfo info);
}

