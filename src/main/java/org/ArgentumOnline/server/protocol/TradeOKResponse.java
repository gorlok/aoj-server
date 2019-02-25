package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TradeOKResponse extends ServerPacket {
	// TradeOK
	@Override
	public ServerPacketID id() {
		return ServerPacketID.TradeOK;
	}
	public TradeOKResponse(){
	}
};

