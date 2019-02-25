package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TalkAsNPCRequest extends ClientPacket {
	// TalkAsNPC,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TalkAsNPC;
	}
	public String message;
	public TalkAsNPCRequest(String message){
		this.message = message;
	}
};

