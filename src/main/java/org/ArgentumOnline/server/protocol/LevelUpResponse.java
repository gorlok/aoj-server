package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class LevelUpResponse extends ServerPacket {
	// LevelUp,i:skillPoints
	@Override
	public ServerPacketID id() {
		return ServerPacketID.LevelUp;
	}
	public short skillPoints;
	public LevelUpResponse(short skillPoints){
		this.skillPoints = skillPoints;
	}
	public static LevelUpResponse decode(ByteBuf in) {    
		try {                                   
			short skillPoints = readShort(in);
			return new LevelUpResponse(skillPoints);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

