package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BugReportRequest extends ClientPacket {
	// BugReport,s:bugReport
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BugReport;
	}
	public String bugReport;
	public BugReportRequest(String bugReport){
		this.bugReport = bugReport;
	}
	public static BugReportRequest decode(ByteBuf in) {    
		try {                                   
			String bugReport = readStr(in);
			return new BugReportRequest(bugReport);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

