package org.ArgentumOnline.server.net;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.protocol.LoginExistingCharRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ProcessingHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		var server = GameServer.instance();
		// TODO
		ClientPacket cp = (ClientPacket) packet;
		log.debug("processing handler " + cp.getClass().getName());
		
		var player = server.findClient(ctx.channel());
		
		switch (cp.id()) {
		case LoginExistingChar:
			var request = (LoginExistingCharRequest)cp;
			player.connectUser(request.userName, request.password);
			break;
			
		default:
			System.out.println("UNHANDLED PACKET: " + cp.getClass().getCanonicalName());
		}
		
	}
}