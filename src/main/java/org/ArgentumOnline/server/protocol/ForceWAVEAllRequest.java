package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ForceWAVEAllRequest extends ClientPacket {
	// ForceWAVEAll,b:waveId
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForceWAVEAll;
	}
	public byte waveId;
	public ForceWAVEAllRequest(byte waveId){
		this.waveId = waveId;
	}
	public static ForceWAVEAllRequest decode(ByteBuf in) {    
		try {                                   
			byte waveId = readByte(in);
			return new ForceWAVEAllRequest(waveId);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

