package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class KillNPCRequest extends ClientPacket {
	// KillNPC
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KillNPC;
	}
	public KillNPCRequest(){
	}
};

