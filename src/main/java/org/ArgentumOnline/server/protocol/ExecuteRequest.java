package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ExecuteRequest extends ClientPacket {
	// Execute,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Execute;
	}
	public String userName;
	public ExecuteRequest(String userName){
		this.userName = userName;
	}
};

