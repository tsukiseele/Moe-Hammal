package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import com.tsukiseele.moehammal.MainApplication;
import com.tsukiseele.moehammal.bean.Image;
import com.tsukiseele.moehammal.helper.ImageItemFactory;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewController(value = "/fxml/ImageMasonryView.fxml")
public class ImageMasonryController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private JFXMasonryPane masonryPane;

	@PostConstruct
	public void init() {
		MainApplication.getContext().register("ImageMasonry", this);
		masonryPane.setMinSize(800, 600);
	}

	public void updateImageSet(List<Image> images) {
		masonryPane.getChildren().clear();
		addImageSet(images);
	}

	public void addImageSet(List<Image> images) {
		ArrayList<Node> children = new ArrayList<>();
		for (Image image : images) {
			children.add(ImageItemFactory.createItem(image, () -> {
				masonryPane.clearLayout();
				masonryPane.requestLayout();
			}));
		}
		masonryPane.getChildren().addAll(children);

		Platform.runLater(() -> scrollPane.requestLayout());
		JFXScrollPane.smoothScrolling(scrollPane);
	}

	public void addImage(Image image) {
		masonryPane.getChildren().add(ImageItemFactory.createItem(image, () -> {
			masonryPane.clearLayout();
			masonryPane.requestLayout();
		}));
	}

	public void clear() {
		masonryPane.getChildren().clear();
	}
}
