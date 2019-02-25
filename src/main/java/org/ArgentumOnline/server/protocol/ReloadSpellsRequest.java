package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ReloadSpellsRequest extends ClientPacket {
	// ReloadSpells
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ReloadSpells;
	}
	public ReloadSpellsRequest(){
	}
	public static ReloadSpellsRequest decode(ByteBuf in) {    
		try {                                   
			return new ReloadSpellsRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

