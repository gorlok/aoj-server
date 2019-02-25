package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SystemMessageRequest extends ClientPacket {
	// SystemMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SystemMessage;
	}
	public String message;
	public SystemMessageRequest(String message){
		this.message = message;
	}
};

