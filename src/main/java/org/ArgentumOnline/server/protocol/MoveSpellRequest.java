package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MoveSpellRequest extends ClientPacket {
	// MoveSpell,b:dir,b:spell
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MoveSpell;
	}
	public byte dir;
	public byte spell;
	public MoveSpellRequest(byte dir,byte spell){
		this.dir = dir;
		this.spell = spell;
	}
	public static MoveSpellRequest decode(ByteBuf in) {    
		try {                                   
			byte dir = readByte(in);
			byte spell = readByte(in);
			return new MoveSpellRequest(dir,spell);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

