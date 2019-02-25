package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

