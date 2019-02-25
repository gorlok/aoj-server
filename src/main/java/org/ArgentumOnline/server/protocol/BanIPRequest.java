package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BanIPRequest extends ClientPacket {
	// BanIP,b:ip1,b:ip2,b:ip3,b:ip4,s:reason
	@Override
	public ClientPacketID id() {
		return ClientPacketID.BanIP;
	}
	public byte ip1;
	public byte ip2;
	public byte ip3;
	public byte ip4;
	public String reason;
	public BanIPRequest(byte ip1,byte ip2,byte ip3,byte ip4,String reason){
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.ip3 = ip3;
		this.ip4 = ip4;
		this.reason = reason;
	}
	public static BanIPRequest decode(ByteBuf in) {    
		try {                                   
			byte ip1 = readByte(in);
			byte ip2 = readByte(in);
			byte ip3 = readByte(in);
			byte ip4 = readByte(in);
			String reason = readStr(in);
			return new BanIPRequest(ip1,ip2,ip3,ip4,reason);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

