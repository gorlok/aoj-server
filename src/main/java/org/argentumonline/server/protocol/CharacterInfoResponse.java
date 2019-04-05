/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.protocol;

import org.argentumonline.server.net.*;

import io.netty.buffer.ByteBuf;

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
	public static CharacterInfoResponse decode(ByteBuf in) {    
		try {                                   
			String charName = readStr(in);
			byte race = readByte(in);
			byte clazz = readByte(in);
			byte gender = readByte(in);
			byte level = readByte(in);
			int gold = readInt(in);
			int bank = readInt(in);
			int reputation = readInt(in);
			String previousPetitions = readStr(in);
			String currentGuild = readStr(in);
			String previousGuilds = readStr(in);
			byte royalArmy = readByte(in);
			byte caosLegion = readByte(in);
			int citizensKilled = readInt(in);
			int criminalsKilled = readInt(in);
			return new CharacterInfoResponse(charName,race,clazz,gender,level,gold,bank,reputation,previousPetitions,currentGuild,previousGuilds,royalArmy,caosLegion,citizensKilled,criminalsKilled);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,charName);
		writeByte(out,race);
		writeByte(out,clazz);
		writeByte(out,gender);
		writeByte(out,level);
		writeInt(out,gold);
		writeInt(out,bank);
		writeInt(out,reputation);
		writeStr(out,previousPetitions);
		writeStr(out,currentGuild);
		writeStr(out,previousGuilds);
		writeByte(out,royalArmy);
		writeByte(out,caosLegion);
		writeInt(out,citizensKilled);
		writeInt(out,criminalsKilled);
	}
};

