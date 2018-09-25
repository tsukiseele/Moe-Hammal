package com.tsukiseele.moehammal.helper;

import com.jfoenix.controls.JFXProgressBar;
import com.tsukiseele.moecrawler.download.DownloadCallback;
import com.tsukiseele.moecrawler.download.DownloadInfo;
import com.tsukiseele.moecrawler.download.DownloadManager;
import com.tsukiseele.moecrawler.download.DownloadTask;
import com.tsukiseele.moecrawler.utils.FileUtil;
import com.tsukiseele.moehammal.interfaces.SyncDownloadCallback;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DownloadItemFactory {
	public static Node createItem(DownloadTask task) {
		VBox root = new VBox();
//		FlowPane progressContainer = new FlowPane();
		Label title = new Label();
		Label message = new Label();
		JFXProgressBar progressBar = new JFXProgressBar();

		root.setSpacing(5);
		// 设为0使其自动适配
		root.setPrefWidth(0);
		title.setText(task.getInfo().fileName);
		progressBar.getStyleClass().add("custom-jfx-progress-bar-stroke");
		//progressBar.setPrefWidth(250 - message.getPrefWidth());
//		progressContainer.setAlignment(Pos.CENTER_LEFT);
//		progressContainer.setPrefWidth(root.getPrefWidth());
//		progressContainer.getChildren().addAll(progressBar, message);
		root.getChildren().addAll(title, progressBar, message);
		task.addDownloadCallback(new SyncDownloadCallback(new DownloadCallback() {
			@Override
			public void onStart(DownloadInfo info) {
				message.setText("正在开始");
			}

			@Override
			public void onProgress(DownloadInfo info) {
				progressBar.setProgress((float) info.currentLength / info.totalLength);
				message.setText(FileUtil.formatDataSize(info.currentLength) + "/" + FileUtil.formatDataSize(info.totalLength));
			}

			@Override
			public void onCancel(DownloadInfo info) {
				message.setText("下载取消");
			}

			@Override
			public void onWait(DownloadInfo info) {
				message.setText("下载等待");
			}

			@Override
			public void onSuccess(DownloadInfo info) {
				progressBar.setProgress((float) info.currentLength / info.totalLength);
				message.setText("下载完成");
			}

			@Override
			public void onError(DownloadInfo info) {
				if (info.retryCount < 3) {
					message.setText("第" + ++info.retryCount + "次重试");
					DownloadManager.resume(info.url);
				} else {
					message.setText("下载失败");
				}
			}

			@Override
			public void onResume(DownloadInfo info) {
				message.setText("正在开始");
			}
		}));

		return root;
	}
}
