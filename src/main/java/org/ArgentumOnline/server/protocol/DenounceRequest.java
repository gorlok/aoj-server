package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DenounceRequest extends ClientPacket {
	// Denounce,s:text
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Denounce;
	}
	public String text;
	public DenounceRequest(String text){
		this.text = text;
	}
};

