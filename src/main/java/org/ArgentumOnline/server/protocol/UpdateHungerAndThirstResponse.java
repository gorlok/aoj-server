package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

