package com.gbz.lemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.util.logging.resources.logging;

/**
 * 此类只做学习使用
 * 
 * @author Administrator
 */

public class MyMD5 {
	private static final Logger log = LoggerFactory.getLogger(MyMD5.class);
	// 补位
	private static final byte FILL_HEAD = 0x1;
	private static final byte FILL_END = 0x0;

	/**
	 * 计算md5码
	 * 
	 * @param data
	 * @return
	 */
	public String digest(byte[] data) {
		return null;
	}

	/**
	 * 补位：使用1和n个0补位 1）使位数为：n*512 + 488 (n可为0)
	 * 2）追加64bit的数，包括data长度，如果data长度大于64则取低64位
	 * 
	 * @param data
	 * @return
	 */
	private static byte[] fill(byte[] data) {
		int dataSize = data.length;
		int mod = dataSize % 512;
		int n = (int) Math.round(Math.random() * 10);
		int fillSize = 512 - 64 - mod + (n * 512);
		byte[] fillByteData = null;
		if (fillSize > 0) {
			fillByteData = new byte[fillSize];
			fillByteData[0] = FILL_HEAD;
			data = BitUtil.byteMerger(data, fillByteData);
		}
		log.debug("开始处理64补位。。。");
		String fill64 = Integer.toBinaryString(dataSize);
		int fill64len = fill64.length();
		// Integer.toBinaryString 会把前面的0去掉，这里补上
		int fill64fill = 8 - fill64len % 8;
		for (int i = 0; i < fill64fill; i++) {
			fill64 = "0" + fill64;
			fill64len++;
		}
		log.debug("得到二进制数据：须要补充{}位，得到数据{}",fill64fill,fill64);
		if (fill64len > 64) {
			log.debug("大于64位：进行截取");
			fill64 = fill64.substring(fill64len - 64);
		} else if (fill64len < 64) {
			int a = 64 - fill64len;
			fill64 = fill64 + FILL_HEAD;
			for (int i = 0; i < a - 1; i++) {
				fill64 = fill64 + FILL_END;
			}
			log.debug("小于64位：进行补位，补位后为{}",fill64);
		}
		byte[] fill64Array = StringUtil.stringTobyteArray(fill64);

		byte[] byteMerger = BitUtil.byteMerger(data, fill64Array);
		log.info("补码后数据{}位,为:{}",byteMerger.length,byteMerger);
		return byteMerger;
	}

	public static void main(String[] args) {
		log.info("{}",fill(new byte[]{1,2,4,5,7,8}));
}
}
