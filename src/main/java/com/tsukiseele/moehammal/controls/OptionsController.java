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
import com.tsukiseele.moecrawler.utils.UniversalUtil;
import com.tsukiseele.moehammal.MainApplication;
import com.tsukiseele.moehammal.bean.Image;
import com.tsukiseele.moehammal.app.Config;
import com.tsukiseele.moehammal.controls.windows.ImageCatalogWindow;
import com.tsukiseele.moehammal.controls.windows.ImageViewerWindow;
import com.tsukiseele.moehammal.helper.WindowManager;
import com.tsukiseele.moehammal.helper.SiteRuleManager;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@ViewController(value = "/fxml/OptionsView.fxml")
public class OptionsController {
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private JFXComboBox<Label> optionsSiteCombo;
	@FXML
	private JFXComboBox<Label> optionsQualityCombo;
	@FXML
	private JFXTextField optionsSearchInput;
	@FXML
	private JFXTextField optionsStartPageCodeInput;
	@FXML
	private JFXTextField optionsEndPageCodeInput;
	@FXML
	private Label optionsCurrentPageText;
	@FXML
	private JFXSpinner optionsLoadProgress;

	private List<Site> sites;
	private MoeCrawler crawler;

	private Gallery<Image> images;
	private MoeParser<Image> parser;

	private static String imageUrlFlag = Image.URL_SIMPLE;
	private boolean isDownloadStateFlag = false;

	@PostConstruct
	public void init() {
		context.register("Options", this);
		// 初始化规则选择栏
		updateSiteCombo();
		// 初始化品质选择框
		initQualityCombo();

		// 初始化页码输入框
		optionsStartPageCodeInput.focusedProperty().addListener((obj, oldValue, newValue) -> {
			if (!newValue)
				checkNumberInput(optionsStartPageCodeInput);
		});
		optionsEndPageCodeInput.focusedProperty().addListener((obj, oldValue, newValue) -> {
			if (!newValue)
				checkNumberInput(optionsEndPageCodeInput);
		});

		optionsSearchInput.setOnKeyPressed((event -> {
			if (event.getCode() == KeyCode.ENTER)
				onSearchClick(null);
		}));
	}

	@FXML
	public void onSearchClick(ActionEvent event) {
		ImageMasonryController controller = (ImageMasonryController) context.getRegisteredObject("ImageMasonry");
		controller.setOnItemClickListener(image -> {
			try {
				if (image.hasCatalog()) {
					WindowManager.load(ImageCatalogWindow.class)
							.putObject("crawler", crawler)
							.putObject("image", image)
							.create("图册")
							.show();
				} else {
					WindowManager.load(ImageViewerWindow.class)
							.putObject("crawler", crawler)
							.putObject("image", image)
							.create("图片预览")
							.show();
				}

			} catch (FlowException e) {
				e.printStackTrace();
			}
		});
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
				optionsLoadProgress.setVisible(false);
				Thread.currentThread().interrupt();
				MainApplication.showSnackbar("加载已取消");
			});
			// UI操作
			Platform.runLater(() -> {
				// 显示加载进度条
				optionsLoadProgress.setVisible(true);
				// 弹出对话框
				dialogLayout.setPadding(new Insets(5, 5, 5, 5));
				dialogLayout.setHeading(new Label("提示"));
				dialogLayout.setBody(new HBox(20, new JFXSpinner(), new Label("少女祈祷中，请耐心等待...")));
				dialogLayout.setActions(button);
				dialog.setContent(dialogLayout);
				dialog.show(MainController.getContent());
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
					if (images.isEmpty()) {
						MainApplication.showSnackbar("该页没有数据");
					} else {
						controller.updateImageSet(crawler, images);
						updateCurrentPageCode(crawler.getMode().pageCode);
					}
				});
			} catch (IOException e) {
				Platform.runLater(() -> {
					MainApplication.showSnackbar("加载失败：" + e.toString());
				});
				e.printStackTrace();
			} finally {
				// 关闭加载显示
				optionsLoadProgress.setVisible(false);
				dialog.close();
			}
		}).start();
	}

	@FXML
	public void onDownloadClick() {
		DownloadController downloadController = (DownloadController) context.getRegisteredObject("Download");
		ImageMasonryController imageMasonryController = (ImageMasonryController) context.getRegisteredObject("ImageMasonry");

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
								System.out.println("Catalog -- " + UniversalUtil.toString(image));
								try {
									DownloadTask task = new DownloadTask(image.getUrl(imageUrlFlag), Config.PATH_DOWNLOAD.getAbsolutePath(), true);
									task.addRequestHeaders(crawler.getHeaders());
									task.addRequestHeader("Referer", image.getCatalogUrl());
									System.out.println("------------------------------------" + task.getInfo().headers);

									DownloadManager.execute(task);
									Platform.runLater(() -> {
										downloadController.addDownloadItem(task);
										if (isDownloadStateFlag)
											imageMasonryController.addImageSet(crawler, datas);

									});
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onItemSuccess(Image image) {
							try {
								System.out.println("Item -- " + UniversalUtil.toString(image));
								DownloadTask task = new DownloadTask(image.getUrl(imageUrlFlag), Config.PATH_DOWNLOAD.getAbsolutePath(), true);
								task.addRequestHeaders(crawler.getHeaders());
								task.addRequestHeader("Referer", image.getCatalogUrl());
								System.out.println("------------------------------------" + task.getInfo().headers);
								DownloadManager.execute(task);
								Platform.runLater(() -> {
									downloadController.addDownloadItem(task);
									if (isDownloadStateFlag)
										imageMasonryController.addImage(crawler, image);
								});
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
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
	// 更新Site选择框
	private void updateSiteCombo() {
		sites = SiteRuleManager.instance().getSites();
		String[] siteNames = new String[sites.size()];
		for (int i = 0; i < sites.size(); i++)
			siteNames[i] = sites.get(i).getTitle();
		ObservableList items = FXCollections.observableArrayList(siteNames);
		optionsSiteCombo.setItems(items);
		optionsSiteCombo.getSelectionModel().selectFirst();
	}
	// 初始化品质选框
	private void initQualityCombo() {
		ObservableList items = FXCollections.observableArrayList(
				"源文件",
				"高品质",
				"标准");
		optionsQualityCombo.getItems().addAll(items);
//		optionsQualityCombo.focusedProperty().addListener((obj, oldValue, newValue) -> {
//			newValue.
//		});
		//optionsQualityCombo.set
		//optionsQualityCombo.getSelectionModel().selectLast();

	}

	private Site getCurrentSite() {
		return sites.get(optionsSiteCombo.getSelectionModel().getSelectedIndex());
	}

	public static String getUrl(Image image) {
		return image.getUrl(imageUrlFlag);
	}
}
