package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowUserRequestResponse extends ServerPacket {
	// ShowUserRequest,s:details
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowUserRequest;
	}
	public String details;
	public ShowUserRequestResponse(String details){
		this.details = details;
	}
};

