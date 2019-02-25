package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GMPanelRequest extends ClientPacket {
	// GMPanel
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GMPanel;
	}
	public GMPanelRequest(){
	}
	public static GMPanelRequest decode(ByteBuf in) {    
		try {                                   
			return new GMPanelRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

