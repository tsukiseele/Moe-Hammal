package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXDrawer;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/ContentView.fxml")
public class ContentController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private JFXDrawer drawer;
	@FXML
	private BorderPane content;
	@FXML
	private StackPane imageMasonryView;
	@FXML
	private StackPane optionsView;
	@FXML
	private StackPane downloadView;

	@PostConstruct
	public void init() throws FlowException {
		context.register("drawer", drawer);

		drawer.setContent(content);

		Flow sideMenuFlow = new Flow(SideMenuController.class);
		FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
		drawer.setSidePane(sideMenuFlowHandler.start());

		imageMasonryView.getChildren().add(new Flow(ImageMasonryController.class).createHandler(context).start());
		optionsView.getChildren().add(new Flow(OptionsController.class).createHandler(context).start());
		downloadView.getChildren().add(new Flow(DownloadController.class).createHandler(context).start());
	}
}
