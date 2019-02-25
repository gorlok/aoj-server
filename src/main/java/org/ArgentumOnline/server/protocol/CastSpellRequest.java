package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CastSpellRequest extends ClientPacket {
	// CastSpell,b:spell
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CastSpell;
	}
	public byte spell;
	public CastSpellRequest(byte spell){
		this.spell = spell;
	}
	public static CastSpellRequest decode(ByteBuf in) {    
		try {                                   
			byte spell = readByte(in);
			return new CastSpellRequest(spell);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

