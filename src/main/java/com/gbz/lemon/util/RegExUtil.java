package com.gbz.lemon.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtil {

	
	/**
	 * src中是否包含des
	 * 
	 * @param src
	 * @param desStr
	 * @return
	 */
	public static Boolean isFind(String src,String desStr){
		
		if(StringUtil.isEmpty(src) || StringUtil.isEmpty(desStr)){
			return null;
		}
		
//		if(desStr.contains("\\") || desStr.contains("[")){
//			int indexOf = desStr.indexOf("\\");
//			String head = desStr.s
//			desStr = desStr"\\" + desStr.indexOf("\\");
//		}
		
		Pattern pat = Pattern.compile(desStr);
    	Matcher matcher = pat.matcher(src);
    	return matcher.find();
	}
	
	
}
