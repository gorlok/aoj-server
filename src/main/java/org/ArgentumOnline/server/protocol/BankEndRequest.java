package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankEndRequest extends ClientPacket {
	// BankEnd
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankEnd;
	}
	public BankEndRequest(){
	}
};

