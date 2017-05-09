package com.zach.helper.biz;

import java.util.Map;

import com.zach.helper.entity.Column;
import com.zach.helper.entity.Table;
import com.zach.helper.helper.JavaCodeHelper;
import com.zach.helper.helper.TypeMapping;
import com.zach.helper.window.MainWindow;

/**
 * ʵ����������
 */
public class EntityBuilderBiz {
	public static String builder(Map<String, Table> tableMap, String encoding) throws Exception {
		String filePath = "";
		for (String tableKey : tableMap.keySet()) {
			Table table = tableMap.get(tableKey);
			// ͨ������ȡ����
			String className = table.getEntityName();
			if (className != null && !"".equals(className)) {
				className = table.getPackName() + "." + className;
			} else {
				className = JavaCodeHelper.getFullClassName(table.getPackName() + "." + table.getTableName());
			}
			MainWindow.print("������" + className);
			// ��ʼ��
			JavaCodeHelper javaCodeHelper = new JavaCodeHelper(className, table.getTableNameCH());
			// ��������
			for (String key : table.getColumns().keySet()) {
				Column c = table.getColumns().get(key);
				String fk = c.getForeignKey();
				if (fk != null && fk.length() > 0 && !"null".equals(fk)) {// �����ֶ�
					MainWindow.print("�����" + fk);
					fk = fk.replace("[", "");
					String fks[] = fk.split("]");
					String fullType = JavaCodeHelper.getFullClassName(fks[0]);// ����
					javaCodeHelper.addImportPage(fullType);// �������Ͱ�
					String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
					javaCodeHelper.addField(c.getFieldNameCH(), type, c.getFieldName());// �ֶ�
				} else {// ��ͨ�ֶ�
					String fullType = TypeMapping.sqlToJava(c.getFieldType());
					MainWindow.print("�ֶ�����ת����[" + c.getFieldName() + "]\b" + c.getFieldType() + "-->" + fullType);
					String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
					javaCodeHelper.addImportPage(fullType);// �������Ͱ�
					javaCodeHelper.addField(c.getFieldNameCH(), type, c.getFieldName());// �ֶ�
				}
			}
			filePath = javaCodeHelper.buildByTemplate(encoding);// ����ʵ��
			MainWindow.print("ʵ�������ɳɹ����ļ�·����" + filePath);
		}
		return filePath;
	}
}
