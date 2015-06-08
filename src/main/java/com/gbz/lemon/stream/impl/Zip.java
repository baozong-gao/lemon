package com.gbz.lemon.stream.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gbz.lemon.stream.FileStream;
import com.gbz.lemon.util.MapUtil;

public class Zip implements FileStream {

	private static final Logger logger = LoggerFactory.getLogger(Zip.class);
	
	MapUtil<String> mapUtil = new MapUtil<String>();

	public String read(String path) {
		if(path == null){
			return null;
		}
		File file = new File(path);
		try {
			if(!file.exists()){
				logger.error("文件不存在");
			}else  if(file.isDirectory()){
				logger.error("不能读取一个目录");
			}else  if (file.isFile()) {
				ZipFile zip = new ZipFile(file);
				String zipName = zip.getName();
				logger.debug("reading file {} start ...", zipName);
				Map<String, List<String>> fileData = readZipFile(file);
				return mapUtil.mapToString(fileData);
			}
		} catch (Exception e) {
			logger.error("read file error.", e);
		}finally{
			logger.debug("reading file end.");
		}
		return null;
	}

	/**
	 * 
	 * @param file
	 * @return  map<fileName,List<rowTest>>
	 * @throws FileNotFoundException 
	 */
	private Map<String, List<String>> readZipFile(File file) throws FileNotFoundException {
		Map<String, List<String>> dataMap = new HashMap<String,List<String>>();
		InputStream io = new FileInputStream(file);
		ZipInputStream zipIo = new ZipInputStream(io);
		read(zipIo,dataMap);
		return dataMap;
	}

	private void read(ZipInputStream zipIo, Map<String, List<String>> dataMap) {
		
		try {
			ZipEntry entry = zipIo.getNextEntry();
			if(entry == null){
				return ;
			}
			if(!entry.isDirectory()){
				String name = entry.getName();
				logger.debug("start read "+name);
				InputStreamReader io = new InputStreamReader(zipIo);
				BufferedReader isr = new BufferedReader(io);
				String line = null;
				while((line = isr.readLine())!= null){
					mapUtil.putMap(dataMap, name, line);
				}
				logger.debug("read "+name+" end.");
			}
			read(zipIo,dataMap);
		} catch (IOException e) {
			logger.error("read file error.",e);
		}
	}
    /**
     * 把目录或文件压缩到文件中
     * name 如果是文件名则放到src目录下，如果包括文件路径则放到该目录下
     */
	public void write(String srcPath, String name) {
        String systemFileSeparator = System.getProperty("file.separator");
        if(name!=null&& name.length()>0){
        	Pattern pat = Pattern.compile(systemFileSeparator);
        	Matcher matcher = pat.matcher(name);
        	boolean find = matcher.find();
        }
	}
	
public static void main(String[] args) {
	Zip zip = new Zip();
	String read = zip.read("e://test_maven.zip");
	System.out.println(read);
}
}
