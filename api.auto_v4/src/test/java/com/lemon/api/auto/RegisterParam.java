package com.lemon.api.auto;

public class RegisterParam {
	private  String mobilephone;
	private String pword;
	public String getMobilephone() {
		return mobilephone;
	}
	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	public String getPword() {
		return pword;
	}
	public void setPword(String pword) {
		this.pword = pword;
	}

	@Override
	public String toString() {
		 
		return  "mobilephone="+mobilephone+",pword="+pword;
	}
}
