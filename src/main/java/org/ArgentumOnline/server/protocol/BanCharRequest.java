package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BanCharRequest extends ClientPacket {
	// BanChar,s:userName,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BanChar;
	}
	public String userName;
	public String reason;
	public BanCharRequest(String userName,String reason){
		this.userName = userName;
		this.reason = reason;
	}
};

