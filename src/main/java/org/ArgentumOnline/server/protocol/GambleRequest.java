package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GambleRequest extends ClientPacket {
	// Gamble,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Gamble;
	}
	public short amount;
	public GambleRequest(short amount){
		this.amount = amount;
	}
};

