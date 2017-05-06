package com.zach.helper.entity;

public class Column {
	private int id;// id
	private String tableName;// ����
	private String fieldName;// �ֶ���
	private String fieldNameCH;// �ֶ�������
	private String fieldType;// ����
	private int fieldLength;// ����
	private int precision;// ����
	private String defaultValue;// Ĭ��ֵ
	private boolean hasLength;// �Ƿ��г���
	private boolean hasPrecision;// �Ƿ��о���
	private boolean isPrimaryKey;// �Ƿ�������
	private boolean isCanNull;// �Ƿ����Ϊ��
	private boolean isIdentity;// �Ƿ��Ǳ�ʶ
	private String desc;// �ֶ�˵��
	private String foreignKey;// ���

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
		// �����Ƿ���ͬ,Ϊtrueʱ������ͬ
		boolean boolType = true;
		// ��Ϊ�����ݿ��ȡ������������ȫ��ΪСд,��������ҲҪ�ĳ�Сд
		if (!this.getFieldType().equals(cExcel.getFieldType().toLowerCase())) {
			boolType = false;
		}

		// �����޸� //TODO �˴�����������
		Boolean boolFK = true;
		if (this.getForeignKey() != null && cExcel.getForeignKey() != null) {
			if (this.getForeignKey().length() > 0 && cExcel.getForeignKey().length() > 0) {
				if (!this.getForeignKey().equals(cExcel.getForeignKey())) {
					boolFK = false;
				}
			}
		}

		// �������Ƿ�ı�
		boolean name_ch = true;
		if (!this.getFieldNameCH().equals(cExcel.getFieldNameCH())) {
			name_ch = false;
		}

		// �����Ƿ���ͬ,Ϊtrueʱ���򳤶���ͬ
		boolean boolLength = true;
		if (cExcel.isHasLength()) {// �г���
			boolLength = cExcel.getFieldLength() == this.getFieldLength();
		} else {
			boolLength = cExcel.isHasLength() == this.isHasLength();// �Ƿ��л���û�г���
		}

		// �����Ƿ���ͬ,Ϊtrueʱ���򾫶���ͬ
		boolean boolPrecision = true;// Ϊtrueʱ���򳤶���ͬ
		if (cExcel.isHasPrecision()) {// �о���
			boolPrecision = cExcel.getPrecision() == this.getPrecision();
		} else {
			boolPrecision = cExcel.isHasPrecision() == this.isHasPrecision();// �Ƿ��л���û�о���
		}

		if (boolType && boolLength && boolPrecision && name_ch && boolFK) {// ����һ��
			return true;
		}

		return false;
	}

}
