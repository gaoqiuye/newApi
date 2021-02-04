package com.lemon.java.maven.day2.xmls;

public class Locator {
	private String keyword;
	private String by;
	private String value;
	
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "keyword="+this.keyword+",by="+this.by+",value="+this.value;
	}
	
}
