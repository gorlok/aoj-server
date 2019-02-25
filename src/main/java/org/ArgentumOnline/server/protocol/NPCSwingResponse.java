package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NPCSwingResponse extends ServerPacket {
	// NPCSwing
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NPCSwing;
	}
	public NPCSwingResponse(){
	}
};

