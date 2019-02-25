package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ForumPostRequest extends ClientPacket {
	// ForumPost,s:title,s:msg
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForumPost;
	}
	public String title;
	public String msg;
	public ForumPostRequest(String title,String msg){
		this.title = title;
		this.msg = msg;
	}
};

