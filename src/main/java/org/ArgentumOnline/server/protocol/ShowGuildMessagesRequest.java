package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowGuildMessagesRequest extends ClientPacket {
	// ShowGuildMessages,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ShowGuildMessages;
	}
	public String guild;
	public ShowGuildMessagesRequest(String guild){
		this.guild = guild;
	}
};

