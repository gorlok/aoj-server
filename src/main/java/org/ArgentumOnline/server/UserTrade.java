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

	public void doAceptarComerciarUsuario(Player client) {
		// Comando COMUSUOK
		// Aceptar el cambio

		if (this.destUsu == 0) {
			return;
		}
		Player targetClient = client.server.getClientById(this.destUsu);
		if (this.destUsu != client.getId()) {
			return;
		}
		this.acepto = true;
		if (!this.acepto) {
			client.enviarMensaje("El otro usuario aun no ha aceptado tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		boolean terminarAhora = false;
		short obj1_objid = 0;
		int obj1_cant = 0;
		if (this.objeto == Constants.FLAGORO) {
			obj1_objid = Constants.OBJ_ORO;
			if (this.cant > client.m_estads.getGold()) {
				client.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj1_cant = this.cant;
			obj1_objid = client.m_inv.getObjeto(this.objeto).objid;
			if (obj1_cant > client.m_inv.getObjeto(this.objeto).cant) {
				client.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
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
			client.m_comUsu.finComerciarUsu(client);
			targetClient.m_comUsu.finComerciarUsu(targetClient);
			return;
		}
		// pone el oro directamente en la billetera
		if (this.objeto == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			targetClient.m_estads.addGold(-this.cant);
			targetClient.sendUpdateUserStats();
			// y se la doy al otro
			client.m_estads.addGold(this.cant);
			client.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = client.m_inv.agregarItem(obj2_objid, obj2_cant);
			if (agregados < obj2_cant) {
				// Tiro al piso lo que no pude guardar en el inventario.
				Map mapa = client.server.getMapa(client.m_pos.map);
				mapa.tirarItemAlPiso(client.m_pos.x, client.m_pos.y,
						new InventoryObject(obj2_objid, obj2_cant - agregados));
			}
			targetClient.quitarObjetos(obj2_objid, obj2_cant);
		}
		// pone el oro directamente en la billetera
		if (this.objeto == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			client.m_estads.addGold(-this.cant); // restar
			client.sendUpdateUserStats();
			// y se la doy al otro
			targetClient.m_estads.addGold(this.cant); // sumar
			targetClient.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = targetClient.m_inv.agregarItem(obj1_objid, obj1_objid);
			if (agregados < obj1_cant) {
				// Tiro al piso los items que no se agregaron al inventario.
				Map mapa = client.server.getMapa(targetClient.m_pos.map);
				mapa.tirarItemAlPiso(targetClient.m_pos.x, targetClient.m_pos.y,
						new InventoryObject(obj1_objid, obj1_objid - agregados));
			}
			client.quitarObjetos(obj1_objid, obj1_cant);
		}
		client.enviarInventario();
		targetClient.enviarInventario();
		client.m_comUsu.finComerciarUsu(client);
		targetClient.m_comUsu.finComerciarUsu(targetClient);
	}

	public void doFinComerciarUsuario(Player client) {
		// Comando FINCOMUSU
		// Salir del modo comercio Usuario
		if (this.destUsu > 0 && this.destUsu == client.getId()) {
			Player targetClient = client.server.getClientById(this.destUsu);
			targetClient.enviarMensaje(client.m_nick + " ha dejado de comerciar con vos.", FontType.FONTTYPE_TALK);
			if (targetClient != null) {
				targetClient.m_comUsu.finComerciarUsu(targetClient);
			}
		}
		client.m_comUsu.finComerciarUsu(client);
	}

	public void finComerciarUsu(Player client) {
		this.acepto = false;
		this.cant = 0;
		this.destUsu = 0;
		this.objeto = 0;
		client.m_flags.Comerciando = false;
		client.sendPacket(new UserCommerceEndResponse());
	}

	public void doRechazarComerciarUsuario(Player client) {
		// Comando COMUSUNO
		// Rechazar el cambio
		if (this.destUsu > 0) {
			Player targetClient = client.server.getClientById(this.destUsu);
			targetClient.enviarMensaje(client.m_nick + " ha rechazado tu oferta.", FontType.FONTTYPE_TALK);
			finComerciarUsu(targetClient);
		}
		client.enviarMensaje("Has rechazado la oferta del otro usuario.", FontType.FONTTYPE_TALK);
		finComerciarUsu(client);
	}

	public void doOfrecerComerciarUsuario(Player client, short slot, int cant) {
		// Comando OFRECER
		if (slot < 1 || cant < 1) {
			return;
		}
		if (this.destUsu == 0) {
			return;
		}
		Player targetClient = client.server.getClientById(this.destUsu);
		// sigue conectado el usuario ?
		if (!targetClient.m_flags.UserLogged) {
			finComerciarUsu(client);
			return;
		}
		// esta vivo ?
		if (!targetClient.isAlive()) {
			finComerciarUsu(client);
			return;
		}
		// Tiene la cantidad que se ofrece ??
		if (slot == Constants.FLAGORO) {
			// oro de la billetera
			if (cant > client.m_estads.getGold()) {
				client.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		} else {
			// objeto del inventario
			if (cant > client.m_inv.getObjeto(slot).cant) {
				client.enviarMensaje("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		}
		if (this.objeto > 0) {
			client.enviarMensaje("No puedes cambiar tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		this.objeto = slot;
		this.cant = cant;
		if (this.destUsu != client.getId()) {
			finComerciarUsu(client);
			return;
		}
		if (this.acepto) {
			// NO NO NO vos te estas pasando de listo...
			this.acepto = false;
			targetClient.enviarMensaje(client.m_nick + " ha cambiado su oferta.", FontType.FONTTYPE_TALK);
		}
		// Es la ofrenda de respuesta :)
		targetClient.m_comUsu.recibirObjetoTransaccion(targetClient);
	}

	void recibirObjetoTransaccion(Player client) {
		if (this.destUsu == 0) {
			return;
		}
		Player targetClient = client.server.getClientById(this.destUsu);

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
			ObjectInfo info = client.server.getObjectInfoStorage().getInfoObjeto(objid);
			// destUsu.enviar(MSG_COMUSUINV, 1, objid, info.Nombre, cant, 0,
			// info.GrhIndex, info.ObjType, info.MaxHIT, info.MinHIT,
			// info.MaxDef, info.Valor / 3);
		}
	}
}
