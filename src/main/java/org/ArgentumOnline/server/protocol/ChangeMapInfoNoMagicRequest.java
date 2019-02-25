package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeMapInfoNoMagicRequest extends ClientPacket {
	// ChangeMapInfoNoMagic,b:noMagic
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoMagic;
	}
	public byte noMagic;
	public ChangeMapInfoNoMagicRequest(byte noMagic){
		this.noMagic = noMagic;
	}
	public static ChangeMapInfoNoMagicRequest decode(ByteBuf in) {    
		try {                                   
			byte noMagic = readByte(in);
			return new ChangeMapInfoNoMagicRequest(noMagic);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

