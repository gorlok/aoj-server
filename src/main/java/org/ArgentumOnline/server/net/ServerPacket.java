package org.ArgentumOnline.server.net;

import io.netty.buffer.ByteBuf;

public abstract class ServerPacket extends Packet {
	public abstract ServerPacketID id();
	
	public abstract void encode(ByteBuf out);
};
