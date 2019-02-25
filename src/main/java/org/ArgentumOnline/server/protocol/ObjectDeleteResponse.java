package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ObjectDeleteResponse extends ServerPacket {
	// ObjectDelete,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ObjectDelete;
	}
	public byte x;
	public byte y;
	public ObjectDeleteResponse(byte x,byte y){
		this.x = x;
		this.y = y;
	}
};

