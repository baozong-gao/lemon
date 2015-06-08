package com.gbz.lemon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtil<T>{
	/**
	 * @param map
	 * @param key
	 * @param value
	 */
    public void putMap(Map<T,List<T>> map,T key,T value){
    	List<T> list = map.get(key);
    	if(list == null){
    		map.put(key, new ArrayList<T>());
    	}
    	map.get(key).add(value);
    }
    
    public List<T> mapToList(Map<T,List<T>> map){
    	List<T> value = new ArrayList<T>();
    	for(T key:map.keySet()){
    		value.addAll(map.get(key));
    	}
    	return value;
    }
    
    public String mapToString(Map<T,List<T>> map){
    	List<T> mapToList = mapToList(map);
    	StringBuffer sb = new StringBuffer();
    	for(T s :mapToList){
    		sb.append(s+System.getProperty("line.separator"));
    	}
    	return sb.toString();
    }
}
