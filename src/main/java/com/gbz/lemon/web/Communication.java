package com.gbz.lemon.web;


public interface Communication {
	
	public void sendMessage(byte [] data)throws Exception;
	
	public byte [] accept()throws Exception;
}
