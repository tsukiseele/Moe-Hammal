package com.tsukiseele.moecrawler.core;

import com.tsukiseele.moecrawler.bean.Section;
import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moecrawler.utils.OkHttpUtil;
import com.tsukiseele.moecrawler.utils.TextUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tsukiseele.moecrawler.core.Const.*;

/**
 * 抽象类，可以重写此类部分方法以自定义HTML加载方式与逻辑
 *
 *
 */
public abstract class Crawler implements Serializable {
	public static final int TYPE_HOME = 0;
	public static final int TYPE_SEARCH = 1;
	public static final int TYPE_EXTRA = 2;
	
	public static final String[] USER_AGENTS = new String[] {
		"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.109 Safari/537.36", 
		"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
		"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;",
		"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
		"Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1"
	};
	
	public abstract Mode getMode();

	public abstract Site getSite();
	
	protected abstract String onResponse(Section section, String html);

	public InputStream requestByteStream(String url) throws IOException {
		OkHttpClient client = OkHttpUtil.getOkHttpClient();
		Request request = new Request.Builder()
				.headers(getHeaders(getSite()))
				.url(url)
				.get()
				.build();
		Response response = client.newCall(request).execute();
		return response.body().byteStream();
	}
	public Reader requestCharStream(String url) throws IOException {

		OkHttpClient client = OkHttpUtil.getOkHttpClient();
		Request request = new Request.Builder()
				.headers(getHeaders(getSite()))
				.url(url)
				.get()
				.build();
		Response response = client.newCall(request).execute();
		return response.body().charStream();
	}

	protected String request(String url) throws IOException {
		// 请求网页
		OkHttpClient client = OkHttpUtil.getOkHttpClient();
		Request request = new Request.Builder()
			.headers(getHeaders(getSite()))
			.url(url)
			.get()
			.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	public final String execute() throws IOException {
		Section section = getSection();
		Mode mode = getMode();
		if (section == null)
			throw new NullPointerException("section not exists! key = " + mode.extraKey);
		// 处理URL，替换里面的占位符和转义字符
		String url = encodeURL(replaceUrlPlaceholder(section.getIndexUrl(), mode.pageCode, mode.keyword));
		return onResponse(section, request(url));
	}
	
	
	public final Section getSection() {
		return findCurrentRule(getSite(), getMode().type, getMode().extraKey);
	}
	
	public final <T extends Mappable> MoeParser<T> buildParser(Class<T> type) {
		return new MoeParser<T>(this, type);
	}
	
	/**
	 * 随机获取UA
	 *
	 */
	public static final String getDefaultUserAgent() {
		return USER_AGENTS[(int) (Math.random() * USER_AGENTS.length)];
	}
	
	public Headers getHeaders() {
		return getHeaders(getSite());
	}
	
	/**
	 * 获取请求头
	 *
	 */
	public static Headers getHeaders(Site site) {
		Map<String, String> headerMap = new HashMap<>();
		// 添加默认请求头
		headerMap.put("User-Agent", getDefaultUserAgent());
		
		// 合并规则内的请求头
		if (site.getRequestHeaders() != null && !site.getRequestHeaders().isEmpty())
			headerMap.putAll(site.getRequestHeaders());

		return Headers.of(headerMap);
	}
	
	/**
	 * 替换所有占位符
	 *
	 */
	private String replaceUrlPlaceholder(String indexUrl, int pageCode, String keyword) {
		return replacePageCode(replaceSearchKeyword(indexUrl, keyword), pageCode);
	}
	
	/**
	 * 占位符：
	 * {page:a} 表示页面从pageCode + a页开始加载
	 * {page:a, b} 表示页面从(pageCode + a) * b页开始加载
	 *
	 * a 补正码，可以对pageCode给予一定的补正
	 * b 步距，可以改变pageCode的步距
	 */
	public static String replacePageCode(String indexUrl, int pageCode) {
		// 补正码
		int correct = 0;
		// 步距，默认为1
		int pace = 1;
		
		// 替换页码
		Matcher pageMatcher = PATTERN_CONTENT_PAGE.matcher(indexUrl);
		if (pageMatcher.find()) {
			int groupCount = pageMatcher.groupCount();
			int[] ints = new int[] {0, 1};
			for (int i = 1; i <= groupCount; i++) {
				String group = pageMatcher.group(i);
				if (!TextUtil.isEmpty(group))
					ints[i - 1] = Integer.parseInt(group);
			}
			correct = ints[0];
			pace = ints[1];
		}
		// 页码值 (当前页码 + 起始页码) * 修正码
		return indexUrl.replaceAll(REGEX_PLACEHOLDER_PAGE, String.valueOf((pageCode + correct) * pace));
	}
	
	/**
	 * {keyword:} 表示该位置会替换为搜索标签
	 *
	 */
	public static String replaceSearchKeyword(String indexUrl, String keyword) {
		// 关键字为空，则使用默认关键字
		if (TextUtil.isEmpty(keyword)) {
			Matcher keywordMatcher = PATTERN_CONTENT_KEYWORD.matcher(indexUrl);
			if (keywordMatcher.find())
				keyword = keywordMatcher.group();
		}
		return indexUrl.replaceAll(REGEX_PLACEHOLDER_KEYWORD, keyword);
	}
	
	/**
	 * 编码URL
	 *
	 */
	public static String encodeURL(String url) throws UnsupportedEncodingException {
		
		String[][] ESCAPES = new String[][] {
			{"&amp;", "&"},
			{" ", "%20"}
		};
		for (String[] escape : ESCAPES)
			url = url.replaceAll(escape[0], escape[1]);
		return url;
	}
	
	/**
	 * 通过Site找到当前Rule
	 *
	 */
	public static Section findCurrentRule(Site site, int mode, String extraKey) {
		Section section = null;
		switch (mode) {
			case Crawler.TYPE_HOME :
				section = site.getHomeSection();
				break;
			case Crawler.TYPE_SEARCH :
				section = site.getSearchSection();
				break;
			case Crawler.TYPE_EXTRA :
				if (site.getExtraSections() != null && !site.getExtraSections().isEmpty())
					section = site.getExtraSections().get(extraKey);
				break;
		}
		return section;
	}
	
	/**
	 * 内部Bean类
	 * 决定Crawler抓取模式以及相关参数
	 * 
	 */
	public static class Mode implements Serializable {
		public int type;
		public int pageCode;
		public String keyword;
		public String extraKey;
		public String extraData;

		public Mode(int type, int pageCode, String keyword, String extraKey, String extraData) {
			this.type = type;
			this.pageCode = pageCode;
			this.keyword = keyword;
			this.extraKey = extraKey;
			this.extraData = extraData;
		}
	}
}
