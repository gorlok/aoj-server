package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AlterNameRequest extends ClientPacket {
	// AlterName,s:userName,s:newName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AlterName;
	}
	public String userName;
	public String newName;
	public AlterNameRequest(String userName,String newName){
		this.userName = userName;
		this.newName = newName;
	}
};

