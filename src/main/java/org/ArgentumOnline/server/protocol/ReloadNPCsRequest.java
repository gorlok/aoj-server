package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ReloadNPCsRequest extends ClientPacket {
	// ReloadNPCs
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadNPCs;
	}
	public ReloadNPCsRequest(){
	}
};

