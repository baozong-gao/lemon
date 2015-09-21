package com.gbz.lemon.web.impl;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gbz.lemon.util.ListUtil;
import com.gbz.lemon.web.TcpClient;

public class NioClient implements TcpClient {
	
	private static final Logger log = LoggerFactory.getLogger(NioClient.class);

	private String serverAddr = null;
	
	private boolean isBlock = false;

	private int serverPort = -1;

	private NioClient() {
	}

	public NioClient(String ip, int port) {
		this.serverAddr = ip;
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
					byte[] dst = null;
					while (true) {
						read = server.read(redbuffer);
						if (read == -1 || read == 0) {
							break;
						} else {
							redbuffer.flip();
							dst = new byte[read];
							redbuffer.get(dst, 0, read);
							ret = ListUtil.byteMerger(ret, dst);
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
}
