package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SaveCharsRequest extends ClientPacket {
	// SaveChars
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SaveChars;
	}
	public SaveCharsRequest(){
	}
};

