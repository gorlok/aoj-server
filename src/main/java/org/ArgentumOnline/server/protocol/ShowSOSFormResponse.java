package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowSOSFormResponse extends ServerPacket {
	// ShowSOSForm,s:sosList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowSOSForm;
	}
	public String sosList;
	public ShowSOSFormResponse(String sosList){
		this.sosList = sosList;
	}
	public static ShowSOSFormResponse decode(ByteBuf in) {    
		try {                                   
			String sosList = readStr(in);
			return new ShowSOSFormResponse(sosList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

