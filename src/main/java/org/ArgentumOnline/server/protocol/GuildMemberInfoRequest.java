package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildMemberInfoRequest extends ClientPacket {
	// GuildMemberInfo,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildMemberInfo;
	}
	public String userName;
	public GuildMemberInfoRequest(String userName){
		this.userName = userName;
	}
};

