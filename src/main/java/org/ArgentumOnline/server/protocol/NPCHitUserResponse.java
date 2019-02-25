package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static NPCHitUserResponse decode(ByteBuf in) {    
		try {                                   
			byte target = readByte(in);
			short damage = readShort(in);
			return new NPCHitUserResponse(target,damage);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

