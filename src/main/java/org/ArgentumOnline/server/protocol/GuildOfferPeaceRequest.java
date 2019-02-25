package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildOfferPeaceRequest extends ClientPacket {
	// GuildOfferPeace,s:guild,s:proposal
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOfferPeace;
	}
	public String guild;
	public String proposal;
	public GuildOfferPeaceRequest(String guild,String proposal){
		this.guild = guild;
		this.proposal = proposal;
	}
};

