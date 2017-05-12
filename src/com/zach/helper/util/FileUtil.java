package com.zach.helper.util;

import java.io.File;

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
		if (!templatePath.contains(File.separator)) {
			templatePath = templatePath.replace("/", File.separator);
		}
		String path = getProjectPath();
		path += templatePath;
		return path;
	}
}
