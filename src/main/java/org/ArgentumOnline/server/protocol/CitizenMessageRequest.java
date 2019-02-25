package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CitizenMessageRequest extends ClientPacket {
	// CitizenMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CitizenMessage;
	}
	public String message;
	public CitizenMessageRequest(String message){
		this.message = message;
	}
};

