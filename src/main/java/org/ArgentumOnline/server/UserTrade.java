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

	short objectSlot = 0; // Indice del inventario a comerciar, que objeto desea dar

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
		if (destUsu == targetPlayer.getId() && targetPlayer.userTrade.destUsu == player.getId()) {
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
			player.sendMessage(targetPlayer.getNick() + " desea comerciar. Si deseas aceptar, Escribe /COMERCIAR.", FontType.FONTTYPE_TALK);
			targetPlayer.flags().TargetUser = player.getId();
		}
	}
	

	public void userCommerceAccept() {
		// Comando COMUSUOK
		// Aceptar el cambio

		if (this.destUsu == 0) {
			return;
		}
		Player targetPlayer = player.server.playerById(this.destUsu);
		if (this.destUsu != player.getId()) {
			return;
		}
		this.acepto = true;
		if (!this.acepto) {
			player.sendMessage("El otro usuario aun no ha aceptado tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		boolean terminarAhora = false;
		short obj1_objid = 0;
		int obj1_cant = 0;
		if (this.objectSlot == Constants.FLAGORO) {
			obj1_objid = Constants.OBJ_ORO;
			if (this.cant > player.stats.getGold()) {
				player.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj1_cant = this.cant;
			obj1_objid = player.userInv.getObjeto(this.objectSlot).objid;
			if (obj1_cant > player.userInv.getObjeto(this.objectSlot).cant) {
				player.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		}

		short obj2_objid = 0;
		int obj2_cant = 0;
		if (this.objectSlot == Constants.FLAGORO) {
			obj2_objid = Constants.OBJ_ORO;
			if (this.cant > targetPlayer.stats.getGold()) {
				targetPlayer.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj2_cant = this.cant;
			obj2_objid = targetPlayer.userInv.getObjeto(this.objectSlot).objid;
			if (obj2_cant > targetPlayer.userInv.getObjeto(this.objectSlot).cant) {
				targetPlayer.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		}
		// Por si las moscas...
		if (terminarAhora) {
			sendCommerceEnded();
			targetPlayer.userTrade.sendCommerceEnded();
			return;
		}
		// pone el oro directamente en la billetera
		if (this.objectSlot == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			targetPlayer.stats.addGold(-this.cant);
			targetPlayer.sendUpdateUserStats();
			// y se la doy al otro
			player.stats.addGold(this.cant);
			player.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = player.userInv.agregarItem(obj2_objid, obj2_cant);
			if (agregados < obj2_cant) {
				// Tiro al piso lo que no pude guardar en el inventario.
				Map mapa = player.server.getMap(player.pos().map);
				mapa.tirarItemAlPiso(player.pos().x, player.pos().y,
						new InventoryObject(obj2_objid, obj2_cant - agregados));
			}
			targetPlayer.quitarObjetos(obj2_objid, obj2_cant);
		}
		// pone el oro directamente en la billetera
		if (this.objectSlot == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			player.stats.addGold(-this.cant); // restar
			player.sendUpdateUserStats();
			// y se la doy al otro
			targetPlayer.stats.addGold(this.cant); // sumar
			targetPlayer.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = targetPlayer.userInv.agregarItem(obj1_objid, obj1_objid);
			if (agregados < obj1_cant) {
				// Tiro al piso los items que no se agregaron al inventario.
				Map mapa = player.server.getMap(targetPlayer.pos().map);
				mapa.tirarItemAlPiso(targetPlayer.pos().x, targetPlayer.pos().y,
						new InventoryObject(obj1_objid, obj1_objid - agregados));
			}
			player.quitarObjetos(obj1_objid, obj1_cant);
		}
		player.sendInventoryToUser();
		targetPlayer.sendInventoryToUser();
		sendCommerceEnded();
		targetPlayer.userTrade.sendCommerceEnded();
	}

	public void userCommerceEnd() {
		// Comando FINCOMUSU
		// Salir del modo comercio Usuario
		if (this.destUsu > 0 && this.destUsu == player.getId()) {
			Player targetPlayer = player.server.playerById(this.destUsu);
			if (targetPlayer != null) {
				targetPlayer.sendMessage(player.userName + " ha dejado de comerciar con vos.", FontType.FONTTYPE_TALK);
				if (targetPlayer != null) {
					targetPlayer.userTrade.sendCommerceEnded();
				}
			}
		}
		sendCommerceEnded();
	}

	private void sendCommerceEnded() {
		this.acepto = false;
		this.cant = 0;
		this.destUsu = 0;
		this.objectSlot = 0;
		player.flags().Comerciando = false;
		player.sendPacket(new UserCommerceEndResponse());
	}

	public void userCommerceReject() {
		// Comando COMUSUNO
		// Rechazar el cambio
		if (this.destUsu > 0) {
			Player targetPlayer = player.server.playerById(this.destUsu);
			targetPlayer.sendMessage(player.userName + " ha rechazado tu oferta.", FontType.FONTTYPE_TALK);
			targetPlayer.userTrade.sendCommerceEnded();
		}
		player.sendMessage("Has rechazado la oferta del otro usuario.", FontType.FONTTYPE_TALK);
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
		Player targetPlayer = player.server.playerById(this.destUsu);
		// sigue conectado el usuario ?
		if (!targetPlayer.flags().UserLogged) {
			sendCommerceEnded();
			return;
		}
		
		// esta vivo ?
		if (!targetPlayer.isAlive()) {
			sendCommerceEnded();
			return;
		}
		
		// Tiene la cantidad que se ofrece ??
		if (slot == Constants.FLAGORO) {
			// oro de la billetera
			if (amount > player.stats.getGold()) {
				player.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		} else {
			// objeto del inventario
			if (amount > player.userInv.getObjeto(slot).cant) {
				player.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		}
		
		if (this.objectSlot > 0) {
			player.sendMessage("No puedes cambiar tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		
		this.objectSlot = slot;
		this.cant = amount;
		if (this.destUsu != player.getId()) {
			sendCommerceEnded();
			return;
		}
		if (this.acepto) {
			// NO NO NO vos te estas pasando de listo...
			this.acepto = false;
			targetPlayer.sendMessage(player.userName + " ha cambiado su oferta.", FontType.FONTTYPE_TALK);
		}
		
		// Es la ofrenda de respuesta :)
		targetPlayer.userTrade.recibirObjetoTransaccion();
	}

	void recibirObjetoTransaccion() {
		if (this.destUsu == 0) {
			return;
		}
		Player targetPlayer = player.server.playerById(this.destUsu);

		short objid = 0;
		if (this.objectSlot == Constants.FLAGORO) {
			objid = Constants.OBJ_ORO;
		} else {
			objid = targetPlayer.userInv.getObjeto(this.objectSlot).objid;
		}
		if (this.cant <= 0 || objid <= 0) {
			return;
		}
		if (objid > 0 && this.cant > 0) {
			player.updateVentanaComercio(objid, cant);
		}
	}

	/*
'envia a AQuien el objeto del otro
Public Sub EnviarObjetoTransaccion(ByVal AQuien As Integer)
Dim ObjInd As Integer
Dim ObjCant As Long

'[Alejo]: En esta funcion se centralizaba el problema
'         de no poder comerciar con mas de 32k de oro.
'         Ahora si funciona!!!

ObjCant = UserList(UserList(AQuien).ComUsu.DestUsu).ComUsu.cant
If UserList(UserList(AQuien).ComUsu.DestUsu).ComUsu.Objeto = FLAGORO Then
    ObjInd = iORO
Else
    ObjInd = UserList(UserList(AQuien).ComUsu.DestUsu).Invent.Object(UserList(UserList(AQuien).ComUsu.DestUsu).ComUsu.Objeto).ObjIndex
End If

If ObjCant <= 0 Or ObjInd <= 0 Then Exit Sub

If ObjInd > 0 And ObjCant > 0 Then
    Call WriteChangeUserTradeSlot(AQuien, ObjInd, ObjCant)
    Call FlushBuffer(AQuien)
End If

End Sub
	 */
	
}
