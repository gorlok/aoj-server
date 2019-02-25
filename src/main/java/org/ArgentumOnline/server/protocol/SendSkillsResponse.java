package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SendSkillsResponse extends ServerPacket {
	// SendSkills,b[NUMSKILLS]:skills
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SendSkills;
	}
	public byte[] skills;
	public SendSkillsResponse(byte[] skills){
		this.skills = skills;
	}
	public static SendSkillsResponse decode(ByteBuf in) {    
		try {                                   
			byte[] skills = readBytes(in, Skill.MAX_SKILLS);
			return new SendSkillsResponse(skills);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

