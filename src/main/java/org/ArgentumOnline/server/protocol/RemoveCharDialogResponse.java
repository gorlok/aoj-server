package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RemoveCharDialogResponse extends ServerPacket {
	// RemoveCharDialog,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RemoveCharDialog;
	}
	public short charIndex;
	public RemoveCharDialogResponse(short charIndex){
		this.charIndex = charIndex;
	}
};

