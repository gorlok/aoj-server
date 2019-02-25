package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SOSRemoveRequest extends ClientPacket {
	// SOSRemove,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SOSRemove;
	}
	public String userName;
	public SOSRemoveRequest(String userName){
		this.userName = userName;
	}
};

