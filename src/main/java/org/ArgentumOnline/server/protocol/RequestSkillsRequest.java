package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestSkillsRequest extends ClientPacket {
	// RequestSkills
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestSkills;
	}
	public RequestSkillsRequest(){
	}
};

