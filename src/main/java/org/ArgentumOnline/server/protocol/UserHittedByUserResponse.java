package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static UserHittedByUserResponse decode(ByteBuf in) {    
		try {                                   
			short attackerChar = readShort(in);
			byte target = readByte(in);
			short damage = readShort(in);
			return new UserHittedByUserResponse(attackerChar,target,damage);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,attackerChar);
		writeByte(out,target);
		writeShort(out,damage);
	}
};

