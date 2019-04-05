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

import org.argentumonline.server.GamblerStats;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.user.Player;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Util;

/**
 * @author gorlok
 */
public class NpcGambler extends Npc {

	protected NpcGambler(int npc_number, GameServer server) {
		super(npc_number, server);
	}		

	public void bet(Player player, int gold) {
		// Comando /APOSTAR
		
		if (gold < 0) {
			player.talk(COLOR_BLANCO, "Has ingresado una apuesta inválida.", getId());
			return;
		}
		
		if (gold < 1) {
			player.talk(COLOR_BLANCO, "El mínimo de apuesta es 1 moneda.", getId());
			return;
		}
		
		if (gold > APUESTA_MAXIMA) {
			player.talk(COLOR_BLANCO, "El máximo de apuesta es " + APUESTA_MAXIMA + " monedas.", getId());
			return;
		}
		
		if (player.stats().getGold() < gold) {
			switch (player.gender()) {
			case GENERO_HOMBRE:
				player.talk(COLOR_BLANCO, "No tienes esa cantidad, embustero!", getId());
				break;
			case GENERO_MUJER:
				player.talk(COLOR_BLANCO, "No tienes esa cantidad, embustera!", getId());
				break;
			};
			return;
		}
		
		if (Util.Azar(1, 100) <= 45) {
			player.stats().addGold( gold );
			player.talk(COLOR_BLANCO, "Felicidades! Has ganado " + gold + " monedas de oro!", getId());
			
			server.getGamblerStats().incrementLost(gold);
		} else {
			player.stats().addGold( -gold );
			player.talk(COLOR_BLANCO, "Lo siento, has perdido " + gold + " monedas de oro.", getId());
			
			server.getGamblerStats().incrementWins(gold);
		}
		player.sendUpdateUserStats();
	}

	public void balance(Player player) {
		if (player.flags().isGM()) {
			var gamblerStats = server.getGamblerStats();
			
            long earnings = gamblerStats.getGanancias() - gamblerStats.getPerdidas();
            
            int percentage = 0;
            if (earnings >= 0 && gamblerStats.getGanancias() > 0) {
            	percentage = (int) (earnings * 100 / gamblerStats.getGanancias()); 
            }
            
            if (earnings < 0 && gamblerStats.getPerdidas() > 0) {
            	percentage = (int) (earnings * 100 / gamblerStats.getPerdidas()); 
            }
            
            player.sendMessage(
            		"Entradas: " + gamblerStats.getGanancias() +
            		" Salidas: " + gamblerStats.getPerdidas() +
            		" Ganancia Neta: " + earnings + " (" + percentage + "%)" +
            		" Jugadas: " + gamblerStats.getJugadas(), 
            		FontType.FONTTYPE_INFO);
		}
	}

	
}
