package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class LoggedMessageResponse extends ServerPacket {
	// LoggedMessage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.LoggedMessage;
	}
	public LoggedMessageResponse(){
	}
};

