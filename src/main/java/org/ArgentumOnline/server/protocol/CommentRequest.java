package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static CommentRequest decode(ByteBuf in) {    
		try {                                   
			String comment = readStr(in);
			return new CommentRequest(comment);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

