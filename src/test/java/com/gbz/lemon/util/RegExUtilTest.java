package com.gbz.lemon.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class RegExUtilTest {

	@Test
	public void isFindTest() {
		assertEquals(RegExUtil.isFind(null, null), null);
		assertEquals(RegExUtil.isFind("", null), null);
		assertEquals(RegExUtil.isFind(null, ""), null);
		assertEquals(RegExUtil.isFind("", ""), null);
		
		assertEquals(RegExUtil.isFind("aa", "a"),true);
		assertEquals(RegExUtil.isFind("aa/aa", "/"),true);
		assertEquals(RegExUtil.isFind("aa\\aa", "\\"),true);
		assertEquals(RegExUtil.isFind("aa[", "["),true);
		
		assertEquals(RegExUtil.isFind("aa[asdf", "[asdf"),true);
		assertEquals(RegExUtil.isFind("aa[", "["),true);
		assertEquals(RegExUtil.isFind("aa[", "["),true);
	}
}
