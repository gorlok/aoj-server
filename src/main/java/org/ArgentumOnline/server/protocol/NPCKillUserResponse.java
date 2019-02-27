package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class NPCKillUserResponse extends ServerPacket {
	// NPCKillUser
	@Override
	public ServerPacketID id() {
		return ServerPacketID.NPCKillUser;
	}
	public NPCKillUserResponse(){
	}
	public static NPCKillUserResponse decode(ByteBuf in) {    
		try {                                   
			return new NPCKillUserResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

