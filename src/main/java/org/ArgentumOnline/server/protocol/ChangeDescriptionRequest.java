package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeDescriptionRequest extends ClientPacket {
	// ChangeDescription,s:description
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeDescription;
	}
	public String description;
	public ChangeDescriptionRequest(String description){
		this.description = description;
	}
};

