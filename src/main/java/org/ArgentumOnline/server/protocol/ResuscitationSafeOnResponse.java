package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResuscitationSafeOnResponse extends ServerPacket {
	// ResuscitationSafeOn
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ResuscitationSafeOn;
	}
	public ResuscitationSafeOnResponse(){
	}
};

