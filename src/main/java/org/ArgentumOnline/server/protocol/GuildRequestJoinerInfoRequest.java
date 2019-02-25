package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildRequestJoinerInfoRequest extends ClientPacket {
	// GuildRequestJoinerInfo,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRequestJoinerInfo;
	}
	public String userName;
	public GuildRequestJoinerInfoRequest(String userName){
		this.userName = userName;
	}
};

