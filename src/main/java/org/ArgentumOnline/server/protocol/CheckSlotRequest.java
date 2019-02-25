package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CheckSlotRequest extends ClientPacket {
	// CheckSlot,s:userName,b:slot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CheckSlot;
	}
	public String userName;
	public byte slot;
	public CheckSlotRequest(String userName,byte slot){
		this.userName = userName;
		this.slot = slot;
	}
	public static CheckSlotRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			byte slot = readByte(in);
			return new CheckSlotRequest(userName,slot);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

