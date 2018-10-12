package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.tsukiseele.moecrawler.download.DownloadCallback;
import com.tsukiseele.moecrawler.download.DownloadInfo;
import com.tsukiseele.moecrawler.download.DownloadManager;
import com.tsukiseele.moecrawler.download.DownloadTask;
import com.tsukiseele.moecrawler.utils.FileUtil;
import com.tsukiseele.moehammal.interfaces.SyncDownloadCallback;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/DownloadView.fxml")
public class DownloadController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private JFXListView downloadListView;

	@PostConstruct
	public void init() {
		context.register("Download", this);
		downloadListView.setMinWidth(100);
	}

	public void addDownloadItem(DownloadTask task) {
		downloadListView.getItems().add(DownloadItemFactory.createItem(task));
	}

	public static class DownloadItemFactory {

		public static Node createItem(DownloadTask task) {
			VBox root = new VBox();
			Label title = new Label();
			Label message = new Label();
			JFXProgressBar progressBar = new JFXProgressBar();

			title.setText(task.getInfo().fileName);
			progressBar.getStyleClass().add("custom-jfx-progress-bar-stroke");
			root.setSpacing(5);
			// 设为 0 使其自动适配宽度
			root.setPrefWidth(0);
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
}
