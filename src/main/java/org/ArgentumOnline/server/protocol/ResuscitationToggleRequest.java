package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResuscitationToggleRequest extends ClientPacket {
	// ResuscitationToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResuscitationToggle;
	}
	public ResuscitationToggleRequest(){
	}
};

