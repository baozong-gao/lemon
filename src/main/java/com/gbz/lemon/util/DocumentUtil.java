package com.gbz.lemon.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class DocumentUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentUtil.class);
	/**
	 * xml 转 document
	 * 
	 * @param xml
	 * @return
	 */
	public static Document xmlToDocument(String xml) {
		if (xml == null) {
			throw new IllegalArgumentException();
		}
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder newDocumentBuilder = dbf.newDocumentBuilder();
			return newDocumentBuilder.parse(new ByteArrayInputStream(xml
					.getBytes()));
		} catch (Exception e) {
			logger.error("string to document error ", e);
		}
		return null;
	}

	/**
	 * document 对象转 字符串
	 * 
	 * @param doc
	 * @return
	 */
	public static String documentToStringXML(Document doc) {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t;
		try {
			t = tf.newTransformer();
			t.setOutputProperty("encoding", "UTF-8");// 解决中文问题，试过用GBK不行
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			return bos.toString();
		} catch (Exception e) {
			logger.error("document convert xml string error", e);
		}
		return null;
	}
}
