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
 * 同步数据库工类
 * */
public class SyncDbBiz {
	/**
	 * 同步数据库
	 * */
	public static void builderTable(MainWindow window, ConConfig config, Map<String, Table> excelTables) {
		for (String key : excelTables.keySet()) {
			Table tableExcel = excelTables.get(key);
			String pak = tableExcel.getPackName();
			if (pak == null || pak.length() == 0) {
				window.print("发布失败,表" + tableExcel.getTableName() + "缺少包名!");
				return;
			}

			//从数据库中取出表,括列
			Table tableDB = MssqlDBHelper.getTable(config, tableExcel.getTableName(), true);
			if (tableDB == null) {//数据库中不存在此表
				try {
					MssqlDBHelper.createTable(config, tableExcel);
					window.print("建表成功:" + tableExcel.getTableName());
					tableDB = MssqlDBHelper.getTable(config, tableExcel.getTableName(), true);
				} catch (Exception err) {
					window.print("建表失败:" + tableExcel.getTableName() + err.getMessage());
					return;//建表失败
				}
			}

			//找出需要修改的表基本信息
			if (!tableExcel.equals(tableDB)) {//两张表不一样
				try {
					MssqlDBHelper.updateTable(config, tableExcel, tableDB.getId());//修改表基本信息
					window.print("修改表成功:" + tableExcel.getTableName());
				} catch (Exception err) {
					window.print("修改表失败:" + tableExcel.getTableName() + err.getMessage());
				}
			}

			//表表比较，找出需要添加修改的字段
			Map<String, Column> excelColumns = tableExcel.getColumns();
			Map<String, Column> dbColumns = tableDB.getColumns();
			for (String cn : excelColumns.keySet()) {
				String cnTemp = cn.toLowerCase().equals("id") ? "id" : cn;
				if (!dbColumns.containsKey(cnTemp)) {//数据库中没有此字段,则添加
					try {
						MssqlDBHelper.createColumn(config, excelColumns.get(cn));
						window.print("添加字段成功\t表:" + tableExcel.getTableName() + "\t列:" + cn);
					} catch (Exception err) {
						window.print("添加字段失败:" + tableExcel.getTableName() + err.getMessage());
					}
				} else {
					try {
						boolean isOK = MssqlDBHelper.updateColumn(config, dbColumns.get(cnTemp), excelColumns.get(cn));
						if (isOK) {
							window.print("修改字段成功\t表:" + tableExcel.getTableName() + "\t列:" + cn);
						}
					} catch (Exception err) {
						window.print("修改字段失败:" + tableExcel.getTableName() + err.getMessage());
					}
				}
			}

			//表与表比较，找到数据库中存在，但excel不存在（即删除字段）
			for (String cn : dbColumns.keySet()) {
				if (!(excelColumns.containsKey(cn) || excelColumns.containsKey(cn.toUpperCase()))) {//excel中不存在数据库中存在的字段
					try {
						MssqlDBHelper.dropColumn(config, dbColumns.get(cn));
						window.print("删除字段成功\t表:" + tableExcel.getTableName() + "\t列:" + cn);
					} catch (Exception err) {
						window.print("删除字段失败:" + tableExcel.getTableName() + err.getMessage());
					}
				}
			}
		}
	}

	/**
	 * 生成建表脚本
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

		//保存到文件中
		File file = new File(filePath);
		if (!file.exists()) {//如果文件不存在
			try {
				new File(file.getParent()).mkdirs();//先尝试生成目录
				file.createNewFile();//现生成文件
			} catch (Exception err) {
				err.printStackTrace();
			}
		}

		//输出到流中
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			bw.write(sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		window.print("建表脚本已保存至:" + filePath);
	}
}
