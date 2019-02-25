package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserAttackedSwingResponse extends ServerPacket {
	// UserAttackedSwing,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserAttackedSwing;
	}
	public short charIndex;
	public UserAttackedSwingResponse(short charIndex){
		this.charIndex = charIndex;
	}
};

