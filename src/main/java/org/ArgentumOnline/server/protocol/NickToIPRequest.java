package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NickToIPRequest extends ClientPacket {
	// NickToIP,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.NickToIP;
	}
	public String userName;
	public NickToIPRequest(String userName){
		this.userName = userName;
	}
};

