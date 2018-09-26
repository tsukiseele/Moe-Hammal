package com.tsukiseele.moecrawler.bean;

import com.tsukiseele.moecrawler.core.Const;
import com.tsukiseele.moecrawler.utils.RegexUtil;
import com.tsukiseele.moecrawler.utils.TextUtil;
import com.tsukiseele.moecrawler.utils.UniversalUtil;

import java.util.regex.Matcher;

import java.io.Serializable;

/**
 * 网站的规则选择器
 *
 */
public class Selector implements Serializable {
	public String selector;
	public String fun;
	public String attr;
	public String regex;
	public String capture;
	public String replacement;

	public void init() {
		if (TextUtil.isEmpty(fun)) {
			String selector = null;
			String fun = null;
			String attr = null;
			// 提取选择器
			selector = RegexUtil.matchesText(this.selector, Const.REGEX_SELECTOR);
			// 提取选择方法
			Matcher matcher = Const.REGEX_FUN.matcher(this.selector);
			if (matcher.find()) {
				switch (matcher.group().trim()) {
					case "attr" :
						fun = "attr";
						attr = RegexUtil.matchesText(this.selector, Const.REGEX_ATTR);
						break;
					case "html" :
						fun = "html";
						break;
					case "text" :
						fun = "text";
						break;
				}
			}
			this.selector = selector;
			this.fun = fun;
			this.attr = attr;
		}
	}
	
	@Override
	public String toString() {
		return UniversalUtil.toString(this);
	}
}
