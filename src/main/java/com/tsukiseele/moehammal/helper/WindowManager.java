package com.tsukiseele.moehammal.helper;

import com.jfoenix.controls.JFXDecorator;
import com.tsukiseele.moecrawler.utils.TextUtil;
import com.tsukiseele.moehammal.MainApplication;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;

public class WindowManager {
	private ViewFlowContext context;
	private Stage stage;
	private Pane contentPane;
	private double minWidth;
	private double minHeight;
	private Class<?> windowClass;

	private WindowManager(Class<?> windowClass) throws FlowException {
		this.windowClass = windowClass;
		this.context = new ViewFlowContext();
	}

	public static WindowManager load(Class<?> windowClass) throws FlowException {
		return new WindowManager(windowClass);
	}

	public WindowManager create(String title, double width, double height) throws FlowException {
		this.minWidth = width;
		this.minHeight = height;
		this.stage = new Stage();
		Image icon = new Image(MainApplication.class.getResourceAsStream("/icons/icon.png"));
		ImageView imageView = new ImageView(icon);
		imageView.setFitHeight(28);
		imageView.setFitWidth(28);

		this.contentPane = new Flow(windowClass).createHandler(context).start();

		JFXDecorator decorator = new JFXDecorator(stage, contentPane);
		decorator.setCustomMaximize(true);
		decorator.setGraphic(imageView);

		Scene scene = new Scene(decorator, width, height);
		final ObservableList<String> stylesheets = scene.getStylesheets();
		stylesheets.addAll(
				MainApplication.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
				MainApplication.class.getResource("/css/jfoenix-design.css").toExternalForm(),
				MainApplication.class.getResource("/css/moehammal-main-black.css").toExternalForm()
		);
		if (TextUtil.nonEmpty(title))
			stage.setTitle(title);
		stage.getIcons().add(icon);
		stage.setMinWidth(width);
		stage.setMinHeight(height);
		stage.setScene(scene);

		return this;
	}

	public WindowManager create() throws FlowException {
		return create("");
	}

	public WindowManager create(String title) throws FlowException {
		return create(title, 800, 600);
	}

	public WindowManager putObjects(HashMap<String, Object> objectHashMap) {
		for (String key : objectHashMap.keySet())
			context.register(key, objectHashMap.get(key));
		return this;
	}

	public WindowManager putObject(String key, Object value) {
		context.register(key, value);
		return this;
	}

	public void show() {
		stage.show();
	}

	public Stage getStage() {
		return stage;
	}

	public ViewFlowContext getContext() {
		return context;
	}

	public Pane getContentPane() {
		return contentPane;
	}

	public double getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	public double getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}
}
