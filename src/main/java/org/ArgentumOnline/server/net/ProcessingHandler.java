package org.ArgentumOnline.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ProcessingHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		// TODO
		Packet clientPacket = (Packet) packet;
		log.debug("processing handler " + clientPacket.getClass().getName());
//		ResponseData responseData = new ResponseData();
//		responseData.setIntValue(requestData.getIntValue() * 2);
//		ChannelFuture future = ctx.writeAndFlush(responseData);
//		future.addListener(ChannelFutureListener.CLOSE);
//		System.out.println(requestData);
	}
}