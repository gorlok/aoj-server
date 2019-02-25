package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class TrainListRequest extends ClientPacket {
	// TrainList
	@Override
	public ClientPacketID id() {
		return ClientPacketID.TrainList;
	}
	public TrainListRequest(){
	}
};

