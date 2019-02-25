package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ObjectCreateResponse extends ServerPacket {
	// ObjectCreate,b:x,b:y,i:grhIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ObjectCreate;
	}
	public byte x;
	public byte y;
	public short grhIndex;
	public ObjectCreateResponse(byte x,byte y,short grhIndex){
		this.x = x;
		this.y = y;
		this.grhIndex = grhIndex;
	}
};

