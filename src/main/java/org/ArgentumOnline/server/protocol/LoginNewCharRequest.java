package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

