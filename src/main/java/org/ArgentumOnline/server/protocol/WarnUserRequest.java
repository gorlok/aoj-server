package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WarnUserRequest extends ClientPacket {
	// WarnUser,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WarnUser;
	}
	public String userName;
	public String reason;
	public WarnUserRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
};

