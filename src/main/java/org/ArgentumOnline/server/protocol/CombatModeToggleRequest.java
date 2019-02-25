package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CombatModeToggleRequest extends ClientPacket {
	// CombatModeToggle
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CombatModeToggle;
	}
	public CombatModeToggleRequest(){
	}
	public static CombatModeToggleRequest decode(ByteBuf in) {    
		try {                                   
			return new CombatModeToggleRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

