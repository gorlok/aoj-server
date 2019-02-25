package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SOSRemoveRequest extends ClientPacket {
	// SOSRemove,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SOSRemove;
	}
	public String userName;
	public SOSRemoveRequest(String userName){
		this.userName = userName;
	}
	public static SOSRemoveRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new SOSRemoveRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

