package com.gbz.lemon.template;

import java.io.InputStream;
import java.util.Map;

public abstract class XmlTemplate{
    
	protected String templateRoot = XmlTemplate.class.getClassLoader().getResource("template").getPath();// System.getProperty("user.dir")+File.separator+"template";;
	
	protected String charSet = "utf-8";
	
	public abstract String getXML(Map<String,Object> data,String tempFileName);
	
	public abstract Map<String,Object> parseXML(InputStream xml);
	
	public String getTemplateRoot() {
		return templateRoot;
	}
	public void setTemplateRoot(String templateRoot) {
		this.templateRoot = templateRoot;
	}
	public String getCodeing() {
		return charSet;
	}
	public void setCodeing(String codeing) {
		this.charSet = codeing;
	}
	
}
