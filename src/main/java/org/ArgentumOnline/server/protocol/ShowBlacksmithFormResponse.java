package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowBlacksmithFormResponse extends ServerPacket {
	// ShowBlacksmithForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowBlacksmithForm;
	}
	public ShowBlacksmithFormResponse(){
	}
};

