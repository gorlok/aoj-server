package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CraftCarpenterRequest extends ClientPacket {
	// CraftCarpenter,i:item
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CraftCarpenter;
	}
	public short item;
	public CraftCarpenterRequest(short item){
		this.item = item;
	}
	public static CraftCarpenterRequest decode(ByteBuf in) {    
		try {                                   
			short item = readShort(in);
			return new CraftCarpenterRequest(item);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

