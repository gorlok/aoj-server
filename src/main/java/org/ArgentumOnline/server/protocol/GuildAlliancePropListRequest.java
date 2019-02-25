package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildAlliancePropListRequest extends ClientPacket {
	// GuildAlliancePropList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAlliancePropList;
	}
	public GuildAlliancePropListRequest(){
	}
};

