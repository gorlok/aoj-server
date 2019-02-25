package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class EnlistRequest extends ClientPacket {
	// Enlist
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Enlist;
	}
	public EnlistRequest(){
	}
};

