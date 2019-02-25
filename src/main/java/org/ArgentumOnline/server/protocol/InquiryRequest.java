package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class InquiryRequest extends ClientPacket {
	// Inquiry
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Inquiry;
	}
	public InquiryRequest(){
	}
	public static InquiryRequest decode(ByteBuf in) {    
		try {                                   
			return new InquiryRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

