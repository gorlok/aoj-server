package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AlterPasswordRequest extends ClientPacket {
	// AlterPassword,s:userName,s:copyFrom
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AlterPassword;
	}
	public String userName;
	public String copyFrom;
	public AlterPasswordRequest(String userName,String copyFrom){
		this.userName = userName;
		this.copyFrom = copyFrom;
	}
};

