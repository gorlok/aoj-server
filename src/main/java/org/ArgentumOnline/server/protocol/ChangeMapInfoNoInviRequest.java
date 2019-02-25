package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoNoInviRequest extends ClientPacket {
	// ChangeMapInfoNoInvi,b:noInvisible
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoInvi;
	}
	public byte noInvisible;
	public ChangeMapInfoNoInviRequest(byte noInvisible){
		this.noInvisible = noInvisible;
	}
};

