package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class MoveBankRequest extends ClientPacket {
	// MoveBank,b:dir,b:slot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MoveBank;
	}
	public byte dir;
	public byte slot;
	public MoveBankRequest(byte dir,byte slot){
		this.dir = dir;
		this.slot = slot;
	}
};

