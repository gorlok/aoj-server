package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoRestrictedRequest extends ClientPacket {
	// ChangeMapInfoRestricted,s:status
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoRestricted;
	}
	public String status;
	public ChangeMapInfoRestrictedRequest(String status){
		this.status = status;
	}
};

