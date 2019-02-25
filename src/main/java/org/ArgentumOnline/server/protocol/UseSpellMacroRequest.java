package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UseSpellMacroRequest extends ClientPacket {
	// UseSpellMacro
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UseSpellMacro;
	}
	public UseSpellMacroRequest(){
	}
};

