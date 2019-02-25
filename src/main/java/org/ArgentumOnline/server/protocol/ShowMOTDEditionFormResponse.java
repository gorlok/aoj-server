package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowMOTDEditionFormResponse extends ServerPacket {
	// ShowMOTDEditionForm,s:currentMOTD
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowMOTDEditionForm;
	}
	public String currentMOTD;
	public ShowMOTDEditionFormResponse(String currentMOTD){
		this.currentMOTD = currentMOTD;
	}
};

