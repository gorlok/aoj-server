package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoBackupRequest extends ClientPacket {
	// ChangeMapInfoBackup,b:doTheBackup
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoBackup;
	}
	public byte doTheBackup;
	public ChangeMapInfoBackupRequest(byte doTheBackup){
		this.doTheBackup = doTheBackup;
	}
};

