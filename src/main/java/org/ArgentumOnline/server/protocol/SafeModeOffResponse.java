package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SafeModeOffResponse extends ServerPacket {
	// SafeModeOff
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SafeModeOff;
	}
	public SafeModeOffResponse(){
	}
};

