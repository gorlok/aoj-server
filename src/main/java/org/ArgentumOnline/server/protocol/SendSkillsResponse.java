package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

