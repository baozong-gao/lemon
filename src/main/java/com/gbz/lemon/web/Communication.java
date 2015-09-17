package com.gbz.lemon.web;

import java.io.IOException;

public interface Communication {
	
	public void sendMessage(byte [] data)throws IOException;
	
	public byte [] accept()throws IOException;
}
