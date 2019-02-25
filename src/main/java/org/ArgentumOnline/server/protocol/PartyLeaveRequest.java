package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class PartyLeaveRequest extends ClientPacket {
	// PartyLeave
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyLeave;
	}
	public PartyLeaveRequest(){
	}
};

