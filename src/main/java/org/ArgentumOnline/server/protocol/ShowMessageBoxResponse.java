package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowMessageBoxResponse extends ServerPacket {
	// ShowMessageBox,s:message
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowMessageBox;
	}
	public String message;
	public ShowMessageBoxResponse(String message){
		this.message = message;
	}
};

