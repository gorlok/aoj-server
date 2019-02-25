package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AddForumMsgResponse extends ServerPacket {
	// AddForumMsg,s:title,s:message
	@Override
	public ServerPacketID id() {
		return ServerPacketID.AddForumMsg;
	}
	public String title;
	public String message;
	public AddForumMsgResponse(String title,String message){
		this.title = title;
		this.message = message;
	}
};

