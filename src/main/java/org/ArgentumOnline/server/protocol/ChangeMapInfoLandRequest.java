package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoLandRequest extends ClientPacket {
	// ChangeMapInfoLand,s:infoLand
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoLand;
	}
	public String infoLand;
	public ChangeMapInfoLandRequest(String infoLand){
		this.infoLand = infoLand;
	}
};

