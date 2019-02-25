package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChaosArmourRequest extends ClientPacket {
	// ChaosArmour,b:index,i:objIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChaosArmour;
	}
	public byte index;
	public short objIndex;
	public ChaosArmourRequest(byte index,short objIndex){
		this.index = index;
		this.objIndex = objIndex;
	}
};

