package com.tsukiseele.moehammal.app.console;

import com.tsukiseele.moecrawler.utils.OkHttpUtil;
import com.tsukiseele.moehammal.app.WebDriverManager;
import okhttp3.Request;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

/**
 * MoeCrawler-使用类似CSS选择器语法进行资源抓取的轻量级爬虫
 *
 * Version: 1.1.1
 * Developer: TsukiSeele
 * 
 */

public class ConsoleApplication {
	public static void main_(String... args) throws IOException {
		Request request = new Request.Builder()
				.url("https://exhentai.org/g/1293858/c3b2846811/?inline_set=ts_l")
				.header("igneous", "c550a3bc1")
				.header("ipb_member_id", "3552014")
				.header("ipb_pass_hash", "c53281d74b22c2eb675872d7a800a2cb")
				.header("Cookie", "igneous=c550a3bc1; ipb_member_id=3552014; ipb_pass_hash=c53281d74b22c2eb675872d7a800a2cb; yay=0; s=945bdbf07; sk=hx2vghdv1oczhtgzb7vs1dz283cx; lv=1526437120-1527519150")
				.build();

		System.out.println(OkHttpUtil.getOkHttpClient().newCall(request).execute().body().string());
	}

	public static void main(String[] args) {
		WebDriverManager webDriverManager = WebDriverManager.instance();
		webDriverManager.init(WebDriverManager.BROWSER_TYPE_FIREFOX);
		WebDriver driver = webDriverManager.getWebDriver();

		driver.get("https://exhentai.org/g/1293858/c3b2846811/?inline_set=ts_l");
		WebDriver.Options options = driver.manage();

		options.addCookie(new Cookie("igneous", "aa2976800"));
		options.addCookie(new Cookie("ipb_member_id", "4284686"));
		options.addCookie(new Cookie("ipb_pass_hash", "0e39979493de251eb3d32c7926a21957"));
		driver.get("https://exhentai.org/g/1293858/c3b2846811/?inline_set=ts_l");
		String title = driver.getPageSource();
		System.out.println(title);
	}

	public static void main__(String... args) throws IOException, InterruptedException {
		WebDriverManager webDriverManager = WebDriverManager.instance();
		webDriverManager.init(WebDriverManager.BROWSER_TYPE_FIREFOX);
		WebDriver driver = webDriverManager.getWebDriver();

		driver.get("https://exhentai.org/g/1293858/c3b2846811/?inline_set=ts_l");
		WebDriver.Options options = driver.manage();

		options.addCookie(new Cookie("igneous", "c550a3bc1"));
		options.addCookie(new Cookie("ipb_member_id", "3552014"));
		options.addCookie(new Cookie("ipb_pass_hash", "c53281d74b22c2eb675872d7a800a2cb"));
		driver.get("https://exhentai.org/g/1293858/c3b2846811/?inline_set=ts_l");
		String title = driver.getPageSource();
		System.out.println(title);
		driver.quit();

//		LogUtil.setLevel(LogUtil.Level.CLOSE);
//		try {
//			ConsoleLauncher.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		WebClient webClient = new WebClient(BrowserVersion.CHROME);
//		//webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
//		//webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
//		webClient.getOptions().setActiveXNative(true);
//		webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
//		webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
//		webClient.getOptions().setUseInsecureSSL(true);//忽略ssl认证
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
//
//		CookieManager cookieManager = webClient.getCookieManager();
////		cookieManager.addCookie(new Cookie(".exhentai.org", "igneous", "c550a3bc1"));
////		cookieManager.addCookie(new Cookie(".exhentai.org", "ipb_member_id", "3552014"));
////		cookieManager.addCookie(new Cookie(".exhentai.org", "ipb_pass_hash", "c53281d74b22c2eb675872d7a800a2cb"));
//		HtmlPage page = null;
//		try {
//			page = webClient.getPage("https://www.baidu.com/baidu.html");//尝试加载上面图片例子给出的网页
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			//webClient.close();
//		}
//
//		webClient.waitForBackgroundJavaScript(30000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
//		Thread.sleep(30000);
//
//		String pageXml = page.asXml();//直接将加载完成的页面转换成xml格式的字符串
//		System.out.println(pageXml);
		//System.out.println(OkHttpUtil.getOkHttpClient().newCall(request).execute().body().string());
//		Response response = OkHttpUtil.build()
//
//				.execute();
//		System.out.println(response.body().string());
	}
}
