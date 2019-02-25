package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ReloadSpellsRequest extends ClientPacket {
	// ReloadSpells
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadSpells;
	}
	public ReloadSpellsRequest(){
	}
};

