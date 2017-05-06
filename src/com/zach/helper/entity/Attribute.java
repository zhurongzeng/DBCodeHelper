package com.zach.helper.entity;

public class Attribute {
	private String description;
	private String type;
	private String field;

	public Attribute(String description, String type, String field) {
		this.description = description;
		this.type = type;
		this.field = field;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getField() {
		return this.field;
	}

	public String getType() {
		return this.type;
	}
}
