package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TeleportCreateRequest extends ClientPacket {
	// TeleportCreate,i:mapa,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TeleportCreate;
	}
	public short mapa;
	public byte x;
	public byte y;
	public TeleportCreateRequest(short mapa,byte x,byte y){
		this.mapa = mapa;
		this.x = x;
		this.y = y;
	}
};

