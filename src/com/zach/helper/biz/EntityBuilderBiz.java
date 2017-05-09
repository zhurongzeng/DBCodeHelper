package com.zach.helper.biz;

import java.util.Map;

import com.zach.helper.entity.Column;
import com.zach.helper.entity.Table;
import com.zach.helper.helper.JavaCodeHelper;
import com.zach.helper.helper.TypeMapping;
import com.zach.helper.window.MainWindow;

/**
 * 实体类生成器
 */
public class EntityBuilderBiz {
	public static String builder(Map<String, Table> tableMap, String encoding) throws Exception {
		String filePath = "";
		for (String tableKey : tableMap.keySet()) {
			Table table = tableMap.get(tableKey);
			// 通过表名取得类
			String className = table.getEntityName();
			if (className != null && !"".equals(className)) {
				className = table.getPackName() + "." + className;
			} else {
				className = JavaCodeHelper.getFullClassName(table.getPackName() + "." + table.getTableName());
			}
			MainWindow.print("类名：" + className);
			// 初始化
			JavaCodeHelper javaCodeHelper = new JavaCodeHelper(className, table.getTableNameCH());
			// 其他属性
			for (String key : table.getColumns().keySet()) {
				Column c = table.getColumns().get(key);
				String fk = c.getForeignKey();
				if (fk != null && fk.length() > 0 && !"null".equals(fk)) {// 关联字段
					MainWindow.print("外键：" + fk);
					fk = fk.replace("[", "");
					String fks[] = fk.split("]");
					String fullType = JavaCodeHelper.getFullClassName(fks[0]);// 类型
					javaCodeHelper.addImportPage(fullType);// 包入类型包
					String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
					javaCodeHelper.addField(c.getFieldNameCH(), type, c.getFieldName());// 字段
				} else {// 普通字段
					String fullType = TypeMapping.sqlToJava(c.getFieldType());
					MainWindow.print("字段类型转换：[" + c.getFieldName() + "]\b" + c.getFieldType() + "-->" + fullType);
					String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
					javaCodeHelper.addImportPage(fullType);// 导入类型包
					javaCodeHelper.addField(c.getFieldNameCH(), type, c.getFieldName());// 字段
				}
			}
			filePath = javaCodeHelper.buildByTemplate(encoding);// 生成实体
			MainWindow.print("实体类生成成功！文件路径：" + filePath);
		}
		return filePath;
	}
}
