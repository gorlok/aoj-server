package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SendNightResponse extends ServerPacket {
	// SendNight,b:night
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SendNight;
	}
	public byte night;
	public SendNightResponse(byte night){
		this.night = night;
	}
	public static SendNightResponse decode(ByteBuf in) {    
		try {                                   
			byte night = readByte(in);
			return new SendNightResponse(night);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,night);
	}
};

