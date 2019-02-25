package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowSOSFormResponse extends ServerPacket {
	// ShowSOSForm,s:sosList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowSOSForm;
	}
	public String sosList;
	public ShowSOSFormResponse(String sosList){
		this.sosList = sosList;
	}
};

