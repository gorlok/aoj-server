package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankStartRequest extends ClientPacket {
	// BankStart
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BankStart;
	}
	public BankStartRequest(){
	}
};

