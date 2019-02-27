package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RemoveCharDialogResponse extends ServerPacket {
	// RemoveCharDialog,i:charIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.RemoveCharDialog;
	}
	public short charIndex;
	public RemoveCharDialogResponse(short charIndex){
		this.charIndex = charIndex;
	}
	public static RemoveCharDialogResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			return new RemoveCharDialogResponse(charIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,charIndex);
	}
};

