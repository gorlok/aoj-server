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
	
	public void doBalance(Player client) {
		// Comando /BALANCE
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!client.checkAlive()) {
			return;
		}
		// Se asegura que el target es un npc
		if (client.flags().TargetNpc == 0) {
			client.enviarMensaje("Primero tenes que seleccionar un personaje, hace clic izquierdo sobre el.", FontType.FONTTYPE_INFO);
			return;
		}
		Npc npc = server.getNpcById(client.flags().TargetNpc);
		if (npc.pos().distance(m_pos) > 3) {
			client.enviarMensaje("Estas demasiado lejos del vendedor.", FontType.FONTTYPE_INFO);
			return;
		}
		if (!npc.isBankCashier() || !client.isAlive()) {
			return;
		}
		if (!client.existePersonaje()) {
			client.enviarMensaje("!!El personaje no existe, cree uno nuevo.", FontType.FONTTYPE_INFO);
			client.doSALIR();
			return;
		}
		client.hablar(COLOR_BLANCO, "Tienes " + client.stats().getBankGold() + " monedas de oro en tu cuenta.", npc.getId());
	}

	public void retirarOroBanco(Player client, int cant) {
		if (this.pos().distance(m_pos) > 10) {
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
		if (this.pos().distance(m_pos) > 10) {
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
