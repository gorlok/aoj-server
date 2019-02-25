package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SetCharDescriptionRequest extends ClientPacket {
	// SetCharDescription,s:desc
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetCharDescription;
	}
	public String desc;
	public SetCharDescriptionRequest(String desc){
		this.desc = desc;
	}
};

