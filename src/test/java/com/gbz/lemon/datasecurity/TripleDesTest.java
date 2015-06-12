package com.gbz.lemon.datasecurity;

import org.junit.Before;
import org.junit.Test;
import static  org.junit.Assert.*;
import com.gbz.lemon.datasecurity.impl.symmetrical.TripleDES;

public class TripleDesTest {

	TripleDES ds = null;
	
	@Before
	public void init(){
		ds = new TripleDES("gbz","aaa",null);
	}
	
	@Test
	public void validateTest(){
		String data ="aaaaaa";
		byte[] sign = ds.sign(data.getBytes());
		assertEquals(ds.nuSign(sign), data);
	}
}
