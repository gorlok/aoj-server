package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestGuildLeaderInfoRequest extends ClientPacket {
	// RequestGuildLeaderInfo
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestGuildLeaderInfo;
	}
	public RequestGuildLeaderInfoRequest(){
	}
};

