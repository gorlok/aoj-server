package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TurnOffServerRequest extends ClientPacket {
	// TurnOffServer
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TurnOffServer;
	}
	public TurnOffServerRequest(){
	}
};

