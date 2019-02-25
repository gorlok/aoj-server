package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CraftBlacksmithRequest extends ClientPacket {
	// CraftBlacksmith,i:item
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CraftBlacksmith;
	}
	public short item;
	public CraftBlacksmithRequest(short item){
		this.item = item;
	}
	public static CraftBlacksmithRequest decode(ByteBuf in) {    
		try {                                   
			short item = readShort(in);
			return new CraftBlacksmithRequest(item);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

