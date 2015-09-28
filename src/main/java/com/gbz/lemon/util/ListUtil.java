package com.gbz.lemon.util;

public class ListUtil {
	/**
	 * 合并两个数组
	 * 
	 * @param byte1
	 *            []
	 * @param byte2
	 *            []
	 * @return
	 */
	public static byte[] byteMerger(byte[] byte1, byte[] byte2) {
		if (byte1 == null) {
			byte1 = new byte[] {};
		} else if (byte2 == null) {
			byte2 = new byte[] {};
		}
		byte[] byte_3 = new byte[byte1.length + byte2.length];
		System.arraycopy(byte1, 0, byte_3, 0, byte1.length);
		System.arraycopy(byte2, 0, byte_3, byte1.length, byte2.length);
		return byte_3;
	}

	/**
	 * 分组
	 * 
	 * @param list
	 * @param groupSize
	 * @return
	 */
	public static byte[][] divideIntoGroups(byte[] list, int groupSize) {
		if (list == null) {
			return null;
		}
		int listSize = list.length;
		int grepSize = listSize / groupSize;
		byte[][] groups = new byte[grepSize][];

		byte[] group = null;
		int grepNumber = 0;
		int grepId = 0;
		for (int i = 0; i < listSize; i++) {
			if (i % groupSize == 0) {
				group = new byte[groupSize];
				groups[grepNumber++] = group;
				grepId = 0;
			}
			group[grepId++] = list[i];
		}
		return groups;
	}

}
