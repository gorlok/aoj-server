package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TeleportDestroyRequest extends ClientPacket {
	// TeleportDestroy
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TeleportDestroy;
	}
	public TeleportDestroyRequest(){
	}
};

