package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResucitateRequest extends ClientPacket {
	// Resucitate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Resucitate;
	}
	public ResucitateRequest(){
	}
};

