package com.tsukiseele.moehammal.helper;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import com.tsukiseele.moecrawler.http.HttpRequestPool;
import com.tsukiseele.moecrawler.utils.OkHttpUtil;
import com.tsukiseele.moehammal.bean.Image;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ImageItemFactory {
	public interface ItemUpdateCallback {
		void onUpdate();
	}
	private static int defaultWidth = 200;
	private static int defaultHeight = 200;
	private static int padding = 5;

	public static Node createItem(Image image, ItemUpdateCallback callback) {
		VBox root = new VBox();
		Label imageView = new Label();
		Label title = new Label(image.getTitle());
		root.setAlignment(Pos.CENTER);
		title.setAlignment(Pos.CENTER);
		imageView.setAlignment(Pos.CENTER);
		imageView.setPadding(new Insets(0, 0, padding, 0));
		imageView.setPrefWidth(defaultWidth);
		imageView.setPrefHeight(defaultHeight);
		root.setPrefWidth(defaultWidth);
		root.setPrefHeight(defaultHeight + title.getHeight() + padding * 2);
		updateImage(root, imageView, title, image.getPreviewUrl(), callback);

		SVGGlyph svgGlyph = null;
		try {
			svgGlyph = SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.image, photo, picture-o");
			svgGlyph.setSize(128, 128);
			svgGlyph.setFill(new ColorPicker(Color.WHITE).getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ObjectProperty<SVGGlyph> glyph = new SimpleObjectProperty<>();
		glyph.set(svgGlyph);
		imageView.setGraphic(glyph.get());
		//imageView.setStyle("-fx-background-radius: 5 5 5 5; -fx-background-color: rgb(236, 64, 122)");

		// 类型 颜色 尺寸 模糊深度 X轴偏移 Y轴偏移
		//root.setStyle("-fx-background-color: white, rgb(25, 255, 255);");
				//"-fx-effect: dropshadow(one-pass-box, #FFFFFF, 0, 0, 0, 0);" +
				//"-fx-border-color:  rgba(255, 255, 255, .80);" +
				//"-fx-border-radius: 8;" +
				// +
				//"-fx-padding: 0  0  0  0;");

//		imageView.setStyle("-fx-effect: dropshadow(one-pass-box, #757575, 12, 0, 4, 4);" +
//				//"-fx-border-color:  rgba(255, 255, 255, .80);" +
//				//"-fx-border-radius: 8;" +
//				"-fx-background-color: white, rgb(255, 255, 255);" +
//				"-fx-padding: 0  0  0  0;");

		root.setOnMouseEntered(e -> {
			root.setScaleX(1.05);
			root.setScaleY(1.05);
		});
		root.setOnMouseExited(e -> {
			root.setScaleX(1);
			root.setScaleY(1);
		});
		root.getChildren().addAll(imageView, title);
		JFXDepthManager.setDepth(imageView, 2);
		return root;
	}
	private static void updateImage(VBox root, Label imageView, Label title, String url, ItemUpdateCallback callback) {
		HttpRequestPool.execute(() -> {
			boolean isSuccess = false;
			for (int i = 0; i < 3 & !isSuccess; i++) {
				try {
					javafx.scene.image.Image pic = new javafx.scene.image.Image(OkHttpUtil.build().url(url).execute().body().byteStream());
					ImageView img = new ImageView(pic);
					Platform.runLater(() -> {
						double labelHeight = imageView.getPrefWidth() / pic.getWidth() * pic.getHeight();
						img.setFitHeight(labelHeight);
						img.setFitWidth(imageView.getPrefWidth());
						imageView.setPrefHeight(labelHeight);
						imageView.setGraphic(img);
//						System.out.println("image_height = " + imageView.getPrefHeight());
//						System.out.println("title_height = " + title.getHeight());
						root.setPrefHeight(imageView.getPrefHeight() + title.getHeight() + padding * 2);
						root.setPrefWidth(imageView.getPrefWidth());
//						System.out.println(root.getPrefHeight());
//						System.out.println(root.getHeight());
						callback.onUpdate();
					});
					isSuccess = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static int getDefaultWidth() {
		return defaultWidth;
	}
}
