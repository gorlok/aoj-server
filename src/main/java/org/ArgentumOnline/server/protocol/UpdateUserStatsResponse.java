package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

