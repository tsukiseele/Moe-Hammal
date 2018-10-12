package com.tsukiseele.moecrawler;
/*
	最后修改时间 2018.10.04


 */

import com.tsukiseele.moecrawler.bean.Section;
import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.utils.FileUtil;
import com.tsukiseele.moecrawler.utils.TextUtil;
import com.tsukiseele.moehammal.app.Config;
import com.tsukiseele.moehammal.app.WebDriverManager;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.Serializable;

public class MoeCrawler extends Crawler implements Serializable {
	private Site site;
	private Mode mode;
	
	private MoeCrawler(Site site) {
		this.site = site;
	}
	
	public static MoeCrawler with(Site site) {
		MoeCrawler crawler = new MoeCrawler(site);
		return crawler;
	}
	
	// 使用指定的模式爬取
	public MoeCrawler params(int pageCode) {
		this.mode = new Mode(TYPE_HOME, pageCode, null, null, null);
		return this;
	}

	public MoeCrawler params(int pageCode, String keyword) {
		if (TextUtil.isEmpty(keyword))
			return params(pageCode);
		this.mode = new Mode(TYPE_SEARCH, pageCode, keyword, null, null);
		return this;
	}

	public MoeCrawler params(int pageCode, String extraKey, String extraData) {
		if (TextUtil.isEmpty(extraKey))
			return params(pageCode);
		this.mode = new Mode(TYPE_EXTRA, pageCode, null, extraKey, extraData);
		return this;
	}
	
	@Override
	protected String request(String url) throws IOException {

		if (site.hasFlag(Site.FLAG_LOAD_JS)) {
			try {

				return loadJavaScript(url);
			} catch (Exception e) {
				e.printStackTrace();
				return super.request(url);
			}
		} else {
			return super.request(url);
		}
	}

	private String loadJavaScript(String url) {
		/*
		final String DIR = Config.PATH_ROOT + "\\drivers\\phantomjs";
		final String PHANTOMJS_PATH = DIR + "\\phantomjs.exe";
		final String JAVASCRIPT_PATH = DIR + "\\load_document.js";

		String html = null;
		try {
			String exec = PHANTOMJS_PATH + " " + JAVASCRIPT_PATH + " " + url;
			System.out.println(exec);
			Process process = Runtime.getRuntime().exec(exec);
			process.getInputStream();
			html = FileUtil.readText(process.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/


		boolean isDebug = site.hasFlag(Site.FLAG_DEBUG);
		WebDriverManager webDriverManager = WebDriverManager.instance();
		webDriverManager.init(WebDriverManager.BROWSER_TYPE_FIREFOX);
		webDriverManager.getFirefoxOptions().setHeadless(!isDebug);

		WebDriver driver = webDriverManager.getWebDriver();
		driver.get(url);
		WebDriver.Options options = driver.manage();

//		options.addCookie(new Cookie("igneous", "aa2976800"));
//		options.addCookie(new Cookie("ipb_member_id", "4284686"));
//		options.addCookie(new Cookie("ipb_pass_hash", "0e39979493de251eb3d32c7926a21957"));
//		driver.get("https://exhentai.org/g/1293858/c3b2846811/?inline_set=ts_l");
		String html = driver.getPageSource();
		if (!isDebug) driver.quit();

		System.out.println(html);

		return html;
	}

//		return html;


//		WebClient webClient = new WebClient();
//		webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
//		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
//		webClient.getOptions().setActiveXNative(false);
//		webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
//		webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
//		webClient.setJavaScriptTimeout(15000);
//
//		CookieManager cookieManager = webClient.getCookieManager();
//		cookieManager.addCookie(new Cookie(".exhentai.org", "igneous", "c550a3bc1"));
//		cookieManager.addCookie(new Cookie(".exhentai.org", "ipb_member_id", "3552014"));
//		cookieManager.addCookie(new Cookie(".exhentai.org", "ipb_pass_hash", "c53281d74b22c2eb675872d7a800a2cb"));
//		HtmlPage page = null;
//		try {
//			page = webClient.getPage(url);//尝试加载上面图片例子给出的网页
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			webClient.close();
//		}
//
//		webClient.waitForBackgroundJavaScriptStartingBefore(15000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
//
//		try {
//			Thread.sleep(15000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println(page.getDocumentElement().asXml());
//		String pageXml = page.asXml();//直接将加载完成的页面转换成xml格式的字符串
//
//		https://exhentai.org/g/1293846/ec362d1c78/?inline_set=ts_l
	
	@Override
	protected String onResponse(Section section, String html) {
		return html;
	}
	
	@Override
	public Crawler.Mode getMode() {
		return mode;
	}

	@Override
	public Site getSite() {
		return site;
	}
}
