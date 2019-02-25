package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ReloadServerIniRequest extends ClientPacket {
	// ReloadServerIni
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadServerIni;
	}
	public ReloadServerIniRequest(){
	}
};

