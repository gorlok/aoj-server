package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowServerFormRequest extends ClientPacket {
	// ShowServerForm
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ShowServerForm;
	}
	public ShowServerFormRequest(){
	}
};

