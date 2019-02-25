package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UpdateHPResponse extends ServerPacket {
	// UpdateHP,i:minHP
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateHP;
	}
	public short minHP;
	public UpdateHPResponse(short minHP){
		this.minHP = minHP;
	}
};

