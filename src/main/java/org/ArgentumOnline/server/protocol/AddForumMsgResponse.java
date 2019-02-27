package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static AddForumMsgResponse decode(ByteBuf in) {    
		try {                                   
			String title = readStr(in);
			String message = readStr(in);
			return new AddForumMsgResponse(title,message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,title);
		writeStr(out,message);
	}
};

