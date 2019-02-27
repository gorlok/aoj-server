package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowMessageBoxResponse extends ServerPacket {
	// ShowMessageBox,s:message
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowMessageBox;
	}
	public String message;
	public ShowMessageBoxResponse(String message){
		this.message = message;
	}
	public static ShowMessageBoxResponse decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new ShowMessageBoxResponse(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,message);
	}
};

