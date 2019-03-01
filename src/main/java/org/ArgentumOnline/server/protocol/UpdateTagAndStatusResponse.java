package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateTagAndStatusResponse extends ServerPacket {
	// UpdateTagAndStatus,i:charIndex,b:criminal,s:tag
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateTagAndStatus;
	}
	public short charIndex;
	public byte criminal;
	public String tag;
	public UpdateTagAndStatusResponse(short charIndex,byte criminal,String tag){
		this.charIndex = charIndex;
		this.criminal = criminal;
		this.tag = tag;
	}
	public static UpdateTagAndStatusResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			byte criminal = readByte(in);
			String tag = readStr(in);
			return new UpdateTagAndStatusResponse(charIndex,criminal,tag);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,charIndex);
		writeByte(out,criminal);
		writeStr(out,tag);
	}
};

