package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestMiniStatsRequest extends ClientPacket {
	// RequestMiniStats
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestMiniStats;
	}
	public RequestMiniStatsRequest(){
	}
};

