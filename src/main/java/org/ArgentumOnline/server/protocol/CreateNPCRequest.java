package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CreateNPCRequest extends ClientPacket {
	// CreateNPC,i:npcIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateNPC;
	}
	public short npcIndex;
	public CreateNPCRequest(short npcIndex){
		this.npcIndex = npcIndex;
	}
	public static CreateNPCRequest decode(ByteBuf in) {    
		try {                                   
			short npcIndex = readShort(in);
			return new CreateNPCRequest(npcIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

