package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildLeaveRequest extends ClientPacket {
	// GuildLeave
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildLeave;
	}
	public GuildLeaveRequest(){
	}
};

