package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowGMPanelFormResponse extends ServerPacket {
	// ShowGMPanelForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowGMPanelForm;
	}
	public ShowGMPanelFormResponse(){
	}
};

