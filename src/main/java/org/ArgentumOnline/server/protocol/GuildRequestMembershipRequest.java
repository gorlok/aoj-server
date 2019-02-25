package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildRequestMembershipRequest extends ClientPacket {
	// GuildRequestMembership,s:guild,s:application
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRequestMembership;
	}
	public String guild;
	public String application;
	public GuildRequestMembershipRequest(String guild,String application){
		this.guild = guild;
		this.application = application;
	}
};

