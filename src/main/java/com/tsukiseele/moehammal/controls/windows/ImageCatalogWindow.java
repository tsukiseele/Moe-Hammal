package com.tsukiseele.moehammal.controls.windows;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.tsukiseele.moecrawler.bean.Catalog;
import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.core.MoeParser;
import com.tsukiseele.moehammal.bean.Image;
import com.tsukiseele.moehammal.controls.ImageMasonryController;
import com.tsukiseele.moehammal.helper.WindowManager;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import javax.annotation.PostConstruct;
import java.io.IOException;

@ViewController(value = "/fxml/windows/ImageCatalogView.fxml", title = "图册")
public class ImageCatalogWindow {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private StackPane imageCatalogContent;
	@FXML
	private JFXButton imageCatalogDownloadButton;
	@FXML
	private JFXSpinner imageCatalogLoadProgress;

	private Crawler crawler;
	private Image image;
	private Catalog<Image> imageCatalog;

	@PostConstruct
	public void init() throws FlowException {
		try {
			Pane masonryList = new Flow(ImageMasonryController.class).createHandler(context).start();
			imageCatalogContent.getChildren().add(masonryList);

			ImageMasonryController controller = (ImageMasonryController) context.getRegisteredObject("ImageMasonry");
			controller.setOnItemClickListener(img -> {
				try {
					Rectangle2D bounds = Screen.getScreens().get(0).getBounds();

					WindowManager.load(ImageViewerWindow.class)
							.putObject("crawler", crawler)
							.putObject("image", img)
							.putObject("images", imageCatalog)
							.create("图片预览", bounds.getWidth() / 1.75, bounds.getHeight() / 1.75)
							.show();
				} catch (FlowException e) {
					e.printStackTrace();
				}
			});
			loadCatalog();
		} catch (FlowException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void onDownloadClick() {

	}

	public void loadCatalog() {
		crawler = (Crawler) context.getRegisteredObject("crawler");
		image = (Image) context.getRegisteredObject("image");
		ImageMasonryController controller = (ImageMasonryController) context.getRegisteredObject("ImageMasonry");

		imageCatalogLoadProgress.setVisible(true);
		MoeParser<Image> parser = crawler.buildParser(Image.class);
		new Thread(() -> {
			try {
				imageCatalog = parser.parseCatalog(image);
				Platform.runLater(() -> controller.addImageSet(crawler, imageCatalog));

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				imageCatalogLoadProgress.setVisible(false);
			}
		}).start();
	}
}
