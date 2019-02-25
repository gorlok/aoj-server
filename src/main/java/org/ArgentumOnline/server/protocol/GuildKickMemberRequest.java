package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildKickMemberRequest extends ClientPacket {
	// GuildKickMember,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildKickMember;
	}
	public String userName;
	public GuildKickMemberRequest(String userName){
		this.userName = userName;
	}
};

