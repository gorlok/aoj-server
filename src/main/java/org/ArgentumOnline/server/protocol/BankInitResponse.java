package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankInitResponse extends ServerPacket {
	// BankInit
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankInit;
	}
	public BankInitResponse(){
	}
};

