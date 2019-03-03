package org.ArgentumOnline.server.npc;

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;

import org.ArgentumOnline.server.GamblerStats;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.Util;

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
			player.hablar(COLOR_BLANCO, "Has ingresado una apuesta inválida.", getId());
			return;
		}
		
		if (gold < 1) {
			player.hablar(COLOR_BLANCO, "El mínimo de apuesta es 1 moneda.", getId());
			return;
		}
		
		if (gold > APUESTA_MAXIMA) {
			player.hablar(COLOR_BLANCO, "El máximo de apuesta es " + APUESTA_MAXIMA + " monedas.", getId());
			return;
		}
		
		if (player.stats().getGold() < gold) {
			switch (player.gender()) {
			case GENERO_HOMBRE:
				player.hablar(COLOR_BLANCO, "No tienes esa cantidad, embustero!", getId());
				break;
			case GENERO_MUJER:
				player.hablar(COLOR_BLANCO, "No tienes esa cantidad, embustera!", getId());
				break;
			};
			return;
		}
		
		if (Util.Azar(1, 100) <= 45) {
			player.stats().addGold( gold );
			player.hablar(COLOR_BLANCO, "Felicidades! Has ganado " + gold + " monedas de oro!", getId());
			
			server.getGamblerStats().incrementLost(gold);
		} else {
			player.stats().addGold( -gold );
			player.hablar(COLOR_BLANCO, "Lo siento, has perdido " + gold + " monedas de oro.", getId());
			
			server.getGamblerStats().incrementWins(gold);
		}
		player.sendUpdateUserStats();
	}

	public void balance(Player player) {
		if (player.isGM()) {
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
