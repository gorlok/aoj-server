package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WorkRequest extends ClientPacket {
	// Work,b:skill
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Work;
	}
	public byte skill;
	public WorkRequest(byte skill){
		this.skill = skill;
	}
	public static WorkRequest decode(ByteBuf in) {    
		try {                                   
			byte skill = readByte(in);
			return new WorkRequest(skill);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

