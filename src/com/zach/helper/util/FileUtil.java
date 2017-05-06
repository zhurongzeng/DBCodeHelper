package com.zach.helper.util;

public class FileUtil {
	/**
	 * �õ���Ŀ��Ŀ¼
	 */
	public static String getProjectPath() {
		return System.getProperty("user.dir");
	}

	/**
	 * �õ�ģ��·��
	 */
	public static String getTemplatePath(String templatePath) {
		String path = getProjectPath();
		path += templatePath;
		return path;
	}
}
