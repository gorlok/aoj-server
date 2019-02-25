package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CommentRequest extends ClientPacket {
	// Comment,s:comment
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Comment;
	}
	public String comment;
	public CommentRequest(String comment){
		this.comment = comment;
	}
};

