package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CombatModeToggleRequest extends ClientPacket {
	// CombatModeToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CombatModeToggle;
	}
	public CombatModeToggleRequest(){
	}
};

