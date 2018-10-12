package com.tsukiseele.moehammal.interfaces;

import com.tsukiseele.moecrawler.download.DownloadCallback;
import com.tsukiseele.moecrawler.download.DownloadInfo;
import javafx.application.Platform;

public class SyncDownloadCallback implements DownloadCallback {
	private DownloadCallback callback;
	private long progressUpdateTime = 0;

	public SyncDownloadCallback(DownloadCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onStart(DownloadInfo info) {
		Platform.runLater(() -> {
			callback.onStart(info);
		});
	}

	@Override
	public void onProgress(DownloadInfo info) {
		if (System.currentTimeMillis() - progressUpdateTime > 256) {
			Platform.runLater(() -> {
				callback.onProgress(info);
			});
			progressUpdateTime = System.currentTimeMillis();
		}
	}

	@Override
	public void onCancel(DownloadInfo info) {
		Platform.runLater(() -> {
			callback.onCancel(info);
		});
	}

	@Override
	public void onWait(DownloadInfo info) {
		Platform.runLater(() -> {
			callback.onWait(info);
		});
	}

	@Override
	public void onSuccess(DownloadInfo info) {
		Platform.runLater(() -> {
			callback.onSuccess(info);
		});
	}

	@Override
	public void onError(DownloadInfo info) {
		Platform.runLater(() -> {
			callback.onError(info);
		});
	}

	@Override
	public void onResume(DownloadInfo info) {
		Platform.runLater(() -> {
			callback.onResume(info);
		});
	}
}
