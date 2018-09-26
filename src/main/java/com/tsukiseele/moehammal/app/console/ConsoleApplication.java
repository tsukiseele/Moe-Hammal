package com.tsukiseele.moehammal.app.console;

import com.tsukiseele.moecrawler.utils.LogUtil;

/**
 * MoeCrawler-使用类似CSS选择器语法进行资源抓取的轻量级爬虫
 *
 * Version: 1.1.1
 * Developer: TsukiSeele
 * 
 */

public class ConsoleApplication {

	public static void main(String... args) {
		LogUtil.setLevel(LogUtil.Level.CLOSE);
		try {
			ConsoleLauncher.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
