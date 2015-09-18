package com.gbz.lemon.web.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gbz.lemon.web.Communication;

public class TcpIp implements Communication {

	private static final Logger log = LoggerFactory.getLogger(TcpIp.class);

	private String serverAddr = null;

	private int serverPort = -1;

	private boolean isBlock = false;

	private TcpIp() {
	}

	public TcpIp(String ip, int port) {
		this.serverAddr = ip;
		this.serverPort = port;
	}

	public TcpIp(int port) {
		this.serverPort = port;
	}

	private Selector initSocketClient() throws Exception {
		if (serverAddr != null && serverPort != -1) {
			log.debug("初始化客户端。。。");
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(isBlock);
			log.debug("使用{}模式", isBlock ? "阻塞" : "非阻塞");
			channel.connect(new InetSocketAddress(this.serverAddr,
					this.serverPort));
			log.debug("连接服务器：{}：{}", this.serverAddr, this.serverPort);
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_CONNECT);
			log.debug("准备连接");
			return selector;
		}
		log.error("初始化失败。");
		return null;
	}

	private Selector initSocketServer() throws Exception {
		if (serverAddr == null && serverPort != -1) {
			log.debug("初始化服务端。。。");
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(isBlock);
			log.debug("使用{}模式", isBlock ? "阻塞" : "非阻塞");
			serverChannel.socket().bind(new InetSocketAddress(this.serverPort));
			log.debug("监听本地{}端口", this.serverPort);
			Selector selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			log.debug("准备监听");
			return selector;
		}
		log.error("初始化失败。");
		return null;
	}

	public void sendMessage(byte[] data) throws IOException {
		Selector selector = null;
		try {
			selector = initSocketClient();
		} catch (Exception e) {
			log.error("客户端初始化失败。", e);
		}
		if (selector == null) {
			return;
		}
		ByteBuffer buffer = ByteBuffer.wrap(data);
		Iterator<SelectionKey> selectedKeys;
		boolean isOk = true;
		while (isOk) {
			// 当事件注册到来前，一直阻塞
			log.debug("准备连接服务器。。。");
			selector.select();
			selectedKeys = selector.selectedKeys().iterator();

			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				if (key.isConnectable()) {
					key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				} else if (key.isReadable()) {
					log.debug("开始接收服务器返回信息");
					SocketChannel channel = (SocketChannel) key.channel();
					ByteBuffer redbuffer = ByteBuffer.allocate(10);
					int read;
					redbuffer.flip();
					byte[] ret = null;
					while ((read = channel.read(redbuffer)) != -1) {
						redbuffer.flip();
						ret = byteMerger(ret, redbuffer.array());
						redbuffer.clear();
					}
					log.debug("服务器已返回");
					channel.close();
					log.debug("关闭通道");
					isOk = false;
					break;
				}else if(key.isWritable()){
					log.debug("开始向服务器发送信息");
					SocketChannel channel = (SocketChannel) key.channel();
					if (channel.isConnectionPending()) {
						channel.finishConnect();
						log.debug("已连接上服务器");
					}
					channel.configureBlocking(isBlock);
					channel.write(buffer);
					log.debug("信息已发送出");
					key.interestOps(SelectionKey.OP_READ);
				}
				selectedKeys.remove();
			}
		}

	}

	public byte[] accept() throws IOException {
		byte[] ret = null;
		Selector selector = null;
		try {
			selector = initSocketServer();
		} catch (Exception e) {
			log.error("客户端初始化失败。", e);
		}
		if (selector == null) {
			return ret;
		}
		boolean isOk = true;
		Iterator<SelectionKey> selectedKeys;
		while (isOk) {
			// 当事件注册到来前，一直阻塞
			selector.select();
			selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				if (key.isAcceptable()) {
					key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				} else if (key.isReadable()) {
					SocketChannel channel = (SocketChannel) key.channel();
					ByteBuffer redbuffer = ByteBuffer.allocate(10);
					int read;
					while ((read = channel.read(redbuffer)) != -1) {
						redbuffer.flip();
						ret = byteMerger(ret, redbuffer.array());
						redbuffer.clear();
					}
					log.info("客户端数据接收完成：" + new String(ret));
				}else if(key.isWritable()){
					ServerSocketChannel channel = (ServerSocketChannel) key
							.channel();
					SocketChannel accept = channel.accept();
					accept.configureBlocking(isBlock);
					accept.write(ByteBuffer.wrap("服务器接受连接".getBytes()));
					accept.register(selector, SelectionKey.OP_READ);
					channel.close();
					isOk = false;
					break;
				}
				selectedKeys.remove();
			}
		}
		return ret;
	}

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

	public static void main(String[] args) throws Exception {

		ExecutorService exec = Executors.newFixedThreadPool(2); // 创建一个包含两个线程的池

		exec.execute(new Runnable() {
			public void run() {
				TcpIp worker = new TcpIp(8888); // 线程类
				try {
					worker.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); // 启动线程，调用run
		exec.execute(new Runnable() {
			public void run() {
				TcpIp worker = new TcpIp("127.0.0.1", 8888); // 线程类
				try {
					worker.sendMessage("高保宗高".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); // 启动线程，调用run
		exec.shutdown(); // 我要关闭线程池
	}
}
