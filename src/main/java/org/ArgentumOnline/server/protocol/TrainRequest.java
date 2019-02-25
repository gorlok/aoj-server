package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TrainRequest extends ClientPacket {
	// Train,b:petIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Train;
	}
	public byte petIndex;
	public TrainRequest(byte petIndex){
		this.petIndex = petIndex;
	}
};

