package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXSnackbar;
import com.tsukiseele.moehammal.MainApplication;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/MainView.fxml")
public class MainController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private StackPane root;
	@FXML
	private StackPane imageMasonryView;
	@FXML
	private StackPane optionsView;
	@FXML
	private StackPane downloadView;

	@PostConstruct
	public void init() throws FlowException {
		MainApplication.getContext().register("Root", root);

		imageMasonryView.getChildren().add(new Flow(ImageMasonryController.class).start());
		optionsView.getChildren().add(new Flow(OptionsController.class).start());
		downloadView.getChildren().add(new Flow(DownloadController.class).start());
	}
}
