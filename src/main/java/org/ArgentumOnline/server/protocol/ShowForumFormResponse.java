package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowForumFormResponse extends ServerPacket {
	// ShowForumForm
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowForumForm;
	}
	public ShowForumFormResponse(){
	}
};

