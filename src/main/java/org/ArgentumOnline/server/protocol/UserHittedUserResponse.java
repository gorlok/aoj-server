package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static UserHittedUserResponse decode(ByteBuf in) {    
		try {                                   
			short attackedChar = readShort(in);
			byte target = readByte(in);
			short damage = readShort(in);
			return new UserHittedUserResponse(attackedChar,target,damage);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

