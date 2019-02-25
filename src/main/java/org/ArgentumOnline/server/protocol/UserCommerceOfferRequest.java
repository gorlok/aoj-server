package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UserCommerceOfferRequest extends ClientPacket {
	// UserCommerceOffer,b:slot,l:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UserCommerceOffer;
	}
	public byte slot;
	public int amount;
	public UserCommerceOfferRequest(byte slot,int amount){
		this.slot = slot;
		this.amount = amount;
	}
	public static UserCommerceOfferRequest decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			int amount = readInt(in);
			return new UserCommerceOfferRequest(slot,amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

