package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class MeditateRequest extends ClientPacket {
	// Meditate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Meditate;
	}
	public MeditateRequest(){
	}
};

