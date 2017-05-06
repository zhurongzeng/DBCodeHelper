package com.zach.helper.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Table {
	private int id;// id
	private String packName;// 包名
	private String entityName;// 实体类名
	private String tableName;// 表名
	private String tableNameCH;// 中文表名
	private String description;// 备注

	private Map<String, Column> columns = new LinkedHashMap<String, Column>();// 表字段

	public Table() {
	}

	public Table(String name) {
		this.tableName = name;
	}

	public Table(String tableName, String packName, String className, String tableNameCH, String description) {
		super();
		this.tableName = tableName;
		this.packName = packName;
		this.entityName = className;
		this.tableNameCH = tableNameCH;
		this.description = description;
	}

	public Table(int id, String packName, String tableName, String tableNameCH, String description) {
		super();
		this.id = id;
		this.packName = packName;
		this.tableName = tableName;
		this.tableNameCH = tableNameCH;
		this.description = description;
	}

	public void addColumn(String name, Column column) {
		this.columns.put(name, column);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Column> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, Column> columns) {
		this.columns = columns;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTableNameCH() {
		return tableNameCH;
	}

	public void setTableNameCH(String tableNameCH) {
		this.tableNameCH = tableNameCH;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().toString().equals(this.getClass().toString())) {
			return false;
		}
		Table t = (Table) obj;
		if (t.getPackName().equals(this.getPackName()) && t.getTableName().equals(this.tableName)
				&& t.getTableNameCH().equals(this.tableNameCH) && t.getDescription().equals(this.description)) {
			return true;
		}
		return false;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
