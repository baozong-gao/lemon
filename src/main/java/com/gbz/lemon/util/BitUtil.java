package com.gbz.lemon.util;

public class BitUtil {

	/**
	 * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
	 */
	public static byte[] getBooleanArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 7; i >= 0; i--) {
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}
		return array;
	}

	/**
	 * 把byte转为字符串的bit
	 */
	public static String byteToBit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
				+ (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
				+ (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
				+ (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
	}

	public static byte[] intToByteArray(int i) {
		return new byte[] { (byte) ((i >> 24) & 0xFF),
				(byte) ((i >> 16) & 0xFF), (byte) ((i >> 8) & 0xFF),
				(byte) (i & 0xFF) };

	}

	public static String intToBit(int i) {
		StringBuffer sb = new StringBuffer();
		byte[] byteArray = intToByteArray(i);
		for(byte b : byteArray){
			sb.append(byteToBit(b));
		}
		return sb.toString();
	}

	/**
	 * 二进制字符串转byte
	 */
	public static byte decodeBinaryString(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (byteStr.charAt(0) == '0') {// 正数
				re = Integer.parseInt(byteStr, 2);
			} else {// 负数
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {// 4 bit处理
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	// java 合并两个byte数组
	public static byte[] byteMerger(byte[] byte1, byte[] byte2) {
		if (byte1 == null) {
			byte1 = new byte[] {};
		}
		if (byte2 == null) {
			byte2 = new byte[] {};
		}
		byte[] byte_3 = new byte[byte1.length + byte2.length];
		System.arraycopy(byte1, 0, byte_3, 0, byte1.length);
		System.arraycopy(byte2, 0, byte_3, byte1.length, byte2.length);
		return byte_3;
	}
}
