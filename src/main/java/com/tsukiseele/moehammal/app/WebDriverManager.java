package com.tsukiseele.moehammal.app;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;

public class WebDriverManager {
	public static final String BROWSER_TYPE_FIREFOX = "browser_type_firefox";
	public static final String BROWSER_TYPE_CHROME = "browser_type_chrome";

	private static WebDriverManager instance;
	private WebDriver webDriver;

	private FirefoxOptions firefoxOptions;
	private ChromeOptions chromeOptions;

	private String browserType;

	private WebDriverManager() {}

	public synchronized static WebDriverManager instance() {
		if (instance == null) {
			synchronized (WebDriverManager.class) {
				instance = new WebDriverManager();
			}
		}
		return instance;
	}

	// 初始化浏览器驱动，如果浏览器没有默认安装在C盘，需要自己确定其路径
	public void init(String browserType) {
		this.browserType = browserType;

		final String SEP = File.separator;
		final String ROOT = System.getProperty("user.dir");

		switch (browserType) {
			case BROWSER_TYPE_CHROME:
				// 配置谷歌浏览器Headless驱动
				System.setProperty("webdriver.chrome.driver", ROOT + SEP + "drivers" + SEP + "chromedriver.exe");
				chromeOptions = new ChromeOptions();
				break;
			case BROWSER_TYPE_FIREFOX:
				// 配置火狐浏览器Headless驱动
				System.setProperty("webdriver.gecko.driver", ROOT + SEP + "drivers" + SEP + "geckodriver.exe");
				firefoxOptions = new FirefoxOptions();
				break;
		}
	}

	public WebDriver getWebDriver() {
		switch (browserType) {
			case BROWSER_TYPE_CHROME :
				return new ChromeDriver(chromeOptions);
			case BROWSER_TYPE_FIREFOX :
				return new FirefoxDriver(firefoxOptions);
			default:
				throw new NullPointerException("未指定浏览器类型");
		}
	}

	public FirefoxOptions getFirefoxOptions() {
		return firefoxOptions;
	}

	public ChromeOptions getChromeOptions() {
		return chromeOptions;
	}

	public String getBrowserType() {
		return browserType;
	}
}
