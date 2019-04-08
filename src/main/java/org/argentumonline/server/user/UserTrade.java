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
package org.argentumonline.server.user;

import static org.argentumonline.server.util.Color.COLOR_BLANCO;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Skill;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcMerchant;
import org.argentumonline.server.protocol.ChangeUserTradeSlotResponse;
import org.argentumonline.server.protocol.CommerceEndResponse;
import org.argentumonline.server.protocol.CommerceInitResponse;
import org.argentumonline.server.protocol.UserCommerceEndResponse;
import org.argentumonline.server.util.FontType;

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
	
	private GameServer server;
	private User user;
	
	public UserTrade(GameServer server, User user) {
		this.server = server;
		this.user = user;
	}
	
	public void commerceBuyFromMerchant(byte slotNpc, short amount) {
		// Comando COMP
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!user.checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
		if (user.getFlags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(user.getFlags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = this.server.getMap(user.pos().map);
		if (mapa == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.isTrade()) {
			user.talk(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}
		if (npc.npcInv().isValidSlot(slotNpc)) {
			((NpcMerchant)npc).sellItemToUser(user, slotNpc, amount);
		}
	}

	public void commerceSellToMerchant(byte slot, short amount) {
		// Comando VEND
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!user.checkAlive()) {
			return;
		}
		// ¿El target es un Npc valido?
		if (user.getFlags().TargetNpc == 0) {
			return;
		}
		Npc npc = this.server.npcById(user.getFlags().TargetNpc);
		if (npc == null) {
			return;
		}
		Map mapa = this.server.getMap(user.pos().map);
		if (mapa == null) {
			return;
		}
		// ¿El Npc puede comerciar?
		if (!npc.isTrade()) {
			user.talk(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
			return;
		}

		if (npc.npcInv().isValidSlot(slot)) {
			((NpcMerchant)npc).buyItemFromUser(user, slot, amount);
		}
	}

	public void updateVentanaComercio(short objIndex, int amount) {
		ObjectInfo objInfo = (objIndex == 0) ? ObjectInfo.EMPTY : server.findObj(objIndex);
		user.sendPacket(new ChangeUserTradeSlotResponse(
				objInfo.ObjIndex,
				objInfo.Nombre,
				amount,
				objInfo.GrhIndex,
				objInfo.objType.value(),
				objInfo.MaxHIT,
				objInfo.MinHIT,
				objInfo.Def,
				objInfo.Valor));
	}

	public void commerceEnd() {
		user.getFlags().Comerciando = false;
		user.sendPacket(new CommerceEndResponse());
	}

	public double descuento() {
		// Establece el descuento en funcion del skill comercio
		final double indicesDto[] = {
				1.0, // 0-5
				1.1, 1.1, // 6-10
				1.2, 1.2, // 11-20
				1.3, 1.3, // 21-30
				1.4, 1.4, // 31-40
				1.5, 1.5, // 41-50
				1.6, 1.6, // 51-60
				1.7, 1.7, // 61-70
				1.8, 1.8, // 71-80
				1.9, 1.9, // 81-90
				2.0, 2.0 // 91-100
		};
		int ptsComercio = user.skills().get(Skill.SKILL_Comerciar);
		user.getFlags().Descuento = indicesDto[(short) (ptsComercio / 5)];
		return user.getFlags().Descuento;
	}

	public void commerceStart() {
		// Comando /COMERCIAR
		if (!user.checkAlive()) {
			return;
		}
		if (user.getFlags().isCounselor()) {
			return;
		}

		if (user.isTrading()) {
			user.sendMessage("Ya estás comerciando", FontType.FONTTYPE_INFO);
			return;
		}

		Map mapa = this.server.getMap(user.pos().map);
		if (mapa == null) {
			return;
		}
		Npc npc = user.getNearNpcSelected(Constants.DISTANCE_MERCHANT);
		if (npc != null) {
			// ¿El Npc puede comerciar?
			if (!npc.isTrade()) {
				if (npc.getDesc().length() > 0) {
					user.talk(COLOR_BLANCO, "No tengo ningun interes en comerciar.", npc.getId());
				}
				return;
			}

			// Mandamos el Inventario
			((NpcMerchant)npc).sendNpcInventoryToUser(user);
			user.sendInventoryToUser();
			user.sendUpdateUserStats();

			// Iniciamos el comercio con el Npc
			user.sendPacket(new CommerceInitResponse());
			user.getFlags().Comerciando = true;
		}

		if (user.getFlags().TargetUser > 0) {
            //User commerce...
            //Can he commerce??
			if (user.getFlags().isCounselor()) {
				user.sendMessage("No puedes vender items.", FontType.FONTTYPE_WARNING);
                return;
			}

			User targetUser = this.server.userById(user.getFlags().TargetUser);
			if (targetUser == null) {
				return;
			}
            //Is the other one dead??
			if (!targetUser.isAlive()) {
				user.sendMessage("¡¡No puedes comerciar con los muertos!!", FontType.FONTTYPE_INFO);
                return;
			}

            //Is it me??
			if (targetUser == user) {
				user.sendMessage("No puedes comerciar con vos mismo...", FontType.FONTTYPE_INFO);
                return;
			}

            //Check distance
			if (user.pos().distance(targetUser.pos()) > 3) {
				user.sendMessage("Estás demasiado lejos del usuario.", FontType.FONTTYPE_INFO);
                return;
			}

            //Is he already trading?? is it with me or someone else??
			if (targetUser.isTrading() && targetUser.getFlags().TargetUser != user.getId()) {
				user.sendMessage("No puedes comerciar con el usuario en este momento.", FontType.FONTTYPE_INFO);
			}

            //Initialize some variables...
			destUsu = user.getFlags().TargetUser;
			cant = 0;
			objectSlot = 0;
			acepto = false;

            //Rutina para comerciar con otro usuario
            iniciarComercioConUsuario(user.getFlags().TargetUser);
		}

		user.sendMessage("Primero haz click izquierdo sobre el personaje.", FontType.FONTTYPE_INFO);
	}

	
	
	
	//origen: origen de la transaccion, originador del comando
	//destino: receptor de la transaccion
	public void iniciarComercioConUsuario(short target) {
		User targetUser = GameServer.instance().userById(target);
		if (targetUser == null) {
			return;
		}
		//Si ambos pusieron /comerciar entonces
		if (destUsu == targetUser.getId() && targetUser.userTrade.destUsu == user.getId()) {
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
			user.sendMessage(targetUser.getUserName() + " desea comerciar. Si deseas aceptar, Escribe /COMERCIAR.", FontType.FONTTYPE_TALK);
			targetUser.getFlags().TargetUser = user.getId();
		}
	}
	

	public void userCommerceAccept() {
		// Comando COMUSUOK
		// Aceptar el cambio

		if (this.destUsu == 0) {
			return;
		}
		User targetUser = this.server.userById(this.destUsu);
		if (this.destUsu != user.getId()) {
			return;
		}
		this.acepto = true;
		if (!this.acepto) {
			user.sendMessage("El otro usuario aun no ha aceptado tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		boolean terminarAhora = false;
		short obj1_objid = 0;
		int obj1_cant = 0;
		if (this.objectSlot == Constants.FLAGORO) {
			obj1_objid = Constants.OBJ_ORO;
			if (this.cant > user.stats.getGold()) {
				user.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj1_cant = this.cant;
			obj1_objid = user.getUserInv().getObject(this.objectSlot).objid;
			if (obj1_cant > user.getUserInv().getObject(this.objectSlot).cant) {
				user.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		}

		short obj2_objid = 0;
		int obj2_cant = 0;
		if (this.objectSlot == Constants.FLAGORO) {
			obj2_objid = Constants.OBJ_ORO;
			if (this.cant > targetUser.stats.getGold()) {
				targetUser.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		} else {
			obj2_cant = this.cant;
			obj2_objid = targetUser.getUserInv().getObject(this.objectSlot).objid;
			if (obj2_cant > targetUser.getUserInv().getObject(this.objectSlot).cant) {
				targetUser.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				terminarAhora = true;
			}
		}
		// Por si las moscas...
		if (terminarAhora) {
			sendCommerceEnded();
			targetUser.userTrade.sendCommerceEnded();
			return;
		}
		// pone el oro directamente en la billetera
		if (this.objectSlot == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			targetUser.stats.addGold(-this.cant);
			targetUser.sendUpdateUserStats();
			// y se la doy al otro
			user.stats.addGold(this.cant);
			user.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = user.getUserInv().agregarItem(obj2_objid, obj2_cant);
			if (agregados < obj2_cant) {
				// Tiro al piso lo que no pude guardar en el inventario.
				Map mapa = this.server.getMap(user.pos().map);
				mapa.dropItemOnFloor(user.pos().x, user.pos().y,
						new InventoryObject(obj2_objid, obj2_cant - agregados));
			}
			targetUser.quitarObjetos(obj2_objid, obj2_cant);
		}
		// pone el oro directamente en la billetera
		if (this.objectSlot == Constants.FLAGORO) {
			// quito la cantidad de oro ofrecida
			user.stats.addGold(-this.cant); // restar
			user.sendUpdateUserStats();
			// y se la doy al otro
			targetUser.stats.addGold(this.cant); // sumar
			targetUser.sendUpdateUserStats();
		} else {
			// Quita el objeto y se lo da al otro
			int agregados = targetUser.getUserInv().agregarItem(obj1_objid, obj1_objid);
			if (agregados < obj1_cant) {
				// Tiro al piso los items que no se agregaron al inventario.
				Map mapa = this.server.getMap(targetUser.pos().map);
				mapa.dropItemOnFloor(targetUser.pos().x, targetUser.pos().y,
						new InventoryObject(obj1_objid, obj1_objid - agregados));
			}
			user.quitarObjetos(obj1_objid, obj1_cant);
		}
		user.sendInventoryToUser();
		targetUser.sendInventoryToUser();
		sendCommerceEnded();
		targetUser.userTrade.sendCommerceEnded();
	}

	public void userCommerceEnd() {
		// Comando FINCOMUSU
		// Salir del modo comercio Usuario
		if (this.destUsu > 0 && this.destUsu == user.getId()) {
			User targetUser = this.server.userById(this.destUsu);
			if (targetUser != null) {
				targetUser.sendMessage(user.userName + " ha dejado de comerciar con vos.", FontType.FONTTYPE_TALK);
				if (targetUser != null) {
					targetUser.userTrade.sendCommerceEnded();
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
		user.getFlags().Comerciando = false;
		user.sendPacket(new UserCommerceEndResponse());
	}

	public void userCommerceReject() {
		// Comando COMUSUNO
		// Rechazar el cambio
		if (this.destUsu > 0) {
			User targetUser = this.server.userById(this.destUsu);
			targetUser.sendMessage(user.userName + " ha rechazado tu oferta.", FontType.FONTTYPE_TALK);
			targetUser.userTrade.sendCommerceEnded();
		}
		user.sendMessage("Has rechazado la oferta del otro usuario.", FontType.FONTTYPE_TALK);
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
		User targetUser = this.server.userById(this.destUsu);
		// sigue conectado el usuario ?
		if (!targetUser.getFlags().UserLogged) {
			sendCommerceEnded();
			return;
		}
		
		// esta vivo ?
		if (!targetUser.isAlive()) {
			sendCommerceEnded();
			return;
		}
		
		// Tiene la cantidad que se ofrece ??
		if (slot == Constants.FLAGORO) {
			// oro de la billetera
			if (amount > user.stats.getGold()) {
				user.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		} else {
			// objeto del inventario
			if (amount > user.getUserInv().getObject(slot).cant) {
				user.sendMessage("No tienes esa cantidad.", FontType.FONTTYPE_TALK);
				return;
			}
		}
		
		if (this.objectSlot > 0) {
			user.sendMessage("No puedes cambiar tu oferta.", FontType.FONTTYPE_TALK);
			return;
		}
		
		this.objectSlot = slot;
		this.cant = amount;
		if (this.destUsu != user.getId()) {
			sendCommerceEnded();
			return;
		}
		if (this.acepto) {
			// NO NO NO vos te estas pasando de listo...
			this.acepto = false;
			targetUser.sendMessage(user.userName + " ha cambiado su oferta.", FontType.FONTTYPE_TALK);
		}
		
		// Es la ofrenda de respuesta :)
		targetUser.userTrade.recibirObjetoTransaccion();
	}

	void recibirObjetoTransaccion() {
		if (this.destUsu == 0) {
			return;
		}
		User targetUser = this.server.userById(this.destUsu);

		short objid = 0;
		if (this.objectSlot == Constants.FLAGORO) {
			objid = Constants.OBJ_ORO;
		} else {
			objid = targetUser.getUserInv().getObject(this.objectSlot).objid;
		}
		if (this.cant <= 0 || objid <= 0) {
			return;
		}
		if (objid > 0 && this.cant > 0) {
			updateVentanaComercio(objid, cant);
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
