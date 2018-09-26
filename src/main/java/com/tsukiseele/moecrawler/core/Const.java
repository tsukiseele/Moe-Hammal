package com.tsukiseele.moecrawler.core;

import java.util.regex.Pattern;

public class Const {
	public static final Pattern REGEX_SELECTOR = Pattern.compile("(?<=#\\().*(?=\\)\\.)");
	public static final Pattern REGEX_FUN = Pattern.compile("(?<=\\.)(text|html|attr)");
	public static final Pattern REGEX_ATTR = Pattern.compile("(?<=attr\\().*?(?=\\))");
	public static final Pattern REGEX_CONTENT_PAGE = Pattern.compile("(?<=\\{page:)(-?\\d*)?,?(-?\\d*)?(?=\\})");
	public static final Pattern REGEX_CONTENT_KEYWORD = Pattern.compile("(?<=\\{keyword:).*(?=\\})");
	public static final String REGEX_PLACEHOLDER_PAGE = "\\{page:.*?\\}";
	public static final String REGEX_PLACEHOLDER_KEYWORD = "\\{keyword:.*?\\}";
}
