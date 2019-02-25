package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildAcceptNewMemberRequest extends ClientPacket {
	// GuildAcceptNewMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAcceptNewMember;
	}
	public String userName;
	public GuildAcceptNewMemberRequest(String userName){
		this.userName = userName;
	}
};

