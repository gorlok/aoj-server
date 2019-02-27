package org.ArgentumOnline.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

class ResponseDataEncoder extends MessageToByteEncoder<ServerPacket> {
	private static Logger log = LogManager.getLogger();

	@Override
	protected void encode(ChannelHandlerContext ctx, ServerPacket packet, ByteBuf out) throws Exception {
		log.debug("response data encoder " + packet.getClass().getCanonicalName());
		packet.encode(out);
	}
}