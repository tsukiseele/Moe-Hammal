package com.tsukiseele.moecrawler.core;

import com.tsukiseele.moecrawler.bean.Catalog;
import com.tsukiseele.moecrawler.bean.Gallery;
import com.tsukiseele.moecrawler.bean.Section;
import com.tsukiseele.moecrawler.bean.Selector;
import com.tsukiseele.moecrawler.utils.LogUtil;
import com.tsukiseele.moecrawler.utils.TextUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 核心类，解析文档数据
 *
 *
 */
public class MoeParser<T extends Mappable> {

	private Crawler crawler;
	private Class<T> type;

	public interface MetaDataParseCallback<T extends Mappable> {
		void onCatalogSuccess(Catalog<T> datas);
		void onItemSuccess(T data);
	}

	public MoeParser(Crawler crawler, Class<T> type) {
		this.crawler = crawler;
		this.type = type;
	}

	/*
	 * 深度解析规则，返回数据组集合，该操作极其耗时
	 *
	 */
	public Gallery<?> parseAll(MetaDataParseCallback callback) throws IOException {
		Gallery<Catalog<T>> dataSet = new Gallery<>(new ArrayList<>(), crawler);
		Gallery<T> datas = parseGallery();
		if (datas != null && !datas.isEmpty()) {
			//具有目录结构
			boolean isCatalog = false;
			for (T data : datas) {
				if (data.hasCatalog()) {
					Catalog<T> catalog = parseCatalog(data);
					for (T dat : catalog) {
						if (dat.hasExtra()) parseFillExtra(dat);
					}
					callback.onCatalogSuccess(catalog);
					dataSet.add(catalog);
					isCatalog = true;
				} else {
					callback.onItemSuccess(data);
				}
			}
			if (!isCatalog) {
				return datas;
			}
		}
		return dataSet;
	}
	
	public Gallery<T> parseGallery() throws IOException {
		String html = crawler.execute();

		Map<String, Selector> gallerySelectors = crawler.getSection().getGallerySelectors();

		LogUtil.i(MoeParser.class.getCanonicalName(), gallerySelectors.toString());
		List<T> datas = parseHtmlDocument(Jsoup.parse(html), gallerySelectors, type);
		
		return new Gallery<T>(datas, crawler.getSection(), crawler.getMode().pageCode, crawler.getMode().extraKey);
	}
	/*
	 * 用于解析首页
	 *
	 */
	public Gallery<T> parseGallery(int pageCode) throws IOException {
		crawler.getMode().pageCode = pageCode;
		return parseGallery();
	}

	/*
	 * 用于解析首页
	 * 依据历史内容解析下一页
	 */
	/*
	public Gallery<T> parseNextGallery(Gallery<T> gallery) throws IOException {
		// 与传入的页码同步
		crawler.getMode().pageCode = ++gallery.pageCode;
		return parseGallery();
	}

	public Gallery<T> parseNextGallery() throws IOException {
		++crawler.getMode().pageCode;
		String html = crawler.execute();
		Map<String, Selector> gallerySelectors = crawler.getSection().getGallerySelectors();

		LogUtil.i(MoeParser.class.getCanonicalName(), gallerySelectors.toString());
		List<T> datas = parseHtmlDocument(Jsoup.parse(html), gallerySelectors, type);

		return new Gallery<T>(datas, crawler.getSection(), crawler.getMode().pageCode, crawler.getMode().extraKey);
	}
	*/
	/**
	 * 解析目录内容，包含所有页面
	 *
	 *
	 */
	public Catalog<T> parseCatalog(T map) throws IOException {
		Section section = crawler.getSection();
		Catalog<T> catalog = new Catalog<>(section, map);
		Map<String, Selector> catalogSelectors = section.getCatalogSelectors();
		// 匹配正则代表存在多页
		if (map.getCatalogUrl().matches(Const.REGEX_CONTENT_PAGE.toString())) {
			// flag保存上一页的数据，用于检测是否重复爬取
			T flag = null;
			while (true) {
				List<T> dats = null;
				String mUrl = Crawler.replacePageCode(map.getCatalogUrl(), catalog.pageCode++);
				String html = crawler.request(mUrl);
				dats = parseHtmlDocument(Jsoup.parse(html), catalogSelectors, type);
				// 数据集为空直接返回
				if (dats != null && dats.size() > 0) {
					if (flag == null || dats.get(0).hashCode() != flag.hashCode()) {
						catalog.addAll(dats);
						flag = dats.get(0);
					} else break;
				} else break;
			}
		} else {
			// 不存在多页
			String html = crawler.request(map.getCatalogUrl());
			catalog.addAll(parseHtmlDocument(Jsoup.parse(html), catalogSelectors, type));
		}
		// 填充所有内容
		catalog.metadata.fillToAll(catalog);
		return catalog;
	}
	/**
	 * 解析额外规则
	 *
	 */
	public void parseFillExtra(T map) throws IOException {
		if (map.hasExtra()) {
			Map<String, Selector> extraSelectors = crawler.getSection().getExtraSelectors();
			String html = crawler.request(map.getExtraUrl());
			parseHtmlDocument(Jsoup.parse(html), extraSelectors, type).get(0).fillTo(map);
		}
	}
	
	/*
	 * @parem doc 需要解析的Html文档
	 * @param selectors 选择器组
	 * @param type 生成的数据类型
	 *
	 * @return type类型的数据容器
	 */
	private <T extends Mappable> List<T> parseHtmlDocument(Document doc, Map<String, Selector> selectors, Class<T> type) {
		Map<String, String[]> dataGroup = new HashMap<>();
		LogUtil.i("Selectors", selectors.toString());
		int length = 0;
		for (String key : selectors.keySet()) {
			Selector selector = selectors.get(key);
			String[] strings = parseHtmlElement(doc, selector);
			if (strings != null) {
				LogUtil.i("parseHtmlDocument", Arrays.toString(strings));
				dataGroup.put(key, strings);
				// 用于判断数据列表的最大长度
				if (length < strings.length) length = strings.length;
			}
		}
		// 构造对象组
		Set<String> dataGroupKeySet = dataGroup.keySet();
		List<T> datas = new ArrayList<>();
		try {
			for (int i = 0; i < length; i++) {
				T data = type.newInstance();
				for (String key : dataGroupKeySet) {
					String[] strings = dataGroup.get(key);
					try {
						Field field = type.getDeclaredField(key);
						field.setAccessible(true);
						if (i < strings.length)
							field.set(data, strings[i]);
					} catch (NoSuchFieldException e) {
						continue;
					}
				}
				data.setType(crawler.getSite().getType());
				datas.add(data);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return datas;
	}
	/**
	 * @param doc HTML文档
	 * @param selector 选择器
	 *
	 * @return String[] 选择到的内容
	 *
	 */
	private static String[] parseHtmlElement(Document doc, Selector selector) {
		if (selector == null)
			throw new NullPointerException("selector is empty");
		// 数据容器
		List<String> datas = new ArrayList<String>();

		// 利用选择器获取
		if (!TextUtil.isEmpty(selector.selector)) {
			selector.init();
			Elements es = doc.select(selector.selector);
			for (int i = 0; i < es.size(); i++) {
				Element e = es.get(i);
				LogUtil.i("parseHtmlElement", e.toString());

				String data = null;
				if (selector.fun != null) {
					switch (selector.fun) {
						case "attr" :
							data = e.attr(selector.attr);
							break;
						case "html" :
							data = e.toString();
							break;
						case "text" :
							data = e.text();
							break;
						default :
							data = e.toString();
					}
				} else {
					data = e.toString();
				}
				data = replectContent(data, selector.capture, selector.replacement);

				if (!TextUtil.isEmpty(data))
					datas.add(data);
			}
			// 利用正则获取
		} else if (!TextUtil.isEmpty(selector.regex)) {
			Pattern pattern = Pattern.compile(selector.regex, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(doc.toString());
			while (matcher.find()) {
				String data = replectContent(matcher.group(), selector.capture, selector.replacement);

				if (!TextUtil.isEmpty(data))
					datas.add(data);
			}
		}
		return datas.toArray(new String[datas.size()]);
	}
	/**
	 * @param text 待处理的文本
	 * @param captureRegex 截取正则式
	 * @param replaceRegex 替换式
	 *
	 * @return 替换后的文本
	 *
	 * @remark
	 *	匹配式或替换式为空，返回原文本
	 * 	匹配式或替换式非空，则替换内容，返回替换后的替换式
	 * 		未匹配到内容或其他原因，返回空串
	 */
	private static String replectContent(String text, String captureRegex, String replaceRegex) {
		if (captureRegex == null || "".equals(captureRegex.trim()) ||
			replaceRegex == null || "".equals(replaceRegex.trim())) {
			return text;
		}
		Matcher matcher = null;
		List<String> groups = new ArrayList<>();
		List<Integer> indexs = new ArrayList<>();

		matcher = Pattern.compile(captureRegex).matcher(text);

		// 提取捕获组
		if (matcher.find()) {
			// group[0] 为整个匹配组
			for (int i = 0; i <= matcher.groupCount(); i++)
				groups.add(matcher.group(i));

			// 提取替换式索引
			matcher = Pattern.compile("(?<=\\$)\\d").matcher(replaceRegex);
			while (matcher.find())
				indexs.add(Integer.valueOf(matcher.group()));

			// 根据索引替换内容
			for (int index : indexs)
				if (index >= 0 && index < groups.size())
					replaceRegex = replaceRegex.replaceAll("\\$" + index, groups.get(index));
			return replaceRegex;
		}
		return "";
	}
}
