package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

