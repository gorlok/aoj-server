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

	public void retirarOroBanco(Player client, int cant) {
		if (this.pos().distance(pos()) > 10) {
			client.enviarMensaje("||Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return;
		}
		
		if (!client.existePersonaje()) {
			log.error("ERROR, no existe el personaje " + client.getNick());
			client.enviarMensaje("¡¡El personaje no existe, cree uno nuevo!!", FontType.FONTTYPE_WARNING);
			client.doSALIR();
			return;
		}
		if (cant > 0 && cant <= client.stats().getBankGold()) {
			client.stats().addBankGold( -cant );
			client.stats().addGold( cant );
			client.hablar(COLOR_BLANCO, "Tienes " + client.stats().getBankGold() + " monedas de oro en tu cuenta.", this.getId());
		} else {
			client.hablar(COLOR_BLANCO, "No tienes esa cantidad.", this.getId());
		}
		client.sendUpdateUserStats();
	}

	public void depositarOroBanco(Player client, int cant) {
		if (this.pos().distance(pos()) > 10) {
			client.enviarMensaje("||Estas demasiado lejos.", FontType.FONTTYPE_INFO);
			return;
		}
		// ¿Se tiene dicha cantidad realmente?
		if (cant > 0 && cant <= client.stats().getGold()) {
			client.stats().addBankGold( cant );
			client.stats().addGold( -cant );
			client.hablar(COLOR_BLANCO, "Tienes " + client.stats().getBankGold() + " monedas de oro en tu cuenta.", this.getId());
		} else {
			client.hablar(COLOR_BLANCO, "No tienes esa cantidad.", this.getId());
		}
		client.sendUpdateUserStats();
	}


}
