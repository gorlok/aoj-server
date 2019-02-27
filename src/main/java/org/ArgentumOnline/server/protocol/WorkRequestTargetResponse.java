package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class WorkRequestTargetResponse extends ServerPacket {
	// WorkRequestTarget,b:skill
	@Override
	public ServerPacketID id() {
		return ServerPacketID.WorkRequestTarget;
	}
	public byte skill;
	public WorkRequestTargetResponse(byte skill){
		this.skill = skill;
	}
	public static WorkRequestTargetResponse decode(ByteBuf in) {    
		try {                                   
			byte skill = readByte(in);
			return new WorkRequestTargetResponse(skill);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,skill);
	}
};

