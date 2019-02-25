package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CleanSOSRequest extends ClientPacket {
	// CleanSOS
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CleanSOS;
	}
	public CleanSOSRequest(){
	}
};

