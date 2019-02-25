package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GMMessageRequest extends ClientPacket {
	// GMMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GMMessage;
	}
	public String message;
	public GMMessageRequest(String message){
		this.message = message;
	}
};

