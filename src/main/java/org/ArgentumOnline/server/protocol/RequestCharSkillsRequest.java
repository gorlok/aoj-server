package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharSkillsRequest extends ClientPacket {
	// RequestCharSkills,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharSkills;
	}
	public String userName;
	public RequestCharSkillsRequest(String userName){
		this.userName = userName;
	}
};

