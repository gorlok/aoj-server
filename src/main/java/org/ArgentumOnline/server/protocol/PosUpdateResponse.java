package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PosUpdateResponse extends ServerPacket {
	// PosUpdate,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PosUpdate;
	}
	public byte x;
	public byte y;
	public PosUpdateResponse(byte x,byte y){
		this.x = x;
		this.y = y;
	}
};

