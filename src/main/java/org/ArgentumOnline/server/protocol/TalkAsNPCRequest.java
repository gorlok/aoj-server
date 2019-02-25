package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TalkAsNPCRequest extends ClientPacket {
	// TalkAsNPC,s:message
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TalkAsNPC;
	}
	public String message;
	public TalkAsNPCRequest(String message){
		this.message = message;
	}
	public static TalkAsNPCRequest decode(ByteBuf in) {    
		try {                                   
			String message = readStr(in);
			return new TalkAsNPCRequest(message);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

