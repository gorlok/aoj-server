package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class LeftClickRequest extends ClientPacket {
	// LeftClick,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LeftClick;
	}
	public byte x;
	public byte y;
	public LeftClickRequest(byte x,byte y){
		this.x = x;
		this.y = y;
	}
};

