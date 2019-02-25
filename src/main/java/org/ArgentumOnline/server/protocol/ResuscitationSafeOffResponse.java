package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResuscitationSafeOffResponse extends ServerPacket {
	// ResuscitationSafeOff
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ResuscitationSafeOff;
	}
	public ResuscitationSafeOffResponse(){
	}
};

