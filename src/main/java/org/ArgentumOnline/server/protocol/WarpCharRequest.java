package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WarpCharRequest extends ClientPacket {
	// WarpChar,s:userName,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WarpChar;
	}
	public String userName;
	public byte x;
	public byte y;
	public WarpCharRequest(String userName,byte x,byte y){
		this.userName = userName;
		this.x = x;
		this.y = y;
	}
};

