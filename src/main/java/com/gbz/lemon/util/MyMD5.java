package com.gbz.lemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.provider.MD5;

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

		return null;
	}
    
	private void md5(byte[] datas) {
		byte[][] dataGroups = ListUtil.divideIntoGroups(datas, 32);
		if(dataGroups == null || dataGroups.length != 16){
			log.error("md5 数据分组失败。data：{}",dataGroups);
			return;
		}
		long a = state[0];
		long b = state[1];
		long c = state[2];
		long d = state[3];
		
		for(int j = 0;j<4 ;j++){
			
			for(int i = 0 ;i < dataGroups.length; i++){
				
			}
		}
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
		log.debug("得到二进制数据：须要补充{}位，得到数据{}", fill64fill, fill64);
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
	 * @param Mj    第n个分组
	 * @param s     向左移位数
	 * @param ti    2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。 
	 * @return
	 */
	private long FF(long a, long b, long c,long d,long Mj, long s, long ti){
		a = a + F(b,c,d) + Mj + ti;
		a = (a << s)|(a>>>(32 -s));
		a = a + b ;
		return a;
	}
	/**
	 * GG(a,b,c,d,Mj,s,ti）表示 a = b + ((a + G(b,c,d) + Mj + ti) << s)
	 * @param Mj    第n个分组
	 * @param s     向左移位数
	 * @param ti    2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。 
	 * @return
	 */
	private long GG(long a, long b, long c,long d,long Mj, long s, long ti){
		a = a + G(b,c,d) + Mj + ti;
		a = (a << s)|(a>>>(32 -s));
		a = a + b ;
		return a;
	}
	/**
	 * HH(a,b,c,d,Mj,s,ti）表示 a = b + ((a + H(b,c,d) + Mj + ti) << s)
	 * @param Mj    第n个分组
	 * @param s     向左移位数
	 * @param ti    2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。 
	 * @return
	 */
	private long HH(long a, long b, long c,long d,long Mj, long s, long ti){
		a = a + H(b,c,d) + Mj + ti;
		a = (a << s)|(a>>>(32 -s));
		a = a + b ;
		return a;
	}
	/**
	 * Ⅱ（a,b,c,d,Mj,s,ti）表示 a = b + ((a + I(b,c,d) + Mj + ti) << s)
	 * @param Mj    第n个分组
	 * @param s     向左移位数
	 * @param ti    2^32 * abs(sin(i))的整数部分，i取值从1到64，单位是弧度。 
	 * @return
	 */
	private long II(long a, long b, long c,long d,long Mj, long s, long ti){
		a = a + I(b,c,d) + Mj + ti;
		a = (a << s)|(a>>>(32 -s));
		a = a + b ;
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
		log.info("{}", fill(new byte[] { 1, 2, 4, 5, 7, 8 }));
		System.out.println((1 << 2)|(1>>>30));
		System.out.println((1>>>30));
	}
}
