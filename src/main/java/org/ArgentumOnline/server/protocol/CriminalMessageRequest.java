package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CriminalMessageRequest extends ClientPacket {
	// CriminalMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CriminalMessage;
	}
	public String message;
	public CriminalMessageRequest(String message){
		this.message = message;
	}
};

