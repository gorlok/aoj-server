package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class DumpIPTablesRequest extends ClientPacket {
	// DumpIPTables
	@Override
	public ClientPacketID id() {
		return ClientPacketID.DumpIPTables;
	}
	public DumpIPTablesRequest(){
	}
	public static DumpIPTablesRequest decode(ByteBuf in) {    
		try {                                   
			return new DumpIPTablesRequest();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

