package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SetInvisibleResponse extends ServerPacket {
	// SetInvisible,i:charIndex,b:invisible
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SetInvisible;
	}
	public short charIndex;
	public byte invisible;
	public SetInvisibleResponse(short charIndex,byte invisible){
		this.charIndex = charIndex;
		this.invisible = invisible;
	}
};

