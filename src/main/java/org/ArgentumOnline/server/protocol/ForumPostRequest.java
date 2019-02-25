package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static ForumPostRequest decode(ByteBuf in) {    
		try {                                   
			String title = readStr(in);
			String msg = readStr(in);
			return new ForumPostRequest(title,msg);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

