package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SetTriggerRequest extends ClientPacket {
	// SetTrigger
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetTrigger;
	}
	public SetTriggerRequest(){
	}
};

