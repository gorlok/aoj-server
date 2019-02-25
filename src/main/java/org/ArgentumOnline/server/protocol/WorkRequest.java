package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WorkRequest extends ClientPacket {
	// Work,b:skill
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Work;
	}
	public byte skill;
	public WorkRequest(byte skill){
		this.skill = skill;
	}
};

