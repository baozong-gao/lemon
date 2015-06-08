package com.gbz.lemon.stream;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gbz.lemon.stream.impl.Zip;

public class ZipTest {

	public Zip zip;

	@Before
	public void init() {
		zip = new Zip();
	}

	@After
	public void down() {
		zip = null;
	}

	@Test
	public void readTest() {
		assertEquals(zip.read(null), null);
		assertEquals(zip.read(""), null);
		assertEquals(zip.read("e://test"), null);
	}
	

}
