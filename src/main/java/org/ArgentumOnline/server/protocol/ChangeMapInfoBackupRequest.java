package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static ChangeMapInfoBackupRequest decode(ByteBuf in) {    
		try {                                   
			byte doTheBackup = readByte(in);
			return new ChangeMapInfoBackupRequest(doTheBackup);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

