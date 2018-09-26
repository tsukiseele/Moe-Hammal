package com.tsukiseele.moehammal.app.console;

import com.tsukiseele.moehammal.bean.Image;
import com.tsukiseele.moehammal.config.Config;
import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.core.MoeParser;
import com.tsukiseele.moecrawler.MoeCrawler;
import com.tsukiseele.moecrawler.bean.Catalog;
import com.tsukiseele.moecrawler.bean.Gallery;
import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moecrawler.download.DownloadCallback;
import com.tsukiseele.moecrawler.download.DownloadInfo;
import com.tsukiseele.moecrawler.download.DownloadManager;
import com.tsukiseele.moecrawler.download.DownloadTask;
import com.tsukiseele.moecrawler.utils.FileUtil;
import com.tsukiseele.moecrawler.utils.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ConsoleLauncher {
	private static final String TAG = ConsoleLauncher.class.getCanonicalName();

	private static final String RULE_PATH_N_HENTAI = Config.PATH_ROOT + "/source/rules/comic_n-hentai.json";
	private static final String RULE_PATH_YANDE_POST = Config.PATH_ROOT + "/source/rules/image_yande_post.json";
	private static final String RULE_PATH_E_HENTAI = Config.PATH_ROOT + "/source/rules/comic_e-hentai.json";

	private static Crawler crawler;

	public static void start() throws IOException {
		String tempRulePath = RULE_PATH_YANDE_POST;

		String json = FileUtil.readText(tempRulePath);
		LogUtil.i(TAG, json);

		// 解析规则
		Site site = Site.fromJSON(json);
		LogUtil.i(TAG, site.toString());

		// 设置下载线程池大小
		DownloadManager.getThreadPool().setCorePoolSize(5);

		// 初始化爬虫
		crawler = MoeCrawler.with(site)
				.params(-1, "aki99");
				//.commit();

		// 构造解析器
		MoeParser<Image> parser = crawler.buildParser(Image.class);

		// 遍历页面
		for (int i = 0; i < 10; i++) {
			Gallery<Image> imageGallery = parser.parseGallery(i);
			// 遍历二级页面
			for (Image source : imageGallery) {
				if (source.hasCatalog()) {
					Catalog<Image> images = parser.parseCatalog(source);
					for (Image image : images) {
						executeTask(image);
					}
				} else {
					executeTask(source);
				}
			}
			System.out.println("\nEnd");
		}
	}
	// 执行下载任务
	public static void executeTask(Image image) {
		try {
			DownloadManager.execute(new DownloadTask(image.getSimpleUrl(), Config.PATH_DOWNLOAD.getAbsolutePath())
					.addDownloadCallback(new DownloadCallback() {
						@Override
						public void onStart(DownloadInfo info) {
							System.out.println(info.path + " - 开始下载");
						}

						@Override
						public void onProgress(DownloadInfo info) {
							System.out.println(info.path + " - " + FileUtil.formatDataSize(info.currentLength) + "/" + FileUtil.formatDataSize(info.totalLength));
						}

						@Override
						public void onCancel(DownloadInfo info) {
							System.out.println(info.path + " - 下载取消");
						}

						@Override
						public void onWait(DownloadInfo info) {
							System.out.println(info.path + " - 下载等待");
						}

						@Override
						public void onSuccess(DownloadInfo info) {
							System.out.println(info.path + " - 下载完成");
						}

						@Override
						public void onError(DownloadInfo info) {
							System.out.println(info.path + " - 下载失败：" + info.exception.toString());
							System.out.println("正在重试");
							DownloadManager.resume(info.url);
						}

						@Override
						public void onResume(DownloadInfo info) {
							System.out.println(info.path + " - 下载恢复");
						}
					}));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
