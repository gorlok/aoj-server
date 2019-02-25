package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class EditCharRequest extends ClientPacket {
	// EditChar,s:userName,b:option,s:param1,s:param2
	@Override
	public ClientPacketID id() {
		return ClientPacketID.EditChar;
	}
	public String userName;
	public byte option;
	public String param1;
	public String param2;
	public EditCharRequest(String userName,byte option,String param1,String param2){
		this.userName = userName;
		this.option = option;
		this.param1 = param1;
		this.param2 = param2;
	}
	public static EditCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			byte option = readByte(in);
			String param1 = readStr(in);
			String param2 = readStr(in);
			return new EditCharRequest(userName,option,param1,param2);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

