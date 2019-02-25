package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowMOTDEditionFormResponse extends ServerPacket {
	// ShowMOTDEditionForm,s:currentMOTD
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowMOTDEditionForm;
	}
	public String currentMOTD;
	public ShowMOTDEditionFormResponse(String currentMOTD){
		this.currentMOTD = currentMOTD;
	}
	public static ShowMOTDEditionFormResponse decode(ByteBuf in) {    
		try {                                   
			String currentMOTD = readStr(in);
			return new ShowMOTDEditionFormResponse(currentMOTD);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

