/**
* UserTrade.java
*
* Created on 23 de febrero de 2004, 21:38
*
   AOJava Server
   Copyright (C) 2003-2007 Pablo Fernando Lillia (alias Gorlok)
   Web site: http://www.aojava.com.ar

   This file is part of AOJava.

   AOJava is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   AOJava is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Foobar; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package org.ArgentumOnline.server;

import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.protocol.UserCommerceEndResponse;
import org.ArgentumOnline.server.util.FontType;

/**
 * @author gorlok
 */
public class UserTrade {

	short destUsu = 0; // El otro Usuario

	short objeto = 0; // Indice del inventario a comerciar, que objeto desea dar

	// El tipo de datos de Cant ahora es Long (antes Integer)
	// asi se puede comerciar con oro > 32k
	int cant = 0; // Cantidad a comerciar

	boolean acepto = false;
	
	private Player player;
	
	public UserTrade(Player player) {
		this.player = player;
	}
	
	//origen: origen de la transaccion, originador del comando
	//destino: receptor de la transaccion
	public void iniciarComercioConUsuario(short target) {
		Player targetPlayer = GameServer.instance().playerById(target);
		if (targetPlayer == null) {
			return;
		}
		//Si ambos pusieron /comerciar entonces
		if (destUsu == targetPlayer.getId() && targetPlayer.m_comUsu.destUsu == player.getId()) {
			// FIXME
//		    //Actualiza el inventario del usuario
//		    Call UpdateUserInv(True, Origen, 0)
//		    //Decirle al origen que abra la ventanita.
//		    Call WriteUserCommerceInit(Origen)
//		    UserList(Origen).flags.Comerciando = True
//	
//		    //Actualiza el inventario del usuario
//		    Call UpdateUserInv(True, Destino, 0)
//		    //Decirle al origen que abra la ventanita.
//		    Call WriteUserCommerceInit(Destino)
//		    UserList(Destino).flags.Comerciando = True
	
		} else {
		    //Es el primero que comercia ?
			player.enviarMensaje(targetPlayer.getNick() + " desea comerciar. Si deseas aceptar, Escribe /COMERCIAR.", FontType.FONTTYPE_TALK);
			targetPlayer.flags().TargetUser = player.getId();
		}
	}
	

	public void userCommerceAccept() {
		// Comando COMUSUOK
		// Aceptar el cambio

		if (this.destUsu == 0) {
			return;
		}
		Player targetClient = player.server.playerById(this.destUsu);
		if (this.destUsu != player.getId()) {
			return;
		}
		this.acepto = true;
		if (!this.acepto) {
			player.enviarMensaje("El otro usuario aun no ha aceptado tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		boolean terminarAhora = false;
		short obj1_objid = 0;
		int obj1_cant = 0;
		if (this.objeto == Constants.FLAGORO) {
			obj1_objid = Constants.OBJ_ORO;
			if (this.cant > player.m_estads.getGold()) {
				player.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj1_cant = this.cant;
			obj1_objid = player.m_inv.getObjeto(this.objeto).objid;
			if (obj1_cant > player.m_inv.getObjeto(this.objeto).cant) {
				player.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		}

		short obj2_objid = 0;
		int obj2_cant = 0;
		if (this.objeto == Constants.FLAGORO) {
			obj2_objid = Constants.OBJ_ORO;
			if (this.cant > targetClient.m_estads.getGold()) {
				targetClient.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj2_cant = this.cant;
			obj2_objid = targetClient.m_inv.getObjeto(this.objeto).objid;
			if (obj2_cant > targetClient.m_inv.getObjeto(this.objeto).cant) {
				targetClient.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		}
		// Por si las moscas...
		if (terminarAhora) {
			sendCommerceEnded();
			targetClient.m_comUsu.sendCommerceEnded();
			return;
		}
		// pone el oro directamente en la billetera
		if (this.objeto == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			targetClient.m_estads.addGold(-this.cant);
			targetClient.sendUpdateUserStats();
			// y se la doy al otro
			player.m_estads.addGold(this.cant);
			player.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = player.m_inv.agregarItem(obj2_objid, obj2_cant);
			if (agregados < obj2_cant) {
				// Tiro al piso lo que no pude guardar en el inventario.
				Map mapa = player.server.getMap(player.pos().map);
				mapa.tirarItemAlPiso(player.pos().x, player.pos().y,
						new InventoryObject(obj2_objid, obj2_cant - agregados));
			}
			targetClient.quitarObjetos(obj2_objid, obj2_cant);
		}
		// pone el oro directamente en la billetera
		if (this.objeto == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			player.m_estads.addGold(-this.cant); // restar
			player.sendUpdateUserStats();
			// y se la doy al otro
			targetClient.m_estads.addGold(this.cant); // sumar
			targetClient.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = targetClient.m_inv.agregarItem(obj1_objid, obj1_objid);
			if (agregados < obj1_cant) {
				// Tiro al piso los items que no se agregaron al inventario.
				Map mapa = player.server.getMap(targetClient.pos().map);
				mapa.tirarItemAlPiso(targetClient.pos().x, targetClient.pos().y,
						new InventoryObject(obj1_objid, obj1_objid - agregados));
			}
			player.quitarObjetos(obj1_objid, obj1_cant);
		}
		player.enviarInventario();
		targetClient.enviarInventario();
		sendCommerceEnded();
		targetClient.m_comUsu.sendCommerceEnded();
	}

	public void userCommerceEnd() {
		// Comando FINCOMUSU
		// Salir del modo comercio Usuario
		if (this.destUsu > 0 && this.destUsu == player.getId()) {
			Player targetClient = player.server.playerById(this.destUsu);
			if (targetClient != null) {
				targetClient.enviarMensaje(player.m_nick + " ha dejado de comerciar con vos.", FontType.FONTTYPE_TALK);
				if (targetClient != null) {
					targetClient.m_comUsu.sendCommerceEnded();
				}
			}
		}
		sendCommerceEnded();
	}

	private void sendCommerceEnded() {
		this.acepto = false;
		this.cant = 0;
		this.destUsu = 0;
		this.objeto = 0;
		player.flags().Comerciando = false;
		player.sendPacket(new UserCommerceEndResponse());
	}

	public void userCommerceReject() {
		// Comando COMUSUNO
		// Rechazar el cambio
		if (this.destUsu > 0) {
			Player targetClient = player.server.playerById(this.destUsu);
			targetClient.enviarMensaje(player.m_nick + " ha rechazado tu oferta.", FontType.FONTTYPE_TALK);
			targetClient.m_comUsu.sendCommerceEnded();
		}
		player.enviarMensaje("Has rechazado la oferta del otro usuario.", FontType.FONTTYPE_TALK);
		sendCommerceEnded();
	}

	public void userCommerceOffer(short slot, int amount) {
		// Comando OFRECER
		if (slot < 1 || amount < 1) {
			return;
		}
		if (this.destUsu == 0) {
			return;
		}
		Player targetClient = player.server.playerById(this.destUsu);
		// sigue conectado el usuario ?
		if (!targetClient.flags().UserLogged) {
			sendCommerceEnded();
			return;
		}
		// esta vivo ?
		if (!targetClient.isAlive()) {
			sendCommerceEnded();
			return;
		}
		// Tiene la cantidad que se ofrece ??
		if (slot == Constants.FLAGORO) {
			// oro de la billetera
			if (amount > player.m_estads.getGold()) {
				player.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		} else {
			// objeto del inventario
			if (amount > player.m_inv.getObjeto(slot).cant) {
				player.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		}
		if (this.objeto > 0) {
			player.enviarMensaje("No puedes cambiar tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		this.objeto = slot;
		this.cant = amount;
		if (this.destUsu != player.getId()) {
			sendCommerceEnded();
			return;
		}
		if (this.acepto) {
			// NO NO NO vos te estas pasando de listo...
			this.acepto = false;
			targetClient.enviarMensaje(player.m_nick + " ha cambiado su oferta.", FontType.FONTTYPE_TALK);
		}
		// Es la ofrenda de respuesta :)
		targetClient.m_comUsu.recibirObjetoTransaccion(targetClient);
	}

	void recibirObjetoTransaccion(Player client) {
		if (this.destUsu == 0) {
			return;
		}
		Player targetClient = client.server.playerById(this.destUsu);

		short objid = 0;
		if (this.objeto == Constants.FLAGORO) {
			objid = Constants.OBJ_ORO;
		} else {
			objid = targetClient.m_inv.getObjeto(this.objeto).objid;
		}
		if (this.cant <= 0 || objid <= 0) {
			return;
		}
		if (objid > 0 && this.cant > 0) {
			player.updateVentanaComercio(objid, cant);
		}
	}
	
}
