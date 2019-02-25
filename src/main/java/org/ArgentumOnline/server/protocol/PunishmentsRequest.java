package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PunishmentsRequest extends ClientPacket {
	// Punishments,s:name
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Punishments;
	}
	public String name;
	public PunishmentsRequest(String name){
		this.name = name;
	}
};

