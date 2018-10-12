package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.*;
import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moehammal.helper.SiteRuleManager;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewController(value = "/fxml/ToolbarView.fxml")
public class ToolbarController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private StackPane drawerButtonContainer;
	@FXML
	private JFXHamburger drawerButton;
	@FXML
	private Label toolbarTitle;
	@FXML
	private JFXComboBox toolbarSiteCombo;
	@FXML
	private JFXTextField toolbarSearchInput;
	@FXML
	private JFXButton toolbarSearchButton;

	private List<Site> sites;

	@PostConstruct
	public void init() throws Exception {
		JFXDrawer drawer = (JFXDrawer) context.getRegisteredObject("drawer");
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
		updateSiteCombo();
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

	}
	@FXML
	private void onSearchClick() {

	}

	// 更新Site选择框
	private void updateSiteCombo() {
		sites = SiteRuleManager.instance().getSites();
		String[] siteNames = new String[sites.size()];
		for (int i = 0; i < sites.size(); i++)
			siteNames[i] = sites.get(i).getTitle();
		ObservableList items = FXCollections.observableArrayList(siteNames);
		toolbarSiteCombo.setItems(items);
		toolbarSiteCombo.getSelectionModel().selectFirst();
	}
}
