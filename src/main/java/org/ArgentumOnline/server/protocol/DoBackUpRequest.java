package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DoBackUpRequest extends ClientPacket {
	// DoBackUp
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DoBackUp;
	}
	public DoBackUpRequest(){
	}
};

