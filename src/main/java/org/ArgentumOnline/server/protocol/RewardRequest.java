package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class RewardRequest extends ClientPacket {
	// Reward
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Reward;
	}
	public RewardRequest(){
	}
};

