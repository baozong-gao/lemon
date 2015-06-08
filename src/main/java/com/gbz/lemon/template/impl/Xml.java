package com.gbz.lemon.template.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gbz.lemon.template.XmlTemplate;

public class Xml extends XmlTemplate{
	
    private static final Logger logger = LoggerFactory.getLogger(Xml.class);  
    
    GroupTemplate gt = null;
    
	public  Xml(){
		logger.debug("template dir {}, charset {}.",templateRoot,charSet);
		FileResourceLoader resourceLoader = new FileResourceLoader(templateRoot,charSet);
		try {
			Configuration cfg = Configuration.defaultConfiguration();
			gt = new GroupTemplate(resourceLoader, cfg);
			logger.debug("xml init down.");
		} catch (IOException e) {
		logger.error("init template error .",e);
		}
		
	}
	@Override
	public String getXML(Map<String,Object> data,String tempFileName){
		logger.debug("template file name {}",templateRoot+System.getProperty("file.separator")+tempFileName);
		Template t = gt.getTemplate(tempFileName);
		t.binding(data);
		String render = t.render();
		logger.debug("return string : {}",render);
		return render;
	}

	/**
	 * 读取标签在xml中只能出现一次，否则会覆盖
	 * 返回值：1：属性 Map key ：tag_att   重写mapKeyInXmlTagAttributeName实现定制化
	 *       2:包括子标签的Element不会有文本
	 */
	
	@Override
	public Map<String, Object> parseXML(InputStream xml) {
		 Map<String,Object> xmlData = new HashMap<String,Object>();
		SAXReader saxReader = new SAXReader();
        Document document;
		try {
			document = saxReader.read(xml);
			Element rootElement = document.getRootElement();
			getChildNode(rootElement,xmlData);
		} catch (DocumentException e) {
			logger.error("error read xml .",e);
		}
		return xmlData;
	}
	 public void getChildNode(Element subElement,Map<String,Object> xmlData){
		 String tagName = subElement.getName();
		 int attributeCount = subElement.attributeCount();
		 if(attributeCount>0){
			 List<Attribute> attList = subElement.attributes();
			for(Attribute att:attList){
				String attName = att.getName();
				String attValue = att.getValue();
				xmlData.put(mapKeyInXmlTagAttributeName(tagName,attName), attValue);
			}
		 }
		 
         List<Element> elements = subElement.elements();
          if(!elements.isEmpty()){
              for(Element e :elements ){
                  getChildNode(e,xmlData);
             }
         }else{
        	 xmlData.put(tagName,subElement.getTextTrim());
         }
    }

	 public String mapKeyInXmlTagAttributeName(String tagName,String attrName){
		 return String.format("%s_%s", tagName,attrName);
	 }
}
