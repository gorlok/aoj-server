package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GambleRequest extends ClientPacket {
	// Gamble,i:amount
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Gamble;
	}
	public short amount;
	public GambleRequest(short amount){
		this.amount = amount;
	}
	public static GambleRequest decode(ByteBuf in) {    
		try {                                   
			short amount = readShort(in);
			return new GambleRequest(amount);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

