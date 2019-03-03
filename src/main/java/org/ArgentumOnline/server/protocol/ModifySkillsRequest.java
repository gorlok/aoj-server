package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ModifySkillsRequest extends ClientPacket {
	// ModifySkills,b[NUMSKILLS]:skills
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ModifySkills;
	}
	public byte[] skills;
	public ModifySkillsRequest(byte[] skills){
		this.skills = skills;
	}
	public static ModifySkillsRequest decode(ByteBuf in) {    
		try {                                   
			byte[] skills = readBytes(in, Skill.values().length);
			return new ModifySkillsRequest(skills);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

