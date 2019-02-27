package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class OfferDetailsResponse extends ServerPacket {
	// OfferDetails,s:details
	@Override
	public ServerPacketID id() {
		return ServerPacketID.OfferDetails;
	}
	public String details;
	public OfferDetailsResponse(String details){
		this.details = details;
	}
	public static OfferDetailsResponse decode(ByteBuf in) {    
		try {                                   
			String details = readStr(in);
			return new OfferDetailsResponse(details);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,details);
	}
};

