package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowCarpenterFormResponse extends ServerPacket {
	// ShowCarpenterForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowCarpenterForm;
	}
	public ShowCarpenterFormResponse(){
	}
};

