package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildOfferAllianceRequest extends ClientPacket {
	// GuildOfferAlliance,s:guild,s:proposal
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOfferAlliance;
	}
	public String guild;
	public String proposal;
	public GuildOfferAllianceRequest(String guild,String proposal){
		this.guild = guild;
		this.proposal = proposal;
	}
};

