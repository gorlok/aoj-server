package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DoubleClickRequest extends ClientPacket {
	// DoubleClick,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DoubleClick;
	}
	public byte x;
	public byte y;
	public DoubleClickRequest(byte x,byte y){
		this.x = x;
		this.y = y;
	}
};

