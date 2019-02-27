package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserHitNPCResponse extends ServerPacket {
	// UserHitNPC,l:damage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserHitNPC;
	}
	public int damage;
	public UserHitNPCResponse(int damage){
		this.damage = damage;
	}
	public static UserHitNPCResponse decode(ByteBuf in) {    
		try {                                   
			int damage = readInt(in);
			return new UserHitNPCResponse(damage);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeInt(out,damage);
	}
};

