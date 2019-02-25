package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SpellInfoRequest extends ClientPacket {
	// SpellInfo,b:spellSlot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SpellInfo;
	}
	public byte spellSlot;
	public SpellInfoRequest(byte spellSlot){
		this.spellSlot = spellSlot;
	}
	public static SpellInfoRequest decode(ByteBuf in) {    
		try {                                   
			byte spellSlot = readByte(in);
			return new SpellInfoRequest(spellSlot);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

