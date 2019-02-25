package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class KickAllCharsRequest extends ClientPacket {
	// KickAllChars
	@Override
	public ClientPacketID id() {
		return ClientPacketID.KickAllChars;
	}
	public KickAllCharsRequest(){
	}
};

