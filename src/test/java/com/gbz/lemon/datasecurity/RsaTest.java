package com.gbz.lemon.datasecurity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gbz.lemon.datasecurity.impl.asymmetric.Rsa;

public class RsaTest {

	Rsa rsa = null;
	String test = "gbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbzgbz";
	
	@Before
	public void init(){
		rsa = new Rsa();
		rsa.initKey(1024);
	}
	
	@Test
	public void signTest(){
		byte[] signMessage = rsa.SignByPublicKey(test.getBytes());
		byte[] message = rsa.nuSignByPrivateKey(signMessage);
		assertEquals(test, new String(message));
		System.out.println(test.length());
	}
	@Test
	public void signTestByJKS(){
		String file = RsaTest.class.getClassLoader().getResource("jqtest.jks").getPath();
		rsa.initKeyFromJks(file, "111111", "1", "alice");
		byte[] signMessage = rsa.SignByPublicKey(test.getBytes());
		byte[] message = rsa.nuSignByPrivateKey(signMessage);
		assertEquals(test, new String(message));
	}
}
