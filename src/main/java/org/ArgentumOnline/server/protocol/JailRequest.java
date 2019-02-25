package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class JailRequest extends ClientPacket {
	// Jail,s:userName,s:reason,b:jailTime
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Jail;
	}
	public String userName;
	public String reason;
	public byte jailTime;
	public JailRequest(String userName,String reason,byte jailTime){
		this.userName = userName;
		this.reason = reason;
		this.jailTime = jailTime;
	}
	public static JailRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String reason = readStr(in);
			byte jailTime = readByte(in);
			return new JailRequest(userName,reason,jailTime);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

