package com.zach.helper.entity;

public class Column {
	private int id;// id
	private String tableName;// 表名
	private String fieldName;// 字段名
	private String fieldNameCH;// 字段中文名
	private String fieldType;// 类型
	private int fieldLength;// 长度
	private int precision;// 精度
	private String defaultValue;// 默认值
	private boolean hasLength;// 是否有长度
	private boolean hasPrecision;// 是否有精度
	private boolean isPrimaryKey;// 是否是主键
	private boolean isCanNull;// 是否可以为空
	private boolean isIdentity;// 是否是标识
	private String desc;// 字段说明
	private String foreignKey;// 外键

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldNameCH() {
		return fieldNameCH;
	}

	public void setFieldNameCH(String fieldNameCH) {
		this.fieldNameCH = fieldNameCH;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isHasLength() {
		return hasLength;
	}

	public void setHasLength(boolean hasLength) {
		this.hasLength = hasLength;
	}

	public boolean isHasPrecision() {
		return hasPrecision;
	}

	public void setHasPrecision(boolean hasPrecision) {
		this.hasPrecision = hasPrecision;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public boolean isCanNull() {
		return isCanNull;
	}

	public void setCanNull(boolean isCanNull) {
		this.isCanNull = isCanNull;
	}

	public boolean isIdentity() {
		return isIdentity;
	}

	public void setIdentity(boolean isIdentity) {
		this.isIdentity = isIdentity;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().toString().equals(this.getClass().toString())) {
			return false;
		}
		Column cExcel = (Column) obj;
		// 类型是否相同,为true时，则相同
		boolean boolType = true;
		// 因为从数据库读取出来的列类型全改为小写,所以这里也要改成小写
		if (!this.getFieldType().equals(cExcel.getFieldType().toLowerCase())) {
			boolType = false;
		}

		// 引用修改 //TODO 此处法存在问题
		Boolean boolFK = true;
		if (this.getForeignKey() != null && cExcel.getForeignKey() != null) {
			if (this.getForeignKey().length() > 0 && cExcel.getForeignKey().length() > 0) {
				if (!this.getForeignKey().equals(cExcel.getForeignKey())) {
					boolFK = false;
				}
			}
		}

		// 中文名是否改变
		boolean name_ch = true;
		if (!this.getFieldNameCH().equals(cExcel.getFieldNameCH())) {
			name_ch = false;
		}

		// 长度是否相同,为true时，则长度相同
		boolean boolLength = true;
		if (cExcel.isHasLength()) {// 有长度
			boolLength = cExcel.getFieldLength() == this.getFieldLength();
		} else {
			boolLength = cExcel.isHasLength() == this.isHasLength();// 是否都有或者没有长度
		}

		// 精度是否相同,为true时，则精度相同
		boolean boolPrecision = true;// 为true时，则长度相同
		if (cExcel.isHasPrecision()) {// 有精度
			boolPrecision = cExcel.getPrecision() == this.getPrecision();
		} else {
			boolPrecision = cExcel.isHasPrecision() == this.isHasPrecision();// 是否都有或者没有精度
		}

		if (boolType && boolLength && boolPrecision && name_ch && boolFK) {// 内容一样
			return true;
		}

		return false;
	}

}
