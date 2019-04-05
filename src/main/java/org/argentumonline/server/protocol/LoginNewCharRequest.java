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

import org.argentumonline.server.Skill;
import org.argentumonline.server.net.*;

import io.netty.buffer.ByteBuf;

public class LoginNewCharRequest extends ClientPacket {
	// LoginNewChar,s:userName,s:password,b:version1,b:version2,b:version3,i:versionGrafs,i:versionWavs,i:versionMidis,i:versionInits,i:versionMapas,i:versionAoExe,i:versionExtras,b:race,b:gender,b:clazz,b[NUMSKILLS]:skills,s:email,b:homeland
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LoginNewChar;
	}
	public String userName;
	public String password;
	public byte version1;
	public byte version2;
	public byte version3;
	public short versionGrafs;
	public short versionWavs;
	public short versionMidis;
	public short versionInits;
	public short versionMapas;
	public short versionAoExe;
	public short versionExtras;
	public byte race;
	public byte gender;
	public byte clazz;
	public byte[] skills;
	public String email;
	public byte homeland;
	public LoginNewCharRequest(String userName,String password,byte version1,byte version2,byte version3,short versionGrafs,short versionWavs,short versionMidis,short versionInits,short versionMapas,short versionAoExe,short versionExtras,byte race,byte gender,byte clazz,byte[] skills,String email,byte homeland){
		this.userName = userName;
		this.password = password;
		this.version1 = version1;
		this.version2 = version2;
		this.version3 = version3;
		this.versionGrafs = versionGrafs;
		this.versionWavs = versionWavs;
		this.versionMidis = versionMidis;
		this.versionInits = versionInits;
		this.versionMapas = versionMapas;
		this.versionAoExe = versionAoExe;
		this.versionExtras = versionExtras;
		this.race = race;
		this.gender = gender;
		this.clazz = clazz;
		this.skills = skills;
		this.email = email;
		this.homeland = homeland;
	}
	public static LoginNewCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String password = readStr(in);
			byte version1 = readByte(in);
			byte version2 = readByte(in);
			byte version3 = readByte(in);
			short versionGrafs = readShort(in);
			short versionWavs = readShort(in);
			short versionMidis = readShort(in);
			short versionInits = readShort(in);
			short versionMapas = readShort(in);
			short versionAoExe = readShort(in);
			short versionExtras = readShort(in);
			byte race = readByte(in);
			byte gender = readByte(in);
			byte clazz = readByte(in);
			byte[] skills = readBytes(in, Skill.values().length);
			String email = readStr(in);
			byte homeland = readByte(in);
			return new LoginNewCharRequest(userName,password,version1,version2,version3,versionGrafs,versionWavs,versionMidis,versionInits,versionMapas,versionAoExe,versionExtras,race,gender,clazz,skills,email,homeland);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

