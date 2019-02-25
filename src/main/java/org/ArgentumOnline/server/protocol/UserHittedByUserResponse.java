package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserHittedByUserResponse extends ServerPacket {
	// UserHittedByUser,i:attackerChar,b:target,i:damage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserHittedByUser;
	}
	public short attackerChar;
	public byte target;
	public short damage;
	public UserHittedByUserResponse(short attackerChar,byte target,short damage){
		this.attackerChar = attackerChar;
		this.target = target;
		this.damage = damage;
	}
};

