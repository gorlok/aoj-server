package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RemovePunishmentRequest extends ClientPacket {
	// RemovePunishment,s:userName,b:punishment,s:newText
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RemovePunishment;
	}
	public String userName;
	public byte punishment;
	public String newText;
	public RemovePunishmentRequest(String userName,byte punishment,String newText){
		this.userName = userName;
		this.punishment = punishment;
		this.newText = newText;
	}
};

