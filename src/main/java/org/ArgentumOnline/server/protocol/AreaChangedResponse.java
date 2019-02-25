package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AreaChangedResponse extends ServerPacket {
	// AreaChanged,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.AreaChanged;
	}
	public byte x;
	public byte y;
	public AreaChangedResponse(byte x,byte y){
		this.x = x;
		this.y = y;
	}
};

