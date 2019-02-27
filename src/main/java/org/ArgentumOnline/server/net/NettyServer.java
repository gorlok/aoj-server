package org.ArgentumOnline.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	private static Logger log = LogManager.getLogger();

	private int port;

	// 1 threads for accept new connections
	NioEventLoopGroup acceptorGroup = new NioEventLoopGroup(1);
	// threads for clients, 1 per cpu thread/core
	NioEventLoopGroup handlerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

	public NettyServer(int port) {
		this.port = port;

		ServerBootstrap b = new ServerBootstrap();
		b.group(acceptorGroup, handlerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						log.debug("initChannel");
						ch.pipeline()
								.addLast(
										new RequestDecoder(),
										new ProcessingHandler(),
										new ResponseDataEncoder());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			b.localAddress(port).bind().sync();
			log.info("Started on port %d", port);
		} catch (InterruptedException e) {
			log.fatal("Can't start server", e);
		}
	}

	public void shutdown() {
		acceptorGroup.shutdownGracefully();
		handlerGroup.shutdownGracefully();
	}

}
