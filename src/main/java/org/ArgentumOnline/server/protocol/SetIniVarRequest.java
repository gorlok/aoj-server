package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class SetIniVarRequest extends ClientPacket {
	// SetIniVar,s:section,s:key,s:value
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetIniVar;
	}
	public String section;
	public String key;
	public String value;
	public SetIniVarRequest(String section,String key,String value){
		this.section = section;
		this.key = key;
		this.value = value;
	}
	public static SetIniVarRequest decode(ByteBuf in) {    
		try {                                   
			String section = readStr(in);
			String key = readStr(in);
			String value = readStr(in);
			return new SetIniVarRequest(section,key,value);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

