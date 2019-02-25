package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BannedIPReloadRequest extends ClientPacket {
	// BannedIPReload
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BannedIPReload;
	}
	public BannedIPReloadRequest(){
	}
};

