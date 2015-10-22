package com.gbz.lemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	// 链接变量,用于第一轮运算
	private long[] state = new long[] { 0x67452301L, 0xefcdab89L, 0x98badcfeL,
			0x10325476L };
	//移位
	private int[] redom = new int[] { 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17,
			22, 7, 12, 17, 22, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9,
			14, 20, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
			6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21 };

	/**
	 * 计算md5码
	 * 
	 * @param data
	 * @return
	 */
	public String digest(byte[] data) {
		log.debug("开始md5。。。");
		byte[] fill = fill(data);
		byte[][] dataGroups = ListUtil.divideIntoGroups(fill, 512);

		for (byte[] datas : dataGroups) {
			md5(datas);
		}

		return Long.toHexString(state[0])+Long.toHexString(state[1])+Long.toHexString(state[2])+Long.toHexString(state[3]);
	}

	private void md5(byte[] datas) {
		byte[][] dataGroups = ListUtil.divideIntoGroups(datas, 32);
		if (dataGroups == null || dataGroups.length != 16) {
			log.error("md5 数据分组失败。data：{}", dataGroups);
			return;
		}
		long a = state[0];
		long b = state[1];
		long c = state[2];
		long d = state[3];

		/* Round 1 */
		a = FF(a, b, c, d, dataGroups[0][0], redom[0], 0xd76aa478L); /* 1 */
		d = FF(d, a, b, c, dataGroups[0][1], redom[1], 0xe8c7b756L); /* 2 */
		c = FF(c, d, a, b, dataGroups[0][2], redom[2], 0x242070dbL); /* 3 */
		b = FF(b, c, d, a, dataGroups[0][3], redom[3], 0xc1bdceeeL); /* 4 */
		a = FF(a, b, c, d, dataGroups[0][4], redom[4], 0xf57c0fafL); /* 5 */
		d = FF(d, a, b, c, dataGroups[0][5], redom[5], 0x4787c62aL); /* 6 */
		c = FF(c, d, a, b, dataGroups[0][6], redom[6], 0xa8304613L); /* 7 */
		b = FF(b, c, d, a, dataGroups[0][7], redom[7], 0xfd469501L); /* 8 */
		a = FF(a, b, c, d, dataGroups[0][8], redom[8], 0x698098d8L); /* 9 */
		d = FF(d, a, b, c, dataGroups[0][9], redom[9], 0x8b44f7afL); /* 10 */
		c = FF(c, d, a, b, dataGroups[0][10], redom[10], 0xffff5bb1L); /* 11 */
		b = FF(b, c, d, a, dataGroups[0][11], redom[11], 0x895cd7beL); /* 12 */
		a = FF(a, b, c, d, dataGroups[0][12], redom[12], 0x6b901122L); /* 13 */
		d = FF(d, a, b, c, dataGroups[0][13], redom[13], 0xfd987193L); /* 14 */
		c = FF(c, d, a, b, dataGroups[0][14], redom[14], 0xa679438eL); /* 15 */
		b = FF(b, c, d, a, dataGroups[0][15], redom[15], 0x49b40821L); /* 16 */

		/* Round 2 */
		a = GG(a, b, c, d, dataGroups[1][1], redom[16], 0xf61e2562L); /* 17 */
		d = GG(d, a, b, c, dataGroups[1][6], redom[17], 0xc040b340L); /* 18 */
		c = GG(c, d, a, b, dataGroups[1][11], redom[18], 0x265e5a51L); /* 19 */
		b = GG(b, c, d, a, dataGroups[1][0], redom[19], 0xe9b6c7aaL); /* 20 */
		a = GG(a, b, c, d, dataGroups[1][5], redom[20], 0xd62f105dL); /* 21 */
		d = GG(d, a, b, c, dataGroups[1][10], redom[21], 0x2441453L); /* 22 */
		c = GG(c, d, a, b, dataGroups[1][15], redom[22], 0xd8a1e681L); /* 23 */
		b = GG(b, c, d, a, dataGroups[1][4], redom[23], 0xe7d3fbc8L); /* 24 */
		a = GG(a, b, c, d, dataGroups[1][9], redom[24], 0x21e1cde6L); /* 25 */
		d = GG(d, a, b, c, dataGroups[1][14], redom[25], 0xc33707d6L); /* 26 */
		c = GG(c, d, a, b, dataGroups[1][3], redom[26], 0xf4d50d87L); /* 27 */
		b = GG(b, c, d, a, dataGroups[1][8], redom[27], 0x455a14edL); /* 28 */
		a = GG(a, b, c, d, dataGroups[1][13], redom[28], 0xa9e3e905L); /* 29 */
		d = GG(d, a, b, c, dataGroups[1][2], redom[29], 0xfcefa3f8L); /* 30 */
		c = GG(c, d, a, b, dataGroups[1][7], redom[30], 0x676f02d9L); /* 31 */
		b = GG(b, c, d, a, dataGroups[1][12], redom[31], 0x8d2a4c8aL); /* 32 */

		/* Round 3 */
		a = HH(a, b, c, d, dataGroups[2][5], redom[32], 0xfffa3942L); /* 33 */
		d = HH(d, a, b, c, dataGroups[2][8], redom[33], 0x8771f681L); /* 34 */
		c = HH(c, d, a, b, dataGroups[2][11], redom[34], 0x6d9d6122L); /* 35 */
		b = HH(b, c, d, a, dataGroups[2][14], redom[35], 0xfde5380cL); /* 36 */
		a = HH(a, b, c, d, dataGroups[2][1], redom[36], 0xa4beea44L); /* 37 */
		d = HH(d, a, b, c, dataGroups[2][4], redom[37], 0x4bdecfa9L); /* 38 */
		c = HH(c, d, a, b, dataGroups[2][7], redom[38], 0xf6bb4b60L); /* 39 */
		b = HH(b, c, d, a, dataGroups[2][10], redom[39], 0xbebfbc70L); /* 40 */
		a = HH(a, b, c, d, dataGroups[2][13], redom[40], 0x289b7ec6L); /* 41 */
		d = HH(d, a, b, c, dataGroups[2][0], redom[41], 0xeaa127faL); /* 42 */
		c = HH(c, d, a, b, dataGroups[2][3], redom[42], 0xd4ef3085L); /* 43 */
		b = HH(b, c, d, a, dataGroups[2][6], redom[43], 0x4881d05L); /* 44 */
		a = HH(a, b, c, d, dataGroups[2][9], redom[44], 0xd9d4d039L); /* 45 */
		d = HH(d, a, b, c, dataGroups[2][12], redom[45], 0xe6db99e5L); /* 46 */
		c = HH(c, d, a, b, dataGroups[2][15], redom[46], 0x1fa27cf8L); /* 47 */
		b = HH(b, c, d, a, dataGroups[2][2], redom[47], 0xc4ac5665L); /* 48 */

		/* Round 4 */
		a = II(a, b, c, d, dataGroups[3][0], redom[48], 0xf4292244L); /* 49 */
		d = II(d, a, b, c, dataGroups[3][7], redom[49], 0x432aff97L); /* 50 */
		c = II(c, d, a, b, dataGroups[3][14], redom[50], 0xab9423a7L); /* 51 */
		b = II(b, c, d, a, dataGroups[3][5], redom[51], 0xfc93a039L); /* 52 */
		a = II(a, b, c, d, dataGroups[3][12], redom[52], 0x655b59c3L); /* 53 */
		d = II(d, a, b, c, dataGroups[3][3], redom[53], 0x8f0ccc92L); /* 54 */
		c = II(c, d, a, b, dataGroups[3][10], redom[54], 0xffeff47dL); /* 55 */
		b = II(b, c, d, a, dataGroups[3][1], redom[55], 0x85845dd1L); /* 56 */
		a = II(a, b, c, d, dataGroups[3][8], redom[56], 0x6fa87e4fL); /* 57 */
		d = II(d, a, b, c, dataGroups[3][15], redom[57], 0xfe2ce6e0L); /* 58 */
		c = II(c, d, a, b, dataGroups[3][6], redom[58], 0xa3014314L); /* 59 */
		b = II(b, c, d, a, dataGroups[3][13], redom[59], 0x4e0811a1L); /* 60 */
		a = II(a, b, c, d, dataGroups[3][4], redom[60], 0xf7537e82L); /* 61 */
		d = II(d, a, b, c, dataGroups[3][11], redom[61], 0xbd3af235L); /* 62 */
		c = II(c, d, a, b, dataGroups[3][2], redom[62], 0x2ad7d2bbL); /* 63 */
		b = II(b, c, d, a, dataGroups[3][9], redom[63], 0xeb86d391L); /* 64 */

		state[0] += a;
		state[1] += b;
		state[2] += c;
		state[3] += d;

	}

	/**
	 * 补位：使用1和n个0补位 1）使位数为：n*512 + 488 (n可为0)
	 * 2）追加64bit的数，包括data长度，如果data长度大于64则取低64位
	 * 
	 * @param data
	 * @return
	 */
	private static byte[] fill(byte[] data) {
		
		int dataSize = data.length * 8;
		byte [] tmp = null;
		for(byte b : data){
			String byteToBinary = BitUtil.byteToBit(b);
			tmp = BitUtil.byteMerger(tmp, StringUtil.stringTobyteArray(byteToBinary));
		}
		data = tmp;
		int mod = dataSize % 512;
		int n = (int) Math.round(Math.random() * 10);
		int fillSize = 512 - 64 - mod + (n * 512);
		log.debug("数据长度【{}】位，凑齐512位须要补充【{}】位，n:{},实际补充【{}】位。",new Object[]{dataSize,512 - 64 - mod,n,fillSize});
		
		byte[] fillByteData = null;
		if (fillSize > 0) {
			fillByteData = new byte[fillSize];
			fillByteData[0] = FILL_HEAD;
			data = BitUtil.byteMerger(data, fillByteData);
		}
		log.debug("开始处理64补位。。。");
		String fill64 = BitUtil.intToBit(dataSize);
		int fill64len = fill64.length();
		if (fill64len > 64) {
			log.debug("大于64位：进行截取");
			fill64 = fill64.substring(fill64len - 64);
		} else if (fill64len < 64) {
			int a = 64 - fill64len;
			fill64 = fill64 + FILL_HEAD;
			for (int i = 0; i < a - 1; i++) {
				fill64 = fill64 + FILL_END;
			}
			log.debug("小于64位：进行补位，补位后为{}", fill64);
		}
		byte[] fill64Array = StringUtil.stringTobyteArray(fill64);

		byte[] byteMerger = BitUtil.byteMerger(data, fill64Array);
		log.info("补码后数据{}位,为:{}", byteMerger.length, byteMerger);
		return byteMerger;
	}

	/**
	 * FF(a,b,c,d,Mj,s,ti）表示 a = b + ((a + F(b,c,d) + Mj + ti) << s)
	 * 
	 * @param Mj
	 *            第n个分组
	 * @param s
	 *            向左移位数
	 * @param ti
	 *            2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。
	 * @return
	 */
	private long FF(long a, long b, long c, long d, long Mj, long s, long ti) {
		a = a + F(b, c, d) + Mj + ti;
		a = (a << s) | (a >>> (32 - s));
		a = a + b;
		return a;
	}

	/**
	 * GG(a,b,c,d,Mj,s,ti）表示 a = b + ((a + G(b,c,d) + Mj + ti) << s)
	 * 
	 * @param Mj
	 *            第n个分组
	 * @param s
	 *            向左移位数
	 * @param ti
	 *            2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。
	 * @return
	 */
	private long GG(long a, long b, long c, long d, long Mj, long s, long ti) {
		a = a + G(b, c, d) + Mj + ti;
		a = (a << s) | (a >>> (32 - s));
		a = a + b;
		return a;
	}

	/**
	 * HH(a,b,c,d,Mj,s,ti）表示 a = b + ((a + H(b,c,d) + Mj + ti) << s)
	 * 
	 * @param Mj
	 *            第n个分组
	 * @param s
	 *            向左移位数
	 * @param ti
	 *            2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。
	 * @return
	 */
	private long HH(long a, long b, long c, long d, long Mj, long s, long ti) {
		a = a + H(b, c, d) + Mj + ti;
		a = (a << s) | (a >>> (32 - s));
		a = a + b;
		return a;
	}

	/**
	 * Ⅱ（a,b,c,d,Mj,s,ti）表示 a = b + ((a + I(b,c,d) + Mj + ti) << s)
	 * 
	 * @param Mj
	 *            第n个分组
	 * @param s
	 *            向左移位数
	 * @param ti
	 *            2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。
	 * @return
	 */
	private long II(long a, long b, long c, long d, long Mj, long s, long ti) {
		a = a + I(b, c, d) + Mj + ti;
		a = (a << s) | (a >>> (32 - s));
		a = a + b;
		return a;
	}

	/**
	 * F(X,Y,Z) =(X&Y)|((~X)&Z)
	 */
	private long F(long X, long Y, long Z) {
		return (X & Y) | ((~X) & Z);
	}

	/**
	 * G(X,Y,Z) =(X&Z)|(Y&(~Z))
	 */
	private long G(long X, long Y, long Z) {
		return ((X & Z) | (Y & (~Z)));
	}

	/**
	 * H(X,Y,Z) =X^Y^Z
	 */
	private long H(long X, long Y, long Z) {
		return X ^ Y ^ Z;
	}

	/**
	 * I(X,Y,Z)=Y^(X|(~Z))
	 */
	private long I(long X, long Y, long Z) {
		return Y ^ (X | (~Z));
	}

	public static void main(String[] args) {
		System.out.println((byte)Integer.parseInt("11111111110011",2) & 0xff);
		MyMD5 md5 = new MyMD5();
		System.out.println(md5.digest("高保宗".getBytes()));
	}
}
