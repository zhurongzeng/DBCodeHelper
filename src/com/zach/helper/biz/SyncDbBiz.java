package com.zach.helper.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import com.zach.helper.entity.Column;
import com.zach.helper.entity.ConConfig;
import com.zach.helper.entity.Table;
import com.zach.helper.helper.MssqlDBHelper;
import com.zach.helper.window.MainWindow;

/**
 * ͬ�����ݿ⹤��
 * */
public class SyncDbBiz {
	/**
	 * ͬ�����ݿ�
	 * */
	public static void builderTable(MainWindow window, ConConfig config, Map<String, Table> excelTables) {
		for (String key : excelTables.keySet()) {
			Table tableExcel = excelTables.get(key);
			String pak = tableExcel.getPackName();
			if (pak == null || pak.length() == 0) {
				window.print("����ʧ��,��" + tableExcel.getTableName() + "ȱ�ٰ���!");
				return;
			}

			//�����ݿ���ȡ����,����
			Table tableDB = MssqlDBHelper.getTable(config, tableExcel.getTableName(), true);
			if (tableDB == null) {//���ݿ��в����ڴ˱�
				try {
					MssqlDBHelper.createTable(config, tableExcel);
					window.print("����ɹ�:" + tableExcel.getTableName());
					tableDB = MssqlDBHelper.getTable(config, tableExcel.getTableName(), true);
				} catch (Exception err) {
					window.print("����ʧ��:" + tableExcel.getTableName() + err.getMessage());
					return;//����ʧ��
				}
			}

			//�ҳ���Ҫ�޸ĵı������Ϣ
			if (!tableExcel.equals(tableDB)) {//���ű�һ��
				try {
					MssqlDBHelper.updateTable(config, tableExcel, tableDB.getId());//�޸ı������Ϣ
					window.print("�޸ı�ɹ�:" + tableExcel.getTableName());
				} catch (Exception err) {
					window.print("�޸ı�ʧ��:" + tableExcel.getTableName() + err.getMessage());
				}
			}

			//���Ƚϣ��ҳ���Ҫ����޸ĵ��ֶ�
			Map<String, Column> excelColumns = tableExcel.getColumns();
			Map<String, Column> dbColumns = tableDB.getColumns();
			for (String cn : excelColumns.keySet()) {
				String cnTemp = cn.toLowerCase().equals("id") ? "id" : cn;
				if (!dbColumns.containsKey(cnTemp)) {//���ݿ���û�д��ֶ�,�����
					try {
						MssqlDBHelper.createColumn(config, excelColumns.get(cn));
						window.print("����ֶγɹ�\t��:" + tableExcel.getTableName() + "\t��:" + cn);
					} catch (Exception err) {
						window.print("����ֶ�ʧ��:" + tableExcel.getTableName() + err.getMessage());
					}
				} else {
					try {
						boolean isOK = MssqlDBHelper.updateColumn(config, dbColumns.get(cnTemp), excelColumns.get(cn));
						if (isOK) {
							window.print("�޸��ֶγɹ�\t��:" + tableExcel.getTableName() + "\t��:" + cn);
						}
					} catch (Exception err) {
						window.print("�޸��ֶ�ʧ��:" + tableExcel.getTableName() + err.getMessage());
					}
				}
			}

			//�����Ƚϣ��ҵ����ݿ��д��ڣ���excel�����ڣ���ɾ���ֶΣ�
			for (String cn : dbColumns.keySet()) {
				if (!(excelColumns.containsKey(cn) || excelColumns.containsKey(cn.toUpperCase()))) {//excel�в��������ݿ��д��ڵ��ֶ�
					try {
						MssqlDBHelper.dropColumn(config, dbColumns.get(cn));
						window.print("ɾ���ֶγɹ�\t��:" + tableExcel.getTableName() + "\t��:" + cn);
					} catch (Exception err) {
						window.print("ɾ���ֶ�ʧ��:" + tableExcel.getTableName() + err.getMessage());
					}
				}
			}
		}
	}

	/**
	 * ���ɽ���ű�
	 * */
	public static void builderSql(MainWindow window, Map<String, Table> excelTables, String filePath) {
		StringBuffer sb = new StringBuffer();
		for (String key : excelTables.keySet()) {
			Table table = excelTables.get(key);
			sb.append("-------------create table " + table.getTableName() + " -------------" + MssqlDBHelper.NL);
			sb.append("create table ").append(MssqlDBHelper.LM).append(table.getTableName()).append(MssqlDBHelper.RM + MssqlDBHelper.NL);
			sb.append(MssqlDBHelper.LS + MssqlDBHelper.NL);

			int sum = 0;
			for (String ckey : table.getColumns().keySet()) {
				Column column = table.getColumns().get(ckey);
				sb.append(MssqlDBHelper.getCommonColumnSql(column));
				if (sum < table.getColumns().size() - 1) {
					sb.append(",");
				}
				sb.append(MssqlDBHelper.NL);
				sum++;
			}
			sb.append(MssqlDBHelper.RS + MssqlDBHelper.NL + "go" + MssqlDBHelper.NL + MssqlDBHelper.NL);
		}

		//���浽�ļ���
		File file = new File(filePath);
		if (!file.exists()) {//����ļ�������
			try {
				new File(file.getParent()).mkdirs();//�ȳ�������Ŀ¼
				file.createNewFile();//�������ļ�
			} catch (Exception err) {
				err.printStackTrace();
			}
		}

		//���������
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			bw.write(sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		window.print("����ű��ѱ�����:" + filePath);
	}
}
