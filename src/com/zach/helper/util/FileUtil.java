package com.zach.helper.util;

public class FileUtil {
	/**
	 * 得到项目根目录
	 */
	public static String getProjectPath() {
		return System.getProperty("user.dir");
	}

	/**
	 * 得到模版路径
	 */
	public static String getTemplatePath(String templatePath) {
		String path = getProjectPath();
		path += templatePath;
		return path;
	}
}
