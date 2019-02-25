package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CreateNPCRequest extends ClientPacket {
	// CreateNPC,i:npcIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateNPC;
	}
	public short npcIndex;
	public CreateNPCRequest(short npcIndex){
		this.npcIndex = npcIndex;
	}
};

