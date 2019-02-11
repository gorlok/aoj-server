package org.ArgentumOnline.server.protocol;

/**
*
* @author: JAO (Juan Agustín Oliva)
* @userforos: Agushh, Thorkes
* FIXME
*/


public enum serverPacketID { 
	login		        ((byte) 1),
	msgErr              ((byte) 2),
	IP                  ((byte) 3), // index pj
	CC                  ((byte) 4), //create char
	CMP                 ((byte) 5), //change map
	talk                ((byte) 6), // dialog console
	dialog              ((byte) 7), //dialog 
	MSG_CCNPC           ((byte) 8), //create npc
	MSG_PU              ((byte) 9), //change heading
	MSG_HO              ((byte)10), //create object
	MSG_MP              ((byte)11), //move char
	MSG_BO              ((byte)12), //delete object
	MSG_BQ              ((byte)13), //bloq position
	MSG_TW              ((byte)14), //play music
	MSG_CP              ((byte)15), //character change
	MSG_FX              ((byte)16), //create FX
	MSG_CSI             ((byte)17), //change inventory slot
	MSG_SHS             ((byte)18), //change spell slot
	MSG_BP              ((byte)19), //character remove
	MSG_TO1             ((byte)20), // msgs
	MSG_NPC_INV         ((byte)21), //change npc inventory
	MSG_REFRESH         ((byte)22), //refresh user status
	MSG_FINCOM          ((byte)23), // commerce end
	MSG_USERHITNPC      ((byte)24),
	MSG_USERSWING       ((byte)25), // swing hit
	tradeOk             ((byte)26), //update commerce pics
	medOk               ((byte)27), // update user med
	deleteObject        ((byte)28), //delete user item
	dropDices           ((byte)29),
	littleStats         ((byte)30),
	userAtri            ((byte)31),
	userSkills          ((byte)32),
	msgN1               ((byte)33),
	paradOk             ((byte)34),
	wBank               ((byte)35),
	oBank               ((byte)36),
	fBank               ((byte)37),
	userWork            ((byte)38),
	userRain            ((byte)39),
	finOk               ((byte)40),
	userFame            ((byte)41),
	bancoOk             ((byte)42),
	safeToggle          ((byte)43),
	updateStats         ((byte)44),
	areasChange         ((byte)45),
	navigateToggle      ((byte)46);
	
	private final byte binCode; 
	
	serverPacketID(byte bin) {
		this.binCode = bin;
	}
	
	public byte binCode() {
		return this.binCode;
	}
	
}


