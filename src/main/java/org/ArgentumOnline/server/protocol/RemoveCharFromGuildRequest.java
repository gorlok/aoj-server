package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RemoveCharFromGuildRequest extends ClientPacket {
	// RemoveCharFromGuild,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RemoveCharFromGuild;
	}
	public String userName;
	public RemoveCharFromGuildRequest(String userName){
		this.userName = userName;
	}
};

