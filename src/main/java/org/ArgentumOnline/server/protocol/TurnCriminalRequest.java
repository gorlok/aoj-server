package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class TurnCriminalRequest extends ClientPacket {
	// TurnCriminal,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TurnCriminal;
	}
	public String userName;
	public TurnCriminalRequest(String userName){
		this.userName = userName;
	}
	public static TurnCriminalRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new TurnCriminalRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

