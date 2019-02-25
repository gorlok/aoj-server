package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CharacterInfoResponse extends ServerPacket {
	// CharacterInfo,s:charName,b:race,b:clazz,b:gender,b:level,l:gold,l:bank,l:reputation,s:previousPetitions,s:currentGuild,s:previousGuilds,b:royalArmy,b:caosLegion,l:citizensKilled,l:criminalsKilled
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterInfo;
	}
	public String charName;
	public byte race;
	public byte clazz;
	public byte gender;
	public byte level;
	public int gold;
	public int bank;
	public int reputation;
	public String previousPetitions;
	public String currentGuild;
	public String previousGuilds;
	public byte royalArmy;
	public byte caosLegion;
	public int citizensKilled;
	public int criminalsKilled;
	public CharacterInfoResponse(String charName,byte race,byte clazz,byte gender,byte level,int gold,int bank,int reputation,String previousPetitions,String currentGuild,String previousGuilds,byte royalArmy,byte caosLegion,int citizensKilled,int criminalsKilled){
		this.charName = charName;
		this.race = race;
		this.clazz = clazz;
		this.gender = gender;
		this.level = level;
		this.gold = gold;
		this.bank = bank;
		this.reputation = reputation;
		this.previousPetitions = previousPetitions;
		this.currentGuild = currentGuild;
		this.previousGuilds = previousGuilds;
		this.royalArmy = royalArmy;
		this.caosLegion = caosLegion;
		this.citizensKilled = citizensKilled;
		this.criminalsKilled = criminalsKilled;
	}
};

