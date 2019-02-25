package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowGuildFundationFormResponse extends ServerPacket {
	// ShowGuildFundationForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowGuildFundationForm;
	}
	public ShowGuildFundationFormResponse(){
	}
};

