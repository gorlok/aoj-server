package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChaosArmourRequest extends ClientPacket {
	// ChaosArmour,b:index,i:objIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChaosArmour;
	}
	public byte index;
	public short objIndex;
	public ChaosArmourRequest(byte index,short objIndex){
		this.index = index;
		this.objIndex = objIndex;
	}
	public static ChaosArmourRequest decode(ByteBuf in) {    
		try {                                   
			byte index = readByte(in);
			short objIndex = readShort(in);
			return new ChaosArmourRequest(index,objIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

