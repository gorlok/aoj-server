package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AlterMailRequest extends ClientPacket {
	// AlterMail,s:userName,s:newEmail
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AlterMail;
	}
	public String userName;
	public String newEmail;
	public AlterMailRequest(String userName,String newEmail){
		this.userName = userName;
		this.newEmail = newEmail;
	}
};

