package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharStatsRequest extends ClientPacket {
	// RequestCharStats,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharStats;
	}
	public String userName;
	public RequestCharStatsRequest(String userName){
		this.userName = userName;
	}
};

