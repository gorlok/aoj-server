package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TurnCriminalRequest extends ClientPacket {
	// TurnCriminal,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TurnCriminal;
	}
	public String userName;
	public TurnCriminalRequest(String userName){
		this.userName = userName;
	}
};

