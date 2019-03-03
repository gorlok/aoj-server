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
package org.ArgentumOnline.server.npc;

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.util.FontType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NpcCashier extends Npc {
	private static Logger log = LogManager.getLogger();

	protected NpcCashier(int npc_numero, GameServer server) {
		super(npc_numero, server);
	}
	
	public void balance(Player player) {
		// Comando /BALANCE
		if (!player.checkAlive()) {
			return;
		}
		player.hablar(COLOR_BLANCO, "Tienes " + player.stats().getBankGold() + " monedas de oro en tu cuenta.", getId());
	}

	public void retirarOroBanco(Player player, int cant) {
		if (this.pos().distance(pos()) > 10) {
			player.sendMessage("||Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (!player.existePersonaje()) {
			log.error("ERROR, no existe el personaje " + player.getNick());
			player.sendMessage("¡¡El personaje no existe, cree uno nuevo!!", FontType.FONTTYPE_WARNING);
			player.doSALIR();
			return;
		}
		if (cant > 0 && cant <= player.stats().getBankGold()) {
			player.stats().addBankGold( -cant );
			player.stats().addGold( cant );
			player.hablar(COLOR_BLANCO, "Tienes " + player.stats().getBankGold() + " monedas de oro en tu cuenta.", this.getId());
		} else {
			player.hablar(COLOR_BLANCO, "No tienes esa cantidad.", this.getId());
		}
		player.sendUpdateUserStats();
	}

	public void depositarOroBanco(Player player, int cant) {
		if (this.pos().distance(pos()) > 10) {
			player.sendMessage("||Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return;
		}
		// ¿Se tiene dicha cantidad realmente?
		if (cant > 0 && cant <= player.stats().getGold()) {
			player.stats().addBankGold( cant );
			player.stats().addGold( -cant );
			player.hablar(COLOR_BLANCO, "Tienes " + player.stats().getBankGold() + " monedas de oro en tu cuenta.", this.getId());
		} else {
			player.hablar(COLOR_BLANCO, "No tienes esa cantidad.", this.getId());
		}
		player.sendUpdateUserStats();
	}


}
