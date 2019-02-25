package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ServerMessageRequest extends ClientPacket {
	// ServerMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ServerMessage;
	}
	public String message;
	public ServerMessageRequest(String message){
		this.message = message;
	}
};

