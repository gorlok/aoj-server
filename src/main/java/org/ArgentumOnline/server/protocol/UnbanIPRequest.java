package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UnbanIPRequest extends ClientPacket {
	// UnbanIP,b:ip1,b:ip2,b:ip3,b:ip4
	@Override
	public ClientPacketID id() {
		return ClientPacketID.UnbanIP;
	}
	public byte ip1;
	public byte ip2;
	public byte ip3;
	public byte ip4;
	public UnbanIPRequest(byte ip1,byte ip2,byte ip3,byte ip4){
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.ip3 = ip3;
		this.ip4 = ip4;
	}
};
