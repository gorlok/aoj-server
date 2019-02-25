package org.ArgentumOnline.server.net;

import java.nio.charset.Charset;
import java.util.List;

import org.ArgentumOnline.server.protocol.LoginExistingCharRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oracle.tools.packager.Log;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;

public class NettyServer {
	private static Logger log = LogManager.getLogger();

	private int port;

	NioEventLoopGroup acceptorGroup = new NioEventLoopGroup(2); // 2 threads
	NioEventLoopGroup handlerGroup = new NioEventLoopGroup(10); // 10 threads

	private NettyServer(int port) {
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
										new ResponseDataEncoder(),
										new ProcessingHandler());
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

	public static NettyServer start(int port) throws Exception {
		var ns = new NettyServer(port);

		return ns;
	}

	public void shutdown() {
		acceptorGroup.shutdownGracefully();
		handlerGroup.shutdownGracefully();
	}

}

class RequestDecoder extends ReplayingDecoder<ClientPacket> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		in.markReaderIndex();
		
		int id = in.readByte();
		ClientPacketID packetId = ClientPacketID.values()[id];
		ClientPacket packet = null;
		
		switch (packetId) {
		case LoginExistingChar:
			packet = decodeLoginExistingChar(in);
			break;

		default:
			// FIXME
			break;
		}
		
		if (packet == null) {
			return;
		}

		out.add(packet); // add packet to handle it
	}
	
	static Charset charset = Charset.forName("ISO-8859-1");
	private static String readStr(ByteBuf in) {
		short len = in.readShort();
		return in.readCharSequence(len, charset).toString();			
	}

	private ClientPacket decodeLoginExistingChar(ByteBuf in) {
		try {
			String userName = readStr(in);
			String password = readStr(in);
			byte version1 = in.readByte();			
			byte version2 = in.readByte();			
			byte version3 = in.readByte();
			
			short versionGrafs  = in.readShort();
			short versionWavs   = in.readShort();
			short versionMidis  = in.readShort();
			short versionInits  = in.readShort();
			short versionMapas  = in.readShort();
			short versionAoExe  = in.readShort();
			short versionExtras = in.readShort();
			
			return new LoginExistingCharRequest(userName, password, version1, version2, version3, versionGrafs, versionWavs, versionMidis, versionInits, versionMapas, versionAoExe, versionExtras);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}

class ResponseDataEncoder extends MessageToByteEncoder<ServerPacket> {
	private static Logger log = LogManager.getLogger();

	@Override
	protected void encode(ChannelHandlerContext ctx, ServerPacket packet, ByteBuf out) throws Exception {
		// TODO
		//out.writeInt(msg.getIntValue());
		log.debug("response data encoder " + packet.getClass().getName());
	}
}

class ProcessingHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		// TODO
		ClientPacket clientPacket = (ClientPacket) packet;
		log.debug("processing handler " + clientPacket.getClass().getName());
//		ResponseData responseData = new ResponseData();
//		responseData.setIntValue(requestData.getIntValue() * 2);
//		ChannelFuture future = ctx.writeAndFlush(responseData);
//		future.addListener(ChannelFutureListener.CLOSE);
//		System.out.println(requestData);
	}
}
