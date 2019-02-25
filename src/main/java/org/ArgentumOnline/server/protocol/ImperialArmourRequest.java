package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ImperialArmourRequest extends ClientPacket {
	// ImperialArmour,b:index,i:objIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ImperialArmour;
	}
	public byte index;
	public short objIndex;
	public ImperialArmourRequest(byte index,short objIndex){
		this.index = index;
		this.objIndex = objIndex;
	}
};

