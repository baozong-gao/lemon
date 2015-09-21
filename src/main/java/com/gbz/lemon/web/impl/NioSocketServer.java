package com.gbz.lemon.web.impl;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gbz.lemon.util.ListUtil;
import com.gbz.lemon.web.TcpServer;

public class NioSocketServer implements TcpServer {

	private static final Logger log = LoggerFactory.getLogger(NioSocketServer.class);

	private int listenPort = -1;

	private boolean isBlock = false;

	private NioSocketServer() {
	}

	public NioSocketServer(int port) {
		this.listenPort = port;
	}

	private Selector initSocketServer() throws Exception {
		if (listenPort != -1) {
			log.debug("服务器：初始化服务端。。。");
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(isBlock);
			log.debug("服务器：使用{}模式", isBlock ? "阻塞" : "非阻塞");
			serverChannel.socket().bind(new InetSocketAddress(this.listenPort));
			log.debug("服务器：监听本地{}端口", this.listenPort);
			Selector selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			log.debug("服务器：准备监听");
			return selector;
		}
		log.error("服务器：初始化失败。");
		return null;
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
							ret = ListUtil.byteMerger(ret,dst);
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

}
