package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserHittedUserResponse extends ServerPacket {
	// UserHittedUser,i:attackedChar,b:target,i:damage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserHittedUser;
	}
	public short attackedChar;
	public byte target;
	public short damage;
	public UserHittedUserResponse(short attackedChar,byte target,short damage){
		this.attackedChar = attackedChar;
		this.target = target;
		this.damage = damage;
	}
};

