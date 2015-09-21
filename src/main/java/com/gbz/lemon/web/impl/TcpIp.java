package com.gbz.lemon.web.impl;

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
			log.debug("客户端：初始化客户端。。。");
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(isBlock);
			log.debug("客户端：使用{}模式", isBlock ? "阻塞" : "非阻塞");
			channel.connect(new InetSocketAddress(this.serverAddr,
					this.serverPort));
			log.debug("客户端：连接服务器：{}：{}", this.serverAddr, this.serverPort);
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_CONNECT);
			log.debug("客户端：准备连接");
			return selector;
		}
		log.error("客户端：初始化失败。");
		return null;
	}

	private Selector initSocketServer() throws Exception {
		if (serverAddr == null && serverPort != -1) {
			log.debug("服务器：初始化服务端。。。");
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(isBlock);
			log.debug("服务器：使用{}模式", isBlock ? "阻塞" : "非阻塞");
			serverChannel.socket().bind(new InetSocketAddress(this.serverPort));
			log.debug("服务器：监听本地{}端口", this.serverPort);
			Selector selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			log.debug("服务器：准备监听");
			return selector;
		}
		log.error("服务器：初始化失败。");
		return null;
	}

	public void sendMessage(byte[] data) throws Exception {
		Selector selector = null;
		try {
			selector = initSocketClient();
		} catch (Exception e) {
			log.error("客户端：初始化失败。", e);
		}
		if (selector == null) {
			return;
		}
		ByteBuffer buffer = ByteBuffer.wrap(data);
		Iterator<SelectionKey> selectedKeys;
		boolean isOk = true;
		while (isOk) {
			// 当事件注册到来前，一直阻塞
			log.debug("客户端：准备连接服务器。。。");
			Thread.sleep(1000);
			selector.select();
			selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				if (key.isConnectable()) {
					log.debug("客户端：连接到服务器");
					SocketChannel server = (SocketChannel) key.channel();
					if (server.isConnectionPending()) {
						server.finishConnect();
						log.debug("客户端：已连接上服务器");
					}
					log.debug("客户端：开始向服务器发送信息");
					server.configureBlocking(isBlock);
					server.write(buffer);
					log.debug("客户端：信息已发送出");
					key.interestOps(SelectionKey.OP_READ);
					log.debug("客户端：注册了读事件");
				} else if (key.isReadable()) {
					log.debug("客户端：开始接收服务器返回信息");
					SocketChannel server = (SocketChannel) key.channel();
					ByteBuffer redbuffer = ByteBuffer.allocate(1024);
					int read;
					byte[] ret = null;
					byte [] dst = null;
					while (true) {
						read = server.read(redbuffer);
						if (read == -1 || read == 0) {
							break;
						} else {
							redbuffer.flip();
							dst = new byte [read];
							redbuffer.get(dst, 0, read);
							ret = byteMerger(ret, dst);
							redbuffer.clear();
						}
						log.debug("客户端：服务器返回【{}】", new String(ret));
					}
					server.close();
					log.debug("客户端：关闭通道");
					isOk = false;
				}
			}
		}

	}

	public byte[] accept() throws Exception {
		byte[] ret = null;
		Selector selector = null;
		try {
			selector = initSocketServer();
		} catch (Exception e) {
			log.error("服务器：初始化失败。", e);
		}
		if (selector == null) {
			return ret;
		}
		boolean isOk = true;
		Iterator<SelectionKey> selectedKeys;
		while (isOk) {
			// 当事件注册到来前，一直阻塞
			log.debug("服务器：准备接收客户请求。");
			Thread.sleep(1000);
			selector.select();
			selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				if (key.isAcceptable()) {
					log.debug("服务器：有一个连接接入");
					ServerSocketChannel channel = (ServerSocketChannel) key
							.channel();
					SocketChannel client = channel.accept();
					client.configureBlocking(isBlock);
					client.register(selector, SelectionKey.OP_READ);
					log.debug("服务器：使用{}模式，注册了读事件", isBlock ? "阻塞" : "非阻塞");
				} else if (key.isReadable()) {
					log.debug("服务器：有连接向我发送数据");
					SocketChannel client = (SocketChannel) key.channel();
					ByteBuffer redbuffer = ByteBuffer.allocate(1024);
					int read;
					byte [] dst;
					while (true) {
						read = client.read(redbuffer);
						if (read == -1 || read == 0) {
							break;
						} else {
							redbuffer.flip();
							dst = new byte [read];
							redbuffer.get(dst, 0, read);
							ret = byteMerger(ret,dst);
							redbuffer.clear();
						}
					}
					log.info("服务器：客户端数据接收完成：【{}】" , new String(ret));
					client.register(selector, SelectionKey.OP_WRITE);
					log.debug("服务器：接收数据完成，注册了写事件");
				} else if (key.isWritable()) {
					log.debug("服务器：有通道可以写入。");
					SocketChannel client = (SocketChannel) key.channel();
					ByteBuffer buffer = ByteBuffer.wrap("接受成功".getBytes());
					client.write(buffer);
					key.cancel();
					key.channel().close();
					log.debug("服务器：结束通信");
					isOk = false;
				}
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
				} catch (Exception e) {
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); // 启动线程，调用run
		exec.shutdown(); // 我要关闭线程池
	}
}
