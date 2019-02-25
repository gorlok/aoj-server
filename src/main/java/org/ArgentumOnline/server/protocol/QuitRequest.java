package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class QuitRequest extends ClientPacket {
	// Quit
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Quit;
	}
	public QuitRequest(){
	}
};

