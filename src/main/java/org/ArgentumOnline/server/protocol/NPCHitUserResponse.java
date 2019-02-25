package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class NPCHitUserResponse extends ServerPacket {
	// NPCHitUser,b:target,i:damage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NPCHitUser;
	}
	public byte target;
	public short damage;
	public NPCHitUserResponse(byte target,short damage){
		this.target = target;
		this.damage = damage;
	}
};

