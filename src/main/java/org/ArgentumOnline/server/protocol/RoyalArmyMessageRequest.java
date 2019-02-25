package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RoyalArmyMessageRequest extends ClientPacket {
	// RoyalArmyMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RoyalArmyMessage;
	}
	public String message;
	public RoyalArmyMessageRequest(String message){
		this.message = message;
	}
};

