package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ImperialArmourRequest extends ClientPacket {
	// ImperialArmour,b:index,i:objIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ImperialArmour;
	}
	public byte index;
	public short objIndex;
	public ImperialArmourRequest(byte index,short objIndex){
		this.index = index;
		this.objIndex = objIndex;
	}
	public static ImperialArmourRequest decode(ByteBuf in) {    
		try {                                   
			byte index = readByte(in);
			short objIndex = readShort(in);
			return new ImperialArmourRequest(index,objIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

