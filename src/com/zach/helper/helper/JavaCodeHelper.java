package com.zach.helper.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zach.helper.entity.Attribute;
import com.zach.helper.util.FileUtil;
import com.zach.helper.util.StringUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * JAVA类生成
 */
public class JavaCodeHelper {

	private List<String> interfaces = new ArrayList<String>();

	private List<String> annotations = new ArrayList<String>();

	private String clzName;

	private String packageName;

	private List<String> packages = new ArrayList<String>();

	private List<Attribute> fields = new ArrayList<Attribute>();

	private String classDescription;

	boolean isLastMethod = true;

	public static final String NEWLINE = "\r\n";

	public static final String METHOD_NEWLINE = "\r\n\t";

	public static final String METHOD_BODY_NEWLINE = "\r\n\t\t";

	public JavaCodeHelper(String className) {
		String[] strArr = classNameSubPackage(className);
		if (strArr.length == 2) {
			this.packageName = strArr[0];
			clzName = strArr[1];
		} else {
			this.clzName = className;
		}
	}

	public JavaCodeHelper(String className, String description) {
		String[] strArr = classNameSubPackage(className);
		if (strArr.length == 2) {
			this.packageName = strArr[0];
			clzName = strArr[1];
		} else {
			this.clzName = className;
		}
		classDescription = description;
	}

	/**
	 * 添加注解
	 */
	public void addAnnotation(String annoco) {
		annotations.add(annoco);
	}

	/**
	 * 添加继承接口
	 */
	public void implementInterface(String interfaceName) {
		addImportPage(interfaceName);
		interfaces.add(classNameSubName(interfaceName));
	}

	/**
	 * 导入包
	 * 
	 * @param className
	 *            包名
	 */
	public void addImportPage(String className) {
		if (StringUtil.isNotEmpty(this.packageName)) {
			// 非java.lang和当前类的包，并还没有导入过
			if (className.indexOf("java.lang") == -1 && className.indexOf(this.packageName) == -1
					&& packages.indexOf(className) == -1) {
				packages.add(className);
			}
		} else {
			if (className.indexOf("java.lang") == -1 && className.indexOf(".") != -1
					&& packages.indexOf(className) == -1) {
				packages.add(className);
			}
		}
	}

	/**
	 * 添加字段
	 */
	public void addField(String description, String type, String fieldName) {
		fields.add(new Attribute(description, type, fieldName));
	}

	/**
	 * 将类全名拆成包含：包名+类名 的数组<br>
	 * 如果只有类名，则直接返回类名
	 */
	public static String[] classNameSubPackage(String className) {
		int index = className.lastIndexOf(".");
		if (index != -1) {
			String[] strArr = new String[2];
			strArr[0] = className.substring(0, index);
			strArr[1] = className.substring(index + 1);
			return strArr;
		}
		return new String[] { "", className };
	}

	/**
	 * 将类的包名过滤，只返回类名 的数组<br>
	 * 如果只有类名，则直接返回类名
	 */
	public static String classNameSubName(String className) {
		int index = className.lastIndexOf(".");
		if (index != -1) {
			return className.substring(index + 1);
		}
		return className;
	}

	/**
	 * 格式化类全路径名称 如:com.test.T_pserson改为com.test.PsersonInfo
	 * 
	 * @param className
	 * @return
	 */
	public static String getFullClassName(String className) {
		int index = className.lastIndexOf(".");
		if (index != -1) {
			String str1 = className.substring(0, index);
			String str2 = className.substring(index + 1);
			str2 = getClassName(str2);
			return str1 + "." + str2;
		} else {
			return getClassName(className);
		}
	}

	/**
	 * 类名标准化 如:T_pserson改为PsersonInfo
	 */
	public static String getClassName(String className) {
		if (className.indexOf("t_") == 0 || className.indexOf("T_") == 0) {
			className = className.substring(2);
		}
		String[] parts = className.split("_");
		String returnStr = "";
		for (int i = 0; i < parts.length; i++) {
			String str = parts[i];
			if (Character.isUpperCase(str.charAt(0))) {
				returnStr += str;
			} else {
				returnStr += (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1))
						.toString();
			}
		}

		if (returnStr.lastIndexOf("Info") < 1) {
			returnStr = returnStr + "Info";
		}

		return returnStr;
	}

	/**
	 * 类名标准化 如:T_DB_person改为PersonInfo
	 */
	public static String getClassName2(String className) {
		className = className.replaceFirst(className.substring(0, 1), className.substring(0, 1).toUpperCase());
		if (className.lastIndexOf("Info") < 1) {
			className = className + "Info";
		}
		return className;
	}

	/**
	 * 得到短类名 如:T_DB_person改为Person
	 */
	public static String getClassNameNotInfo(String className) {
		className = className.replaceAll("[t]?[T]?_[\\w]*_", "");
		className = className.replaceFirst(className.substring(0, 1), className.substring(0, 1).toUpperCase());
		return className;
	}

	/**
	 * 返回此类是否在项目中存在
	 */
	public boolean isExists() throws IOException {
		String path = FileUtil.getProjectPath();
		path += "\\src\\" + packageName.replace(".", "\\");
		path += "\\" + clzName + ".java";
		return new File(path).exists();
	}

	/**
	 * 使用freemarker模版生成model
	 * 
	 * @param templateName
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public String buildByTemplate(String templateName, String encoding) throws Exception {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setDirectoryForTemplateLoading(new File(FileUtil.getTemplatePath("/templates")));
		cfg.setDefaultEncoding(encoding);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		Template temp = cfg.getTemplate(templateName);

		Map<String, Object> dataOutMap = new HashMap<String, Object>();

		dataOutMap.put("packageName", packageName);
		dataOutMap.put("packageList", packages);
		dataOutMap.put("classDescription", classDescription);
		dataOutMap.put("className", clzName);
		dataOutMap.put("author", System.getenv().get("USERNAME"));
		dataOutMap.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		dataOutMap.put("fieldList", fields);

		File file = new File(new File(FileUtil.getProjectPath()) + File.separator + "src" + File.separator + "files");
		if (!file.exists()) {
			file.mkdirs();
		}
		OutputStream fos = new FileOutputStream(new File(file, clzName + ".java")); // java文件的生成目录
		Writer out = new OutputStreamWriter(fos, encoding);
		temp.process(dataOutMap, out);

		fos.flush();
		fos.close();

		return file.getPath() + File.separator + clzName + ".java";
	}
}
