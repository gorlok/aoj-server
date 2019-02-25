package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CharacterRemoveResponse extends ServerPacket {
	// CharacterRemove,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterRemove;
	}
	public short charIndex;
	public CharacterRemoveResponse(short charIndex){
		this.charIndex = charIndex;
	}
};

