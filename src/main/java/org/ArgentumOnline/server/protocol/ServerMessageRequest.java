package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ServerMessageRequest extends ClientPacket {
	// ServerMessage,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ServerMessage;
	}
	public String message;
	public ServerMessageRequest(String message){
		this.message = message;
	}
	public static ServerMessageRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new ServerMessageRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

