package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ResetNPCInventoryRequest extends ClientPacket {
	// ResetNPCInventory
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ResetNPCInventory;
	}
	public ResetNPCInventoryRequest(){
	}
};

