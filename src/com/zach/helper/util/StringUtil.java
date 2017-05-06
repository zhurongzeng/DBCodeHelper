package com.zach.helper.util;

public class StringUtil {
	/**
	 * ��֤�ַ����Ƿ�ǿ�
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}

	/**
	 * �ַ����׸���ĸ��д
	 */
	public static String firstToUpperCase(String str) {
		char trimChars[] = str.toCharArray();
		trimChars[0] = Character.toUpperCase(trimChars[0]);
		return new String(trimChars);
	}

	/**
	 * �ַ����׸���ĸСд
	 */
	public static String firstToLowerCase(String str) {
		char trimChars[] = str.toCharArray();
		trimChars[0] = Character.toLowerCase(trimChars[0]);
		return new String(trimChars);
	}
}
