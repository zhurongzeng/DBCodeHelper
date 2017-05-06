package com.zach.helper.helper;

import java.util.Arrays;
import java.util.List;

/**
 * 类型映射关系
 */
public class TypeMapping {
	public static String sqlToJava(String type) {
		if (type == null) {
			return null;
		}
		List<String> intType = Arrays.asList(new String[] { "int", "tinyint", "smallint" });
		List<String> longType = Arrays.asList(new String[] { "bigint" });
		List<String> doubleType = Arrays.asList(new String[] { "float" });
		List<String> floatType = Arrays.asList(new String[] { "real" });
		List<String> stringType = Arrays.asList(new String[] { "varchar", "char", "nchar", "nvarchar", "text", "ntext",
				"uniqueidentifier", "sql_variant" });
		List<String> decimalType = Arrays.asList(new String[] { "decimal", "money", "smallmoney", "numeric" });
		List<String> dateType = Arrays.asList(new String[] { "smalldatetime", "datetime", "date" });
		List<String> booleanType = Arrays.asList(new String[] { "bit" });
		type = type.toLowerCase();// 变成小写
		if (intType.contains(type)) {
			return "java.lang.Integer";
		} else if (stringType.contains(type)) {
			return "java.lang.String";
		} else if (dateType.contains(type)) {
			return "java.lang.Date";
		} else if (decimalType.contains(type)) {
			return "java.math.BigDecimal";
		} else if (booleanType.contains(type)) {
			return "java.lang.Boolean";
		} else if (longType.contains(type)) {
			return "java.lang.Long";
		} else if (doubleType.contains(type)) {
			return "java.lang.Double";
		} else if (floatType.contains(type)) {
			return "java.lang.Float";
		}
		return null;
	}

}
