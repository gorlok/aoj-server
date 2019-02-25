package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlacksmithArmorsResponse extends ServerPacket {
	// BlacksmithArmors,i:count,(s:name,i:lingH,i:lingP,i:lingO,i:index)[.]:armors
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlacksmithArmors;
	}
	public short count;
	public BlacksmithArmors_DATA[] armors;
	public BlacksmithArmorsResponse(short count,BlacksmithArmors_DATA[] armors){
		this.count = count;
		this.armors = armors;
	}
};

