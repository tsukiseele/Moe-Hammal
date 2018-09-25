package com.tsukiseele.moehammal.bean;

import com.tsukiseele.moecrawler.core.Mappable;
import com.tsukiseele.moecrawler.utils.UniversalUtil;

public class Image extends Mappable {
	@Override
	public String getType() {
		return "IMAGE";
	}
	private String title;
	private String label;
	private String groupUrl;
	private String extraUrl;
	private String previewUrl;
	private String simpleUrl;
	private String compressUrl;
	private String fileUrl;
	private String datetime;

	public void setExtraUrl(String extraUrl) {
		this.extraUrl = extraUrl;
	}

	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}

	public String getGroupUrl() {
		return groupUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setSimpleUrl(String simpleUrl) {
		this.simpleUrl = simpleUrl;
	}

	public String getSimpleUrl() {
		return simpleUrl;
	}

	public void setCompressUrl(String compressUrl) {
		this.compressUrl = compressUrl;
	}

	public String getCompressUrl() {
		return compressUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String getCatalogUrl() {
		return groupUrl;
	}

	@Override
	public String getExtraUrl() {
		return extraUrl;
	}

	@Override
	public String toString() {
		return UniversalUtil.toString(this);
	}

	@Override
	public int hashCode() {
		return UniversalUtil.toString(this).hashCode();
	}
}
