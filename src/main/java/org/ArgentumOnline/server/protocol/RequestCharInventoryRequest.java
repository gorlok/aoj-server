package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharInventoryRequest extends ClientPacket {
	// RequestCharInventory,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharInventory;
	}
	public String userName;
	public RequestCharInventoryRequest(String userName){
		this.userName = userName;
	}
};

