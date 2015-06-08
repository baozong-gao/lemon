package com.gbz.lemon.stream;

/**
 * 流父类
 * @author baozong.gao
 *
 */
public interface FileStream {
	
	/**
	 * 读取目录
	 * @param path
	 * @return
	 */
	public String read(String des);
	
	/**
	 * 把源数据写到目录
	 * @param src
	 * @param des
	 */
	public void write(String src, String des);

}
