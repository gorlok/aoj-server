package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AlterNameRequest extends ClientPacket {
	// AlterName,s:userName,s:newName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AlterName;
	}
	public String userName;
	public String newName;
	public AlterNameRequest(String userName,String newName){
		this.userName = userName;
		this.newName = newName;
	}
	public static AlterNameRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String newName = readStr(in);
			return new AlterNameRequest(userName,newName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

