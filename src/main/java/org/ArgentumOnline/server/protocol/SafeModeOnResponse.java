package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SafeModeOnResponse extends ServerPacket {
	// SafeModeOn
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SafeModeOn;
	}
	public SafeModeOnResponse(){
	}
};

