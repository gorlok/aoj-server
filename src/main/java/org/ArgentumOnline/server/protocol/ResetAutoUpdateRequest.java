package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResetAutoUpdateRequest extends ClientPacket {
	// ResetAutoUpdate
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResetAutoUpdate;
	}
	public ResetAutoUpdateRequest(){
	}
};

