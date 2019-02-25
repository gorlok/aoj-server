package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AttackRequest extends ClientPacket {
	// Attack
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Attack;
	}
	public AttackRequest(){
	}
};

