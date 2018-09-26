package com.tsukiseele.moecrawler.utils;

public class TextUtil {
	public static boolean isEmpty(String text) {
		return null == text || text.trim().isEmpty();
	}
	public static int toInt(String text) {
		int num = 0;
		try {
			num = Integer.parseInt(text);
		} catch (Exception e) {}
		return num;
	}
} 
