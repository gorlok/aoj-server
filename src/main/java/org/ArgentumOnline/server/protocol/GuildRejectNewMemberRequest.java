package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildRejectNewMemberRequest extends ClientPacket {
	// GuildRejectNewMember,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRejectNewMember;
	}
	public String userName;
	public String reason;
	public GuildRejectNewMemberRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
};

