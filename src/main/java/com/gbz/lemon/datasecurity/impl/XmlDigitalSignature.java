package com.gbz.lemon.datasecurity.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gbz.lemon.datasecurity.DataSecurity;
import com.gbz.lemon.util.DocumentUtil;

public class XmlDigitalSignature extends DataSecurity {

	private static final Logger logger = LoggerFactory.getLogger(XmlDigitalSignature.class);
	@Override
	public String sign(byte [] data) {
		String xml = new String(data);
		
		// xml 转成doc对像
		Document doc = DocumentUtil.xmlToDocument(xml);

		XMLSignatureFactory xmlSigFactory = XMLSignatureFactory
				.getInstance("DOM");
		PrivateKey privateKey = keyUtil.getPrivateKeyByKeyStore(keyPath,
				password, privateAlias);
		DOMSignContext domSignCtx = new DOMSignContext(privateKey,
				doc.getDocumentElement());
		Reference ref = null;
		SignedInfo signedInfo = null;
		try {
			ref = xmlSigFactory
					.newReference("", xmlSigFactory.newDigestMethod(
							DigestMethod.SHA1, null), Collections
							.singletonList(xmlSigFactory.newTransform(
									Transform.ENVELOPED,
									(TransformParameterSpec) null)), null, null);
			signedInfo = xmlSigFactory.newSignedInfo(xmlSigFactory
					.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE,
							(C14NMethodParameterSpec) null), xmlSigFactory
					.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
					Collections.singletonList(ref));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			ex.printStackTrace();
		}

		// 传入公钥路径
		PublicKey publicKey = keyUtil.getPublicKeyByKeyStore(keyPath, password,
				publicAlias);
		KeyInfoFactory keyInfoFact = xmlSigFactory.getKeyInfoFactory();
		KeyValue keyValue = null;
		try {
			keyValue = keyInfoFact.newKeyValue(publicKey);
		} catch (KeyException e) {
			logger.error("public key error ", e);
		}
		KeyInfo keyInfo = keyInfoFact.newKeyInfo(Collections
				.singletonList(keyValue));

		// 创建新的XML签名
		XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo,
				keyInfo);
		try {
			// 对文档签名
			xmlSignature.sign(domSignCtx);
		} catch (MarshalException ex) {
			ex.printStackTrace();
		} catch (XMLSignatureException ex) {
			ex.printStackTrace();
		}

		return DocumentUtil.documentToStringXML(doc);
	}

	@Override
	public Boolean validate(byte [] data) {
		String xml = new String(data);
		boolean validFlag = false;
		try {
			Document doc = DocumentUtil.xmlToDocument(xml);
			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS,
					"Signature");
			PublicKey publicKey = keyUtil.getPublicKeyByKeyStore(keyPath,
					password, publicAlias);
			DOMValidateContext valContext = new DOMValidateContext(publicKey,
					nl.item(0));
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);
			validFlag = signature.validate(valContext);
		} catch (Exception e) {
			logger.error("validate Xml error.", e);
		}
		return validFlag;
	}

}
