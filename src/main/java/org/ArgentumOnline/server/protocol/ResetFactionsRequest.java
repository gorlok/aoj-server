package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResetFactionsRequest extends ClientPacket {
	// ResetFactions,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResetFactions;
	}
	public String userName;
	public ResetFactionsRequest(String userName){
		this.userName = userName;
	}
};

