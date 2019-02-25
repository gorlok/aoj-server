package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MoveBankRequest extends ClientPacket {
	// MoveBank,b:dir,b:slot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MoveBank;
	}
	public byte dir;
	public byte slot;
	public MoveBankRequest(byte dir,byte slot){
		this.dir = dir;
		this.slot = slot;
	}
	public static MoveBankRequest decode(ByteBuf in) {    
		try {                                   
			byte dir = readByte(in);
			byte slot = readByte(in);
			return new MoveBankRequest(dir,slot);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

