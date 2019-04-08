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
package org.argentumonline.server.npc;

import static org.argentumonline.server.util.Color.COLOR_BLANCO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;

public class NpcCashier extends Npc {
	private static Logger log = LogManager.getLogger();

	protected NpcCashier(int npc_numero, GameServer server) {
		super(npc_numero, server);
	}
	
	public void balance(User user) {
		// Comando /BALANCE
		if (!user.checkAlive()) {
			return;
		}
		user.talk(COLOR_BLANCO, "Tienes " + user.getStats().getBankGold() + " monedas de oro en tu cuenta.", getId());
	}

	public void retirarOroBanco(User user, int cant) {
		if (this.pos().distance(pos()) > 10) {
			user.sendMessage("||Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (!user.userExists()) {
			log.error("ERROR, no existe el personaje " + user.getUserName());
			user.sendMessage("¡¡El personaje no existe, cree uno nuevo!!", FontType.FONTTYPE_WARNING);
			user.quitGame();
			return;
		}
		if (cant > 0 && cant <= user.getStats().getBankGold()) {
			user.getStats().addBankGold( -cant );
			user.getStats().addGold( cant );
			user.talk(COLOR_BLANCO, "Tienes " + user.getStats().getBankGold() + " monedas de oro en tu cuenta.", this.getId());
		} else {
			user.talk(COLOR_BLANCO, "No tienes esa cantidad.", this.getId());
		}
		user.sendUpdateUserStats();
	}

	public void depositarOroBanco(User user, int cant) {
		if (this.pos().distance(pos()) > 10) {
			user.sendMessage("||Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return;
		}
		// ¿Se tiene dicha cantidad realmente?
		if (cant > 0 && cant <= user.getStats().getGold()) {
			user.getStats().addBankGold( cant );
			user.getStats().addGold( -cant );
			user.talk(COLOR_BLANCO, "Tienes " + user.getStats().getBankGold() + " monedas de oro en tu cuenta.", this.getId());
		} else {
			user.talk(COLOR_BLANCO, "No tienes esa cantidad.", this.getId());
		}
		user.sendUpdateUserStats();
	}


}
