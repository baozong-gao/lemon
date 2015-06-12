package com.gbz.lemon.datasecurity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.gbz.lemon.datasecurity.impl.XmlDigitalSignature;
import com.gbz.lemon.util.KeyUtil;

public class XMLTest {

	DataSecurity xml =new  XmlDigitalSignature();
	private String privateFile = "";
	private String publicFile = "";
	
	@Before
	public void init(){
		privateFile = XMLTest.class.getClassLoader().getResource("serverKeys.jks").getPath();
		publicFile = XMLTest.class.getClassLoader().getResource("clientKeys.jks").getPath();
		
		xml.setPassword("123456");
		xml.setPrivateAlias("server");
		xml.setPublicAlias("server");
		KeyUtil keyUtil = new KeyUtil();;
		xml.setKeyUtil(keyUtil);
	}
	
	@Test
	public void validateTest(){
		//加密
		xml.keyPath=privateFile;
		String sign = xml.sign("<?xml version='1.0' encoding='GBK' ?><gbz><s>asf</s></gbz>".getBytes());
		xml.keyPath=publicFile;
		assertEquals(xml.validate(sign.getBytes()), true);
		xml.setPublicAlias("alice");
		assertEquals(xml.validate(sign.getBytes()), false);
	}
	
	
	
}
