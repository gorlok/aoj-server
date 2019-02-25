package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NPCFollowRequest extends ClientPacket {
	// NPCFollow
	@Override
	public ClientPacketID id() {
		return ClientPacketID.NPCFollow;
	}
	public NPCFollowRequest(){
	}
};

