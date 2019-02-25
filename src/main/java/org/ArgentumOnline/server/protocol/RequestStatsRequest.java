package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestStatsRequest extends ClientPacket {
	// RequestStats
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestStats;
	}
	public RequestStatsRequest(){
	}
};

