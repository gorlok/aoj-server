package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildFundateRequest extends ClientPacket {
	// GuildFundate,b:clanType
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildFundate;
	}
	public byte clanType;
	public GuildFundateRequest(byte clanType){
		this.clanType = clanType;
	}
};

