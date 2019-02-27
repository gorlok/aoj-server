package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class LoggedMessageResponse extends ServerPacket {
	// LoggedMessage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.LoggedMessage;
	}
	public LoggedMessageResponse(){
	}
	public static LoggedMessageResponse decode(ByteBuf in) {    
		try {                                   
			return new LoggedMessageResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

