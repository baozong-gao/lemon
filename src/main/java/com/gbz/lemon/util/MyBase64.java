package com.gbz.lemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBase64 {

	private static Logger log = LoggerFactory.getLogger(MyBase64.class);

	private static final int SIGN_GREP_SIZE = 3;

	private static final int UNSIGN_GREP_SIZE = 4;

	private static final String HIGH_FILL = "0";

	private static final String COVER = "=";

	public static final String[] BASE_CHARS = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "+", "/" };

	/**
	 * 加密
	 * 
	 * @param src
	 * @return
	 */
	public static String encoder(String src) {
		String[][] grepString = signGrepString(src);
		if (grepString == null) {
			return null;
		}
		StringBuffer ret = new StringBuffer();
		String end = "";
		int grepi = grepString.length;
		log.debug("字符串 {} 被分成了 {} 份", src, grepi);
		for (int j = 0; j < grepi; j++) {
			String[] strings = grepString[j];
			String grepStrToBinary = grepStrToBinary(strings);
			int indexOf = grepStrToBinary.indexOf(COVER);
			if (indexOf > 0) {
				end = grepStrToBinary.substring(indexOf);
				grepStrToBinary = grepStrToBinary.substring(0, indexOf);
			}
			String[][] targat = grepString(grepStrToBinary, 6);
			log.debug("{}-->{},分组：{}", strings, grepStrToBinary, targat);
			int targatSize = targat.length;

			for (int i = 0; i < targatSize; i++) {
				String[] strings2 = targat[i];
				StringBuffer sb = new StringBuffer();
				for (int h = 0; h < strings2.length; h++) {
					sb.append(strings2[h]);
				}
				String s = sb.toString();
				int index = Integer.parseInt(s, 2);
				ret.append(BASE_CHARS[index]);
			}

		}

		return ret.toString() + end;
	}

	public static String decoder(String src) {
		int coverIndex = src.indexOf(COVER);
		String noCoverSrc = src;
		String cover = "";
		if (coverIndex > 0) {
			cover = src.substring(coverIndex);
			noCoverSrc = src.substring(0, coverIndex);
		} else {
			coverIndex = 0;
		}

		String[][] nusignGrepString = nusignGrepString(noCoverSrc);
		if (nusignGrepString == null) {
			return null;
		}
		StringBuffer indexs = new StringBuffer();
		int strlen = nusignGrepString.length;
		log.debug("待解密字符串被 分为{}组", strlen);

		for (int i = 0; i < strlen; i++) {
			String[] strings = nusignGrepString[i];
			String index = signStrToBaseChasreIndex(strings);
			indexs.append(index);
			log.debug("{}-->{}", strings, index);
		}

		StringBuffer decoderStr = new StringBuffer();
		String[][] targat = grepString(indexs.toString(), 8);
		String[] tar = null;
		int maxlen = targat.length - cover.length();
		for (int i = 0; i < maxlen; i++) {
			tar = targat[i];
			String stringFromBinary = binaryToString(tar);
			decoderStr.append(stringFromBinary);
		}

		return decoderStr.toString();
	}

	private static String binaryToString(String[] tar) {
		StringBuffer str = new StringBuffer();
		for (String c : tar) {
			str.append(c);
		}
		char c = (char) Integer.parseInt(str.toString(),2);

		return c + "";
	}

	/**
	 * 把待解密字符转换成字符库下标
	 * 
	 * @param str
	 * @return
	 */
	private static String signStrToBaseChasreIndex(String[] str) {
		StringBuffer sb = new StringBuffer();
		int len = str.length;
		int baseLen = BASE_CHARS.length;
		for (int i = 0; i < len; i++) {
			String string = str[i];
			for (int j = 0; j < baseLen; j++) {
				if (BASE_CHARS[j].equals(string)) {
					String binaryString = Integer.toBinaryString(j);
					String highFILL = highFILL(binaryString, 6);
					sb.append(highFILL);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 把字符数组转成二进制形式
	 * 
	 * @param grepStr
	 * @return
	 */
	private static String grepStrToBinary(String[] grepStr) {
		String endStr = "";
		String binaryStr = "";
		String str = "";
		for (int i = 0; i < SIGN_GREP_SIZE; i++) {
			str = grepStr[i];
			if (str == null) {
				str = HIGH_FILL;
				endStr += COVER;
			}
			char charAt = str.charAt(0);
			String binaryString = Integer.toBinaryString(charAt);
			binaryString = highFILL(binaryString);
			binaryStr += binaryString;
		}
		return binaryStr + endStr;
	}

	/**
	 * 对二进制补码
	 * 
	 * @param binaryStr
	 * @param size
	 * @return
	 */
	private static String highFILL(String binaryStr, int size) {

		int length = binaryStr.length();
		for (; length < size; length = binaryStr.length()) {
			binaryStr = HIGH_FILL + binaryStr;
		}
		return binaryStr;
	}

	/**
	 * 对二进制码，不足8位的进行补充
	 * 
	 * @param binaryStr
	 * @return
	 */
	private static String highFILL(String binaryStr) {
		return highFILL(binaryStr, 8);
	}

	private static String[][] nusignGrepString(String str) {
		return grepString(str, UNSIGN_GREP_SIZE);
	}

	private static String[][] signGrepString(String str) {
		return grepString(str, SIGN_GREP_SIZE);
	}

	/**
	 * 将字符串n个一组
	 * 
	 * @param str
	 * @param grepSize
	 * @return
	 */
	private static String[][] grepString(String str, int grepSize) {
		if (str == null || "".equals(str)) {
			log.error("待分组数组为空");
			return null;
		}

		int size = str.length();
		int grepArraySize = size / grepSize;
		if (size % grepSize > 0) {
			grepArraySize++;
		}
		String[][] grep = new String[grepArraySize][];
		int grepi = 0;

		for (int i = 0; i < size;) {
			grep[grepi] = new String[grepSize];
			for (int subGrepi = 0; subGrepi < grepSize && i < size; subGrepi++) {
				grep[grepi][subGrepi] = str.substring(i, ++i);
			}
			grepi++;
		}

		return grep;
	}

	public static void main(String[] args) {
		String s = encoder("aasdg44asgs");
		System.out.println(s);
		System.out.println(decoder(s));
	}
}
