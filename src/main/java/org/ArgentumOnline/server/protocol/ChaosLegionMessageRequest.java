package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChaosLegionMessageRequest extends ClientPacket {
	// ChaosLegionMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChaosLegionMessage;
	}
	public String message;
	public ChaosLegionMessageRequest(String message){
		this.message = message;
	}
};

