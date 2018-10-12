package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.effects.JFXDepthManager;
import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.http.HttpRequestPool;
import com.tsukiseele.moehammal.bean.Image;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ViewController(value = "/fxml/ImageMasonryView.fxml")
public class ImageMasonryController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private JFXMasonryPane masonryPane;

	private ImageItem.OnItemClickListener onItemClickListener;

	@PostConstruct
	public void init() {
		context.register("ImageMasonry", this);
		masonryPane.setMinSize(800, 600);
	}

	public void updateImageSet(Crawler crawler, List<Image> images) {
		masonryPane.getChildren().clear();
		addImageSet(crawler, images);
	}

	public void addImageSet(Crawler crawler, List<Image> images) {
		for (Image image : images)
			addImage(crawler, image);
		Platform.runLater(() -> scrollPane.requestLayout());
		JFXScrollPane.smoothScrolling(scrollPane);
	}

	public void addImage(Crawler crawler, Image image) {
		ImageItem imageItem = new ImageItem(crawler, image, () -> {
			masonryPane.clearLayout();
			masonryPane.requestLayout();
		}, onItemClickListener);
		masonryPane.getChildren().add(imageItem);
	}

	public void clear() {
		masonryPane.getChildren().clear();
	}

	public ImageItem.OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(ImageItem.OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public static class ImageItem extends VBox {
		public interface ItemUpdateCallback {
			void onUpdate();
		}

		public interface OnItemClickListener {
			void onClick(Image image);
		}

		private static final int defaultWidth = 200;
		private static final int defaultHeight = 200;
		private static final int padding = 5;

		private Crawler crawler;
		private Image image;
		private ImageView imageView;
		private Label title;
		private ItemUpdateCallback itemUpdateCallback;
		private OnItemClickListener onItemClickListener;

		public ImageItem(Crawler crawler, Image image, ItemUpdateCallback itemUpdateCallback, OnItemClickListener onItemClickListener) {
			super(10);
			this.crawler = crawler;
			this.image = image;
			this.imageView = new ImageView();
			this.title = new Label(image.getTitle());
			this.itemUpdateCallback = itemUpdateCallback;
			this.onItemClickListener = onItemClickListener;

			setAlignment(Pos.CENTER);
			setPrefWidth(defaultWidth);
			setPrefHeight(defaultHeight + title.getHeight() + padding * 2);
			imageView.setFitWidth(defaultWidth);
			imageView.setFitHeight(defaultHeight);
			imageView.setStyle("-fx-background-radius: 3");
			title.setAlignment(Pos.CENTER);
			updateImage();

//			SVGGlyph svgGlyph = null;
//			try {
//				svgGlyph = SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.image, photo, picture-o");
//				svgGlyph.setSize(128, 128);
//				svgGlyph.setFill(new ColorPicker(Color.WHITE).getValue());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			javafx.scene.image.Image icon = new javafx.scene.image.Image("/icons/icon.png");

			imageView.setImage(icon);

			if (onItemClickListener != null)
				setOnMouseClicked(event -> {
					onItemClickListener.onClick(image);
				});
			setOnMouseEntered(e -> {
				setScaleX(1.05);
				setScaleY(1.05);
			});
			setOnMouseExited(e -> {
				setScaleX(1);
				setScaleY(1);
			});
			getChildren().addAll(imageView, title);
			JFXDepthManager.setDepth(imageView, 1);
		}

		private void updateImage() {
			HttpRequestPool.execute(() -> {
				boolean isSuccess = false;
				for (int i = 0; i < 3 & !isSuccess; i++) {
					try {
						InputStream inputStream = crawler.requestByteStream(image.getCoverUrl());

						javafx.scene.image.Image pic = new javafx.scene.image.Image(inputStream);

						Platform.runLater(() -> {
							double labelHeight = imageView.getFitWidth() / pic.getWidth() * pic.getHeight();
							imageView.setFitHeight(labelHeight);
							imageView.setImage(pic);
							setPrefHeight(imageView.getFitHeight() + title.getHeight() + padding * 2);
							setPrefWidth(imageView.getFitWidth());
							if (itemUpdateCallback != null)
								itemUpdateCallback.onUpdate();
						});
						isSuccess = true;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		public OnItemClickListener getOnItemClickListener() {
			return onItemClickListener;
		}

		public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
			this.onItemClickListener = onItemClickListener;
		}
	}
}