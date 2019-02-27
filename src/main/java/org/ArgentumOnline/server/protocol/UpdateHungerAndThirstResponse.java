package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateHungerAndThirstResponse extends ServerPacket {
	// UpdateHungerAndThirst,b:maxAGU,b:minAGU,b:maxHAM,b:minHAM
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateHungerAndThirst;
	}
	public byte maxAGU;
	public byte minAGU;
	public byte maxHAM;
	public byte minHAM;
	public UpdateHungerAndThirstResponse(byte maxAGU,byte minAGU,byte maxHAM,byte minHAM){
		this.maxAGU = maxAGU;
		this.minAGU = minAGU;
		this.maxHAM = maxHAM;
		this.minHAM = minHAM;
	}
	public static UpdateHungerAndThirstResponse decode(ByteBuf in) {    
		try {                                   
			byte maxAGU = readByte(in);
			byte minAGU = readByte(in);
			byte maxHAM = readByte(in);
			byte minHAM = readByte(in);
			return new UpdateHungerAndThirstResponse(maxAGU,minAGU,maxHAM,minHAM);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,maxAGU);
		writeByte(out,minAGU);
		writeByte(out,maxHAM);
		writeByte(out,minHAM);
	}
};

