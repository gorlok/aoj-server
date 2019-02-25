package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AskTriggerRequest extends ClientPacket {
	// AskTrigger
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AskTrigger;
	}
	public AskTriggerRequest(){
	}
};

