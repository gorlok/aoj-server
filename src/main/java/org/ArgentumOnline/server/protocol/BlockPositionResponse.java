package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlockPositionResponse extends ServerPacket {
	// BlockPosition,b:x,b:y,b:blocked
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockPosition;
	}
	public byte x;
	public byte y;
	public byte blocked;
	public BlockPositionResponse(byte x,byte y,byte blocked){
		this.x = x;
		this.y = y;
		this.blocked = blocked;
	}
};

