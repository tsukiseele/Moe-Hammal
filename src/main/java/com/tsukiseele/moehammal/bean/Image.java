package com.tsukiseele.moehammal.bean;

import com.tsukiseele.moecrawler.core.Mappable;
import com.tsukiseele.moecrawler.utils.TextUtil;
import com.tsukiseele.moecrawler.utils.UniversalUtil;

public class Image extends Mappable {
	public static final String URL_SIMPLE = "simple";
	public static final String URL_LARGER = "larger";
	public static final String URL_ORIGIN = "origin";

	private String title;
	private String tags;
	private String catalogUrl;
	private String extraUrl;
	private String coverUrl;
	private String simpleUrl;
	private String largerUrl;
	private String originUrl;
	private String datetime;

	@Override
	public String getType() {
		return "IMAGE";
	}

	public void setExtraUrl(String extraUrl) {
		this.extraUrl = extraUrl;
	}

	public void setCatalogUrl(String catalogUrl) {
		this.catalogUrl = catalogUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setSimpleUrl(String simpleUrl) {
		this.simpleUrl = simpleUrl;
	}

	public String getSimpleUrl() {
		return simpleUrl;
	}

	public void setLargerUrl(String largerUrl) {
		this.largerUrl = largerUrl;
	}

	public String getLargerUrl() {
		return largerUrl;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	public String getOriginUrl() {
		return originUrl;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}
	
	public String getTitle() {
		return title;
	}

	public String getLowUrl() {
		return TextUtil.nonEmpty(getSimpleUrl()) ? getSimpleUrl() : TextUtil.nonEmpty(getLargerUrl()) ? getLargerUrl() : getOriginUrl();
	}

	public String getHighUrl() {
		return TextUtil.nonEmpty(getOriginUrl()) ? getOriginUrl() : TextUtil.nonEmpty(getLargerUrl()) ? getLargerUrl() : getSimpleUrl();
	}

	public String getUrl(String flag) {
		switch (flag) {
			case URL_SIMPLE :
				return getLowUrl();
			case URL_LARGER:
				return largerUrl == null ? getOriginUrl() : largerUrl;
			default :
				return getOriginUrl();
		}
	}

	@Override
	public String getCatalogUrl() {
		return catalogUrl;
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
