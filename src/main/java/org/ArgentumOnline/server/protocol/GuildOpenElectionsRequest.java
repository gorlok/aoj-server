package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildOpenElectionsRequest extends ClientPacket {
	// GuildOpenElections
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOpenElections;
	}
	public GuildOpenElectionsRequest(){
	}
};

