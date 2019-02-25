package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BankOKResponse extends ServerPacket {
	// BankOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankOK;
	}
	public BankOKResponse(){
	}
};

