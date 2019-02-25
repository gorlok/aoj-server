package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoZoneRequest extends ClientPacket {
	// ChangeMapInfoZone,s:infoZone
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoZone;
	}
	public String infoZone;
	public ChangeMapInfoZoneRequest(String infoZone){
		this.infoZone = infoZone;
	}
};

