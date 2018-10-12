package com.tsukiseele.moehammal.controls;

import com.tsukiseele.moehammal.app.Config;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/MainView.fxml", title = Config.APPLICTION_TITLE)
public class MainController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private static StackPane toolbarContainer;
	@FXML
	private static StackPane contentContainer;

	@PostConstruct
	public void init() throws Exception {
		new Flow(ContentController.class).createHandler(context).startInPane(contentContainer);
		new Flow(ToolbarController.class).createHandler(context).startInPane(toolbarContainer);
//		context.register("contentContainer");
//		context.register("toolbarContainer");
	}

	public static StackPane getContent() {
		return contentContainer;
	}
}