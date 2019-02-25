package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class HealRequest extends ClientPacket {
	// Heal
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Heal;
	}
	public HealRequest(){
	}
};

