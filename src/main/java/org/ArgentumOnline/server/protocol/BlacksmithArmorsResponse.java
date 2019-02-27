package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BlacksmithArmorsResponse extends ServerPacket {
	// BlacksmithArmors,i:count,(s:name,i:lingH,i:lingP,i:lingO,i:index)[.]:armors
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlacksmithArmors;
	}
	public short count;
	public BlacksmithArmors_DATA[] armors;
	public BlacksmithArmorsResponse(short count,BlacksmithArmors_DATA[] armors){
		this.count = count;
		this.armors = armors;
	}
	public static BlacksmithArmorsResponse decode(ByteBuf in) {    
		try {                                   
			short count = readShort(in);
			
			BlacksmithArmors_DATA[] armors = new BlacksmithArmors_DATA[count];
			for (int i = 0; i < count; i++) {
				armors[i].name = readStr(in);
				armors[i].lingH = readShort(in);
				armors[i].lingP = readShort(in);
				armors[i].lingO = readShort(in);
				armors[i].index = readShort(in);
			}
			
			return new BlacksmithArmorsResponse(count,armors);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,count);
		
		for (int i = 0; i < count; i++) {
			writeStr(out, armors[i].name);
			writeShort(out, armors[i].lingH);
			writeShort(out, armors[i].lingP);
			writeShort(out, armors[i].lingO);
			writeShort(out, armors[i].index);
		}
	}
};

