package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CreateFXResponse extends ServerPacket {
	// CreateFX,i:charIndex,i:fx,i:fxLoops
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CreateFX;
	}
	public short charIndex;
	public short fx;
	public short fxLoops;
	public CreateFXResponse(short charIndex,short fx,short fxLoops){
		this.charIndex = charIndex;
		this.fx = fx;
		this.fxLoops = fxLoops;
	}
};

