package com.lemon.api.auto;

/*
 * Case类保存case的信息
 * 
 */
public class Case {
	private  String CaseId;
	private  String ApiId;
	private  String Desc;
	private  String Params;
	
	public String getCaseId() {
		return CaseId;
	}
	public void setCaseId(String caseId) {
		CaseId = caseId;
	}
	public String getApiId() {
		return ApiId;
	}
	public void setApiId(String apiId) {
		ApiId = apiId;
	}
	public String getDesc() {
		return Desc;
	}
	public void setDesc(String desc) {
		Desc = desc;
	}
	public String getParams() {
		return Params;
	}
	public void setParams(String params) {
		Params = params;
	}
	
	@Override
	public String toString() {
		 
		return "CaseId="+CaseId+",ApiId="+ApiId+",Desc="+Desc+",params="+ Params;
	}
	 
	


}
