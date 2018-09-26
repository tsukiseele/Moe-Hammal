package com.tsukiseele.moecrawler.bean;

import com.tsukiseele.moecrawler.core.Mappable;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

// 保存目录数据和状态
public class Catalog<T extends Mappable> extends ArrayList<T> implements Serializable {
	// 源数据
	public T metadata;
	// 源Section
	public Section section;
	// 目录当前页码
	public int pageCode = 0;

	public Catalog(List<T> datas, Section section, T metadata) {
		super(datas);
		this.section = section;
		this.metadata = metadata;
	}
	
	public Catalog(Section section, T metadata) {
		super();
		this.section = section;
		this.metadata = metadata;
	}
	
	public boolean isSingle() {
		return size() == 1;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
