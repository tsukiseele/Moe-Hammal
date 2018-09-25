package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.tsukiseele.moehammal.MainApplication;
import com.tsukiseele.moehammal.config.Config;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/RootView.fxml", title = Config.APPLICTION_TITLE)
public class RootController {
	@FXML
	private StackPane drawerButtonContainer;
	@FXML
	private JFXHamburger drawerButton;
	@FXML
	private JFXDrawer drawer;
	@FXML
	private Label toolbarTitle;

	@PostConstruct
	public void init() {
		// 初始化顶部抽屉按钮
		drawer.setOnDrawerOpening(e -> {
			final Transition animation = drawerButton.getAnimation();
			animation.setRate(1);
			animation.play();
		});
		drawer.setOnDrawerClosing(e -> {
			final Transition animation = drawerButton.getAnimation();
			animation.setRate(-1);
			animation.play();
		});
		drawerButtonContainer.setOnMouseClicked(e -> {
			if (drawer.isClosed() || drawer.isClosing()) {
				drawer.open();
			} else {
				drawer.close();
			}
		});
		toolbarTitle.setText("ACG Gallery");
		// 初始化顶部菜单按钮
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/popup/MainPopup.fxml"));
//		loader.setController(new InputController());
//		toolbarPopup = new JFXPopup(loader.load());
//
//		optionsBurger.setOnMouseClicked(e -> toolbarPopup.show(optionsBurger,
//				PopupVPosition.TOP,
//				PopupHPosition.RIGHT,
//				-12,
//				15));
		//context = new ViewFlowContext();
		ViewFlowContext context = MainApplication.getContext();
		Flow flow = new Flow(MainController.class);
		FlowHandler flowHandler = flow.createHandler(context);
		try {
			drawer.setContent(flowHandler.start());
		} catch (FlowException e) {
			e.printStackTrace();
		}

		context.register("ContentFlow", flow);
	}
}