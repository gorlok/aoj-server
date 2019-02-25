package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NPCKillUserResponse extends ServerPacket {
	// NPCKillUser
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NPCKillUser;
	}
	public NPCKillUserResponse(){
	}
};

