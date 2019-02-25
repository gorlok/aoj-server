package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class LoginExistingCharRequest extends ClientPacket {
	// LoginExistingChar,s:userName,s:password,b:version1,b:version2,b:version3,i:versionGrafs,i:versionWavs,i:versionMidis,i:versionInits,i:versionMapas,i:versionAoExe,i:versionExtras
	@Override
	public ClientPacketID id() {
		return ClientPacketID.LoginExistingChar;
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
	public LoginExistingCharRequest(String userName,String password,byte version1,byte version2,byte version3,short versionGrafs,short versionWavs,short versionMidis,short versionInits,short versionMapas,short versionAoExe,short versionExtras){
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
	}
};

