package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class HelpRequest extends ClientPacket {
	// Help
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Help;
	}
	public HelpRequest(){
	}
};

