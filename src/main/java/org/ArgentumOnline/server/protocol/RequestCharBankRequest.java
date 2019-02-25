package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RequestCharBankRequest extends ClientPacket {
	// RequestCharBank,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharBank;
	}
	public String userName;
	public RequestCharBankRequest(String userName){
		this.userName = userName;
	}
};

