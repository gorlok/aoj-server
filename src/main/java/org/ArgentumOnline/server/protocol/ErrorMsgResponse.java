package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ErrorMsgResponse extends ServerPacket {
	// ErrorMsg,s:msg
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ErrorMsg;
	}
	public String msg;
	public ErrorMsgResponse(String msg){
		this.msg = msg;
	}
	public static ErrorMsgResponse decode(ByteBuf in) {    
		try {                                   
			String msg = readStr(in);
			return new ErrorMsgResponse(msg);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,msg);
	}
};

