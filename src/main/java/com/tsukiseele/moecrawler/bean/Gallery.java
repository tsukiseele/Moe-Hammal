package com.tsukiseele.moecrawler.bean;

import com.tsukiseele.moecrawler.core.Crawler;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

// 保存画廊数据和状态
public class Gallery<T> extends ArrayList<T> implements Serializable {
	// 源Section
	public Section section;
	// 当前页码
	public int pageCode;
	// 当前Tags
	public String tags;

	public Gallery(List<T> datas, Section section, int pageCode, String keyword) {
		super(datas);
		this.section = section;
		this.pageCode = pageCode;
		this.tags = keyword;
	}

	public Gallery(List<T> datas, Crawler crawler) {
		this(datas, crawler.getSection(), crawler.getMode().pageCode, crawler.getMode().keyword);
	}
	
	public Gallery(List<T> datas, Section section) {
		this(datas, section, 0, null);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
