package com.tsukiseele.moehammal.controls.windows;

import com.jfoenix.controls.JFXSpinner;
import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.core.MoeParser;
import com.tsukiseele.moecrawler.utils.UniversalUtil;
import com.tsukiseele.moehammal.bean.Image;
import com.tsukiseele.moehammal.controls.OptionsController;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;

@ViewController(value = "/fxml/windows/ImageViewerView.fxml")
public class ImageViewerWindow {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private ImageView imageViewerContent;
	@FXML
	private StackPane imageViewerContainer;
	@FXML
	private JFXSpinner imageViewerLoading;

	private ArrayList<Image> images;

	@PostConstruct
	public void init() {
		Crawler crawler = (Crawler) context.getRegisteredObject("crawler");
		Image image = (Image) context.getRegisteredObject("image");
		images = (ArrayList<Image>) context.getRegisteredObject("images");

		if (images != null) {
			int index = 0;
			for (int i = 0; i < images.size(); i++)
				if (image.hashCode() == images.get(i).hashCode()) {
					index = i;
					break;
				}
		}
		if (images != null && !images.isEmpty()) {
			imageViewerContainer.setOnKeyPressed((event -> {
				if (event.getCode() == KeyCode.U) {
					imageViewerContainer.setScaleY(0.5);
				} else if (event.getCode() == KeyCode.D) {
					imageViewerContainer.setScaleX(0.5);
				}
			}));
		}
		imageViewerLoading.setVisible(true);
		new Thread(() -> {
			javafx.scene.image.Image img = null;
			try {
				// 加载额外规则
				if (image.hasExtra())
					crawler.buildParser(Image.class).parseFillExtra(image);
				// 请求图片
				img = new javafx.scene.image.Image(
						crawler.requestByteStream(OptionsController.getUrl(image)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			final javafx.scene.image.Image picture = img;
			Platform.runLater(() -> {
				imageViewerLoading.setVisible(false);

				ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
					double height = imageViewerContainer.getScene().getHeight();
					double width = imageViewerContainer.getScene().getWidth();
					double imageWidth = picture.getWidth();
					double imageHeight = picture.getHeight();
					double proportion = width / height;
					double newWidth;
					double newHeight;
					if (imageWidth / imageHeight > proportion) {
						newWidth = width;
						newHeight = width / imageWidth * imageHeight;
					} else {
						newHeight = height;
						newWidth = height / imageHeight * imageWidth;
					}
					imageViewerContent.setFitHeight(newHeight);
					imageViewerContent.setFitWidth(newWidth);
				};
				imageViewerContainer.getScene().widthProperty().addListener(listener);
				imageViewerContainer.getScene().heightProperty().addListener(listener);
				// 强制刷新
				listener.changed(null, 0, 0);
				imageViewerContent.setImage(picture);
			});
		}).start();
	}
}