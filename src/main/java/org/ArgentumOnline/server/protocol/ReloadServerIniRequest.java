package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ReloadServerIniRequest extends ClientPacket {
	// ReloadServerIni
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadServerIni;
	}
	public ReloadServerIniRequest(){
	}
	public static ReloadServerIniRequest decode(ByteBuf in) {    
		try {                                   
			return new ReloadServerIniRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

