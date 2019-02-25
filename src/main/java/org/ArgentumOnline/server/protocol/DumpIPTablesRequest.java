package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DumpIPTablesRequest extends ClientPacket {
	// DumpIPTables
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DumpIPTables;
	}
	public DumpIPTablesRequest(){
	}
};

