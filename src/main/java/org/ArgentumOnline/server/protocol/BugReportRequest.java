package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

