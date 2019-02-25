package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class WorkLeftClickRequest extends ClientPacket {
	// WorkLeftClick,b:x,b:y,b:skill
	@Override
	public ClientPacketID id() {
		return ClientPacketID.WorkLeftClick;
	}
	public byte x;
	public byte y;
	public byte skill;
	public WorkLeftClickRequest(byte x,byte y,byte skill){
		this.x = x;
		this.y = y;
		this.skill = skill;
	}
};

