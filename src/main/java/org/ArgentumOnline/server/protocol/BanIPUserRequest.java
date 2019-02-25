package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BanIPUserRequest extends ClientPacket {
	// BanIPUser,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BanIPUser;
	}
	public String userName;
	public String reason;
	public BanIPUserRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
};

