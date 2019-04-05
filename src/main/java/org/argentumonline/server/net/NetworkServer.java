/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NetworkServer {
	private static Logger log = LogManager.getLogger();

	private int port;

	// 1 threads for accept new connections
	NioEventLoopGroup acceptorGroup = new NioEventLoopGroup(1);
	// threads for clients, 1 per cpu thread/core
	NioEventLoopGroup handlerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

	public NetworkServer(int port) {
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
	
	public int getPort() {
		return port;
	} 

	public void shutdown() {
		acceptorGroup.shutdownGracefully();
		handlerGroup.shutdownGracefully();
	}

}
