package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankEndResponse extends ServerPacket {
	// BankEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankEnd;
	}
	public BankEndResponse(){
	}
};

