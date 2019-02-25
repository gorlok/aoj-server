package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoPKRequest extends ClientPacket {
	// ChangeMapInfoPK,b:isMapPk
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoPK;
	}
	public byte isMapPk;
	public ChangeMapInfoPKRequest(byte isMapPk){
		this.isMapPk = isMapPk;
	}
};

