package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GMPanelRequest extends ClientPacket {
	// GMPanel
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GMPanel;
	}
	public GMPanelRequest(){
	}
};

