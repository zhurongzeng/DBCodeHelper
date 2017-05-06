package com.zach.helper.helper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zach.helper.entity.Column;
import com.zach.helper.entity.Table;
import com.zach.helper.window.MainWindow;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Excel������<br>
 * �����õ����б��ֶ�<br>
 */
public class ExcelHelper {

	/**
	 * ��Excel�еõ���
	 * 
	 * @param file
	 *            �ļ�
	 * @param readColumn
	 *            �Ƿ��ȡ����Ϣ
	 */
	public static Map<String, Table> getAllTables(File file, boolean readColumn) throws BiffException, IOException {
		Map<String, Table> tables = new LinkedHashMap<String, Table>();
		// ����Workbook��������������
		Workbook wb = Workbook.getWorkbook(file);
		// ��ȡ������
		Sheet[] sheet = wb.getSheets();
		// ��ȡ��һ�ű�
		Sheet firSheet = sheet[0];
		for (int i = 1; i < firSheet.getRows(); i++) {// �ӵ�2�ſ�ʼ
			MainWindow.print($(firSheet.getRow(i), 0) + " " + $(firSheet.getRow(i), 3));
			// ����һ�ű�
			Table table = new Table($(firSheet.getRow(i), 0), $(firSheet.getRow(i), 1), $(firSheet.getRow(i), 2),
					$(firSheet.getRow(i), 3), $(firSheet.getRow(i), 4));
			// ��Ҫ��������Ϣ
			if (readColumn) {
				for (int j = 1; j < sheet.length; j++) {// �ҳ���Ӧ�ı�
					String tableName = $(sheet[j].getRow(1), 0);// �õ�����
					if (table.getTableName().equals(tableName)) {// �ҵ���Ӧ�ı�󣬶�ȡ����Ϣ
						table.setColumns(getColumnsByTable(sheet[j]));
						break;
					}
				}
			}
			tables.put(table.getTableName(), table);
		}
		wb.close();// ���ر���Դ���ͷ��ڴ�
		return tables;
	}

	/**
	 * �õ�ĳ��������ֶ�
	 * 
	 * @param tableName
	 *            ����
	 */
	public static Map<String, Column> getColumnsByTable(File file, String tableName) throws BiffException, IOException {
		Map<String, Column> columns = null;
		// ����Workbook����
		Workbook wb = Workbook.getWorkbook(file);
		// ��ȡ������
		Sheet[] sheet = wb.getSheets();
		// ��ÿ�����������ѭ��,�ҳ���Ӧ��
		for (int i = 1; i < sheet.length; i++) {
			String tn = $(sheet[i].getRow(1), 0);// ����
			if (tableName.equals(tn)) {// �ҵ���Ӧ�ı�
				columns = getColumnsByTable(sheet[i]);
			}
		}
		return columns;
	}

	/**
	 * ��excel��sheet��ÿһ�з�װΪColumn
	 */
	private static Map<String, Column> getColumnsByTable(Sheet sheet) {
		Map<String, Column> columns = new LinkedHashMap<String, Column>();
		for (int j = 1; j < sheet.getRows(); j++) {
			Cell[] cells = sheet.getRow(j);
			Column column = new Column();
			column.setTableName($(cells, 0));// ����
			column.setFieldName(textTransform($(cells, 1)));// �ֶ���
			column.setFieldNameCH($(cells, 2));// �����ֶ���
			column.setForeignKey($(cells, 4));// ���
			column.setDesc($(cells, 5));// ��ע

			// �Ƿ�Ϊ����(Ĭ��idΪ����)
			if ("id".equals(column.getFieldName().toLowerCase())) {
				column.setPrimaryKey(true);
			} else {
				column.setPrimaryKey(false);
			}

			// ������������
			String type = $(cells, 3);// ���ͺͳ���// varchar(10,3)
			if (type.indexOf("(") == -1) {// û�г��ȵ�����
				column.setHasLength(false);
				column.setFieldType(type);// ����
			} else {// �г��ȵ�����
				column.setHasLength(true);
				int beginIndex = type.indexOf("(");
				int endIndex = type.indexOf(")");
				String typeTemp = type.substring(0, beginIndex);// ����
				String typeLengthTemp = type.substring(beginIndex + 1, endIndex);// ����
				int dianIndex = typeLengthTemp.indexOf(",");
				if (dianIndex == -1) {// һλ����
					column.setHasPrecision(false);// �޾���
					column.setFieldLength(Integer.parseInt(typeLengthTemp.trim()));
				} else {// ��λ����
					column.setHasPrecision(true);// �о���
					column.setFieldLength(Integer.parseInt(typeLengthTemp.substring(0, dianIndex).trim()));
					column.setPrecision(Integer.parseInt(typeLengthTemp.substring(dianIndex + 1).trim()));
				}
				column.setFieldType(typeTemp);
			}
			columns.put(column.getFieldName(), column);
		}
		return columns;
	}

	/**
	 * ���ݱ����õ�ĳһ�ű�
	 * 
	 * @param tableName
	 *            ����
	 * @param readColumn
	 *            �Ƿ���Ҫ��ȡ����Ϣ
	 */
	public static Table getTable(File file, String tableName, boolean readColumn) throws BiffException, IOException {
		Table table = null;
		// ����Workbook����
		Workbook wb = Workbook.getWorkbook(file);
		// ��ȡ������
		Sheet[] sheet = wb.getSheets();
		// ��ȡ��һ�ű�
		Sheet firSheet = sheet[0];
		for (int i = 1; i < firSheet.getRows(); i++) {
			String tn = $(firSheet.getRow(i), 2);// ����
			// �ҵ���Ӧ�ı�
			if (tableName.equals(tn)) {
				table = new Table($(firSheet.getRow(i), 0), $(firSheet.getRow(i), 1), $(firSheet.getRow(i), 2),
						$(firSheet.getRow(i), 3), $(firSheet.getRow(i), 4));
				if (readColumn) {
					table.setColumns(getColumnsByTable(file, tableName));
				}
				break;
			}
		}
		wb.close();// ���ر���Դ���ͷ��ڴ�
		return table;
	}

	/**
	 * ��Cell[]�õ���index��cell������<br>
	 * ���index>=Cell.length()����cell[index]Ϊ��,�򷵻�""
	 */
	private static String $(Cell[] cs, int index) {
		if (index >= cs.length) {
			return "";
		}
		if (cs[index] == null) {
			return "";
		}
		return cs[index].getContents();
	}

	/**
	 * �ֶ����ƴ���
	 * 
	 * @param text
	 * @return
	 */
	private static String textTransform(String text) {
		if (text != null && !"".equals(text)) {
			String[] parts = text.split("_");
			String returnStr = parts[0];
			for (int i = 1; i < parts.length; i++) {
				String str = parts[i];
				if (Character.isUpperCase(str.charAt(0))) {
					returnStr += str;
				} else {
					returnStr += (new StringBuilder()).append(Character.toUpperCase(str.charAt(0)))
							.append(str.substring(1)).toString();
				}
			}
			return returnStr;
		}
		return text;
	}
}
