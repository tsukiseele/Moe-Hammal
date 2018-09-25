package com.tsukiseele.moecrawler;

import com.tsukiseele.moecrawler.core.Crawler;
import com.tsukiseele.moecrawler.bean.Section;
import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moecrawler.utils.TextUtil;

import java.io.FileOutputStream;
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
		return super.request(url);
	}
	
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
