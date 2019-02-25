package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserCharIndexInServerResponse extends ServerPacket {
	// UserCharIndexInServer,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserCharIndexInServer;
	}
	public short charIndex;
	public UserCharIndexInServerResponse(short charIndex){
		this.charIndex = charIndex;
	}
};

