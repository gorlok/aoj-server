package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static CentinelReportRequest decode(ByteBuf in) {    
		try {                                   
			short key = readShort(in);
			return new CentinelReportRequest(key);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

