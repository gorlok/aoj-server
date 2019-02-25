package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WorkRequestTargetResponse extends ServerPacket {
	// WorkRequestTarget,b:skill
	@Override
	public ServerPacketID id() {
		return ServerPacketID.WorkRequestTarget;
	}
	public byte skill;
	public WorkRequestTargetResponse(byte skill){
		this.skill = skill;
	}
};

