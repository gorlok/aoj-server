package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RoleMasterRequestRequest extends ClientPacket {
	// RoleMasterRequest,s:request
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RoleMasterRequest;
	}
	public String request;
	public RoleMasterRequestRequest(String request){
		this.request = request;
	}
};

