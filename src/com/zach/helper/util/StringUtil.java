package com.zach.helper.util;

public class StringUtil {
	/**
	 * 验证字符串是否非空
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}

	/**
	 * 字符串首个字母大写
	 */
	public static String firstToUpperCase(String str) {
		char trimChars[] = str.toCharArray();
		trimChars[0] = Character.toUpperCase(trimChars[0]);
		return new String(trimChars);
	}

	/**
	 * 字符串首个字母小写
	 */
	public static String firstToLowerCase(String str) {
		char trimChars[] = str.toCharArray();
		trimChars[0] = Character.toLowerCase(trimChars[0]);
		return new String(trimChars);
	}
}
