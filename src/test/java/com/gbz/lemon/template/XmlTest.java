package com.gbz.lemon.template;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gbz.lemon.template.impl.Xml;
import static org.junit.Assert.*;

public class XmlTest {
	
	XmlTemplate xml = null;
	Map<String,Object> map = null;
	
	@Before
	public void init(){
		xml =new  Xml();
		map = new HashMap<String,Object>();
	}

	@Test(expected = java.lang.Exception.class)
	public void getXMLTest(){
		map.put("messageId", 1);
		map.put("sysname", 2);
		map.put("errorId", 3);
		map.put("version", 4);
		map.put("instId", 5);
		map.put("certId", 6);
		map.put("errorCode", 7);
		xml.getXML(map, "Error.txt");
	}
	
	@Test
	public void getXMLIfTest(){
		map.put("messageId", 1);
		map.put("sysname", 2);
		map.put("errorId", 3);
		map.put("version", 4);
		map.put("instId", 5);
		map.put("certId", 6);
		map.put("errorCode", 7);
		map.put("errorMessage", 8);
		map.put("errorDetail", 9);
		map.put("vendorCode", 10);
		xml.getXML(map, "Error.txt");
	}
	
	@Test
	public void parseXMLTest() throws UnsupportedEncodingException{
		map.put("messageId", 1);
		map.put("sysname", 2);
		map.put("errorId", 3);
		map.put("version", 4);
		map.put("instId", 5);
		map.put("certId", 6);
		map.put("errorCode", 7);
		map.put("errorMessage", 8);
		map.put("errorDetail", 9);
		map.put("vendorCode", 10);
		String xml2 = xml.getXML(map, "Error.txt");
		Map<String, Object> parseXML = xml.parseXML(new ByteArrayInputStream(xml2.getBytes("UTF-8")));
		assertEquals(parseXML.get("Message_id"), "1");
		assertEquals(parseXML.get("sysname"), "2");
		assertEquals(parseXML.get("Error_id"), "3");
		assertEquals(parseXML.get("version"), "4");
		assertEquals(parseXML.get("instId"), "5");
		assertEquals(parseXML.get("certId"), "6");
		assertEquals(parseXML.get("errorCode"), "7");
		assertEquals(parseXML.get("errorMessage"), "8");
		assertEquals(parseXML.get("errorDetail"), "9");
		assertEquals(parseXML.get("vendorCode"), "10");
	}
	
}
