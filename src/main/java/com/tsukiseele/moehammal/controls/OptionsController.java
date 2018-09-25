package com.tsukiseele.moehammal.controls;

import com.jfoenix.controls.*;
import com.tsukiseele.moecrawler.MoeCrawler;
import com.tsukiseele.moecrawler.bean.Catalog;
import com.tsukiseele.moecrawler.bean.Gallery;
import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moecrawler.core.MoeParser;
import com.tsukiseele.moecrawler.download.DownloadManager;
import com.tsukiseele.moecrawler.download.DownloadTask;
import com.tsukiseele.moecrawler.utils.TextUtil;
import com.tsukiseele.moehammal.MainApplication;
import com.tsukiseele.moehammal.bean.Image;
import com.tsukiseele.moehammal.config.Config;
import com.tsukiseele.moehammal.helper.SiteRuleManager;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@ViewController(value = "/fxml/OptionsView.fxml")
public class OptionsController {
	@FXML
	private JFXComboBox<Label> optionsSiteCombo;
	@FXML
	private JFXTextField optionsSearchInput;
	@FXML
	private JFXTextField optionsStartPageCodeInput;
	@FXML
	private JFXTextField optionsEndPageCodeInput;
	@FXML
	private Label optionsCurrentPageText;

	private List<Site> sites;
	private MoeCrawler crawler;

	private Gallery<Image> images;
	private MoeParser<Image> parser;

	private boolean isDownloadStateFlag = false;

	@PostConstruct
	public void init() {
		MainApplication.getContext().register("Options", this);
		// 初始化规则选择栏
		updateSiteCombo();

		// 初始化页码输入框
		optionsStartPageCodeInput.focusedProperty().addListener((obj, oldValue, newValue) -> {
			if (!newValue)
				checkNumberInput(optionsStartPageCodeInput);
		});
		optionsEndPageCodeInput.focusedProperty().addListener((obj, oldValue, newValue) -> {
			if (!newValue)
				checkNumberInput(optionsEndPageCodeInput);
		});
	}
	// 检验输入合法性
	private void checkNumberInput(JFXTextField field) {
		try {
			String text = field.getText();
			if (TextUtil.isEmpty(text))
				return;
			Integer.parseInt(text);
		} catch (NumberFormatException e) {
			MainApplication.showSnackbar("页码必须是整数");
			field.clear();
		}
		int startValue = TextUtil.toInt(optionsStartPageCodeInput.getText());
		int endValue = TextUtil.toInt(optionsEndPageCodeInput.getText());
		if (startValue > endValue)
			optionsEndPageCodeInput.setText(String.valueOf(startValue));
	}

	private void updateSiteCombo() {
		sites = SiteRuleManager.instance().getSites();
		String[] siteNames = new String[sites.size()];
		for (int i = 0; i < sites.size(); i++)
			siteNames[i] = sites.get(i).getTitle();
		ObservableList items = FXCollections.observableArrayList(siteNames);
		optionsSiteCombo.setItems(items);
		optionsSiteCombo.getSelectionModel().selectFirst();
	}

	private Site getCurrentSite() {
		return sites.get(optionsSiteCombo.getSelectionModel().getSelectedIndex());
	}

	@FXML
	public void onSearchClick(ActionEvent event) {
		ImageMasonryController controller = (ImageMasonryController) MainApplication.getContext().getRegisteredObject("ImageMasonry");
		isDownloadStateFlag = false;
		controller.clear();
		new Thread(() -> {
			// 创建对话框
			JFXDialog dialog = new JFXDialog();
			JFXDialogLayout dialogLayout = new JFXDialogLayout();
			JFXButton button = new JFXButton("取消");
			button.setButtonType(JFXButton.ButtonType.FLAT);
			button.setRipplerFill(Paint.valueOf("#EC407A"));
			button.setStyle("-fx-text-fill: FF4081; -fx-font-size:14px; -fx-background-color:#FFFFFF; " +
					"-fx-pref-width: 80; -fx-pref-height: 40; ");
			button.setOnAction((actionEvent) -> {
				dialog.close();
				Thread.currentThread().interrupt();
				MainApplication.showSnackbar("加载已取消");
			});
			// 更新视图
			Platform.runLater(() -> {
				dialogLayout.setPadding(new Insets(5, 5, 5, 5));
				dialogLayout.setHeading(new Label("提示"));
				dialogLayout.setBody(new HBox(20, new JFXSpinner(), new Label("正在加载中，请耐心等待...")));
				dialogLayout.setActions(button);
				dialog.setContent(dialogLayout);
				dialog.show((StackPane) MainApplication.getContext().getRegisteredObject("Root"));
			});
			// 加载数据
			DownloadManager.getThreadPool().setCorePoolSize(5);
			crawler = MoeCrawler
					.with(getCurrentSite())
					.params(TextUtil.toInt(optionsStartPageCodeInput.getText()), optionsSearchInput.getText());
			parser = crawler.buildParser(Image.class);
			try {
				images = parser.parseGallery();
				// 测试线程状态
				if (Thread.currentThread().isInterrupted())
					return;
				// 更新视图
				Platform.runLater(() -> {
					dialog.close();
					if (images.isEmpty()) {
						MainApplication.showSnackbar("该页没有数据");
					} else {
						controller.updateImageSet(images);
						updateCurrentPageCode(crawler.getMode().pageCode);
					}
				});
			} catch (IOException e) {
				Platform.runLater(() -> {
					MainApplication.showSnackbar("加载失败：" + e.toString());
					dialog.close();
				});
				e.printStackTrace();
			}
		}).start();
	}

	@FXML
	public void onDownloadClick() {
		DownloadController downloadController = (DownloadController) MainApplication.getContext().getRegisteredObject("Download");
		ImageMasonryController imageMasonryController = (ImageMasonryController) MainApplication.getContext().getRegisteredObject("ImageMasonry");

		int startCode = TextUtil.toInt(optionsStartPageCodeInput.getText());
		int endCode = TextUtil.toInt(optionsEndPageCodeInput.getText());
		String keyword = optionsSearchInput.getText();

		isDownloadStateFlag = true;
		new Thread(() -> {
			MoeCrawler crawler = MoeCrawler.with(getCurrentSite());
			MoeParser<Image> parser = crawler.buildParser(Image.class);

			for (int i = startCode; i <= endCode; i++) {
				crawler.params(i, keyword);
				Platform.runLater(() -> updateCurrentPageCode(crawler.getMode().pageCode));

				try {
					parser.parseAll(new MoeParser.MetaDataParseCallback<Image>() {
						@Override
						public void onCatalogSuccess(Catalog<Image> datas) {
							for (Image image : datas) {
								try {
									DownloadTask task = new DownloadTask(image.getSimpleUrl(), Config.PATH_DOWNLOAD.getAbsolutePath(), true);
									DownloadManager.execute(task);
									Platform.runLater(() -> {
										downloadController.addDownloadItem(task);
										if (isDownloadStateFlag)
											imageMasonryController.addImageSet(datas);
									});
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onItemSuccess(Image data) {
							try {
								DownloadTask task = new DownloadTask(data.getSimpleUrl(), Config.PATH_DOWNLOAD.getAbsolutePath(), true);
								DownloadManager.execute(task);
								Platform.runLater(() -> {
									downloadController.addDownloadItem(task);
									if (isDownloadStateFlag)
										imageMasonryController.addImage(data);
								});
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
//				MoeCrawler crawler = MoeCrawler.with(getCurrentSite()).params(startCode);
//				MoeParser parser = crawler.buildParser(Image.class);
//				try {
//					Gallery<Image> images = parser.parseGallery();
//					for (Image image : images) {
//						try {
//							List<Image> imgs = parser.parseCatalog(image);
//							for (Image img : imgs) {
//								if (image.hasExtra())
//									parser.parseFillExtra(img);
//								DownloadTask task = new DownloadTask(img.getSimpleUrl(), Config.PATH_DOWNLOAD.getAbsolutePath(), true);
//								DownloadManager.execute(task);
//								Platform.runLater(() -> controller.addDownloadItem(task));
//							}
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				} catch (Exception e) {
//
//				}
			}
		}).start();
	}

	@FXML
	private void onRefreshRuleClick(ActionEvent event) {
		SiteRuleManager.instance().loadRule();
		updateSiteCombo();
	}

	private void updateCurrentPageCode(int pageCode) {
		optionsCurrentPageText.setText("第 " + pageCode + " 页");
	}
}
