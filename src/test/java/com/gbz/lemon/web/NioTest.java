package com.gbz.lemon.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.*;

import com.gbz.lemon.web.impl.NioClient;
import com.gbz.lemon.web.impl.NioSocketServer;

public class NioTest {

	@Test
	public void serverTest() throws Exception {
		final byte[] sendMessages = "高保宗高".getBytes();
		ExecutorService exec = Executors.newFixedThreadPool(2);

		exec.execute(new Runnable() {
			public void run() {
				NioSocketServer worker = new NioSocketServer(8888); 
				try {
					assertEquals(new String(sendMessages), new String(worker.accept())); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); // 启动线程，调用run
		exec.execute(new Runnable() {
			public void run() {
				NioClient worker = new NioClient("127.0.0.1", 8888); // 线程类
				try {
					worker.sendMessage(sendMessages);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); // 启动线程，调用run
		exec.shutdown(); 
		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); //等待子线程多久后关闭线程池
	}
}
