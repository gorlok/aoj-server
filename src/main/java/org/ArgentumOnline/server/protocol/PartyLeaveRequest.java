package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class PartyLeaveRequest extends ClientPacket {
	// PartyLeave
	@Override
	public ClientPacketID id() {
		return ClientPacketID.PartyLeave;
	}
	public PartyLeaveRequest(){
	}
	public static PartyLeaveRequest decode(ByteBuf in) {    
		try {                                   
			return new PartyLeaveRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

