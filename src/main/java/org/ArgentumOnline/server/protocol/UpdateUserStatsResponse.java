package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class UpdateUserStatsResponse extends ServerPacket {
	// UpdateUserStats,i:maxHP,i:minHP,i:maxMAN,i:minMAN,i:maxSTA,i:minSTA,l:gold,b:elv,l:elu,l:exp
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateUserStats;
	}
	public short maxHP;
	public short minHP;
	public short maxMAN;
	public short minMAN;
	public short maxSTA;
	public short minSTA;
	public int gold;
	public byte elv;
	public int elu;
	public int exp;
	public UpdateUserStatsResponse(short maxHP,short minHP,short maxMAN,short minMAN,short maxSTA,short minSTA,int gold,byte elv,int elu,int exp){
		this.maxHP = maxHP;
		this.minHP = minHP;
		this.maxMAN = maxMAN;
		this.minMAN = minMAN;
		this.maxSTA = maxSTA;
		this.minSTA = minSTA;
		this.gold = gold;
		this.elv = elv;
		this.elu = elu;
		this.exp = exp;
	}
	public static UpdateUserStatsResponse decode(ByteBuf in) {    
		try {                                   
			short maxHP = readShort(in);
			short minHP = readShort(in);
			short maxMAN = readShort(in);
			short minMAN = readShort(in);
			short maxSTA = readShort(in);
			short minSTA = readShort(in);
			int gold = readInt(in);
			byte elv = readByte(in);
			int elu = readInt(in);
			int exp = readInt(in);
			return new UpdateUserStatsResponse(maxHP,minHP,maxMAN,minMAN,maxSTA,minSTA,gold,elv,elu,exp);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

