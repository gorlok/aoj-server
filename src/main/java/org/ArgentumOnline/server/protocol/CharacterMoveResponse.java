package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CharacterMoveResponse extends ServerPacket {
	// CharacterMove,i:charIndex,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterMove;
	}
	public short charIndex;
	public byte x;
	public byte y;
	public CharacterMoveResponse(short charIndex,byte x,byte y){
		this.charIndex = charIndex;
		this.x = x;
		this.y = y;
	}
};

