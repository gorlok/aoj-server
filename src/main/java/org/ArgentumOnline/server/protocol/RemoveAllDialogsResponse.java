package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RemoveAllDialogsResponse extends ServerPacket {
	// RemoveAllDialogs
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RemoveAllDialogs;
	}
	public RemoveAllDialogsResponse(){
	}
};

