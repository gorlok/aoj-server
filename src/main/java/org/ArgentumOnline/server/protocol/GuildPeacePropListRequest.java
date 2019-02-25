package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildPeacePropListRequest extends ClientPacket {
	// GuildPeacePropList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildPeacePropList;
	}
	public GuildPeacePropListRequest(){
	}
};

