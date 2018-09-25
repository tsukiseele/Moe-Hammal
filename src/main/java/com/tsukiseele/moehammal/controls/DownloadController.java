package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXListView;
import com.tsukiseele.moecrawler.download.DownloadTask;
import com.tsukiseele.moehammal.MainApplication;
import com.tsukiseele.moehammal.helper.DownloadItemFactory;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/DownloadView.fxml")
public class DownloadController {
	@FXML
	private JFXListView downloadListView;

	@PostConstruct
	public void init() {
		MainApplication.getContext().register("Download", this);
		downloadListView.setMinWidth(100);
	}

	public boolean addDownloadItem(DownloadTask task) {
		downloadListView.getItems().add(DownloadItemFactory.createItem(task));
		return true;
	}
}
