package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowNameRequest extends ClientPacket {
	// ShowName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ShowName;
	}
	public ShowNameRequest(){
	}
};

