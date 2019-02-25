package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CentinelReportRequest extends ClientPacket {
	// CentinelReport,i:key
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CentinelReport;
	}
	public short key;
	public CentinelReportRequest(short key){
		this.key = key;
	}
};

