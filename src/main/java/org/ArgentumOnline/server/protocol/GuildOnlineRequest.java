package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildOnlineRequest extends ClientPacket {
	// GuildOnline
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOnline;
	}
	public GuildOnlineRequest(){
	}
};

