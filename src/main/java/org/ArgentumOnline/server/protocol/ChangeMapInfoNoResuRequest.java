package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoNoResuRequest extends ClientPacket {
	// ChangeMapInfoNoResu,b:noResu
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoResu;
	}
	public byte noResu;
	public ChangeMapInfoNoResuRequest(byte noResu){
		this.noResu = noResu;
	}
};

