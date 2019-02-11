package org.ArgentumOnline.server.protocol;

/**
*
* @author: JAO (Juan Agustín Oliva)
* @userforos: Agushh, Thorkes
*/

public class clientPacketID {
	
	public enum ID {
        logged,
        move,
        talk,
        LC,
        RC,
        WLC,
        cDir,
        dirSP,
        LH,
        doUK,
        pickUp, //agarrar item
        Drop, //drop obj
        Equip, //equipar un item
        Attack, //ataca cuerpo a cuerpo
        useItem,
        endGame,
        commerceStart,
        commerceEnd,
        commerceBuy, //user buy item to npc
        commerceSell, //user sell item to npc
        meditate,
        refreshPos,
        createCharacter,
        throwDices,
        reciveUserStats,
        assignSkills,
        doBank,
        finBank,
        sellBank,
        buyBank,
        safeToggle;
	}
	

}
