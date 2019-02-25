package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UseSpellMacroRequest extends ClientPacket {
	// UseSpellMacro
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UseSpellMacro;
	}
	public UseSpellMacroRequest(){
	}
	public static UseSpellMacroRequest decode(ByteBuf in) {    
		try {                                   
			return new UseSpellMacroRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

