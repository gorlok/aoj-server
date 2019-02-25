package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UptimeRequest extends ClientPacket {
	// Uptime
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Uptime;
	}
	public UptimeRequest(){
	}
};

