package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SetMOTDRequest extends ClientPacket {
	// SetMOTD,s:newMOTD
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetMOTD;
	}
	public String newMOTD;
	public SetMOTDRequest(String newMOTD){
		this.newMOTD = newMOTD;
	}
};

