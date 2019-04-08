package org.argentumonline.server.inventory;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.protocol.BankEndResponse;
import org.argentumonline.server.protocol.BankInitResponse;
import org.argentumonline.server.protocol.BankOKResponse;
import org.argentumonline.server.protocol.ChangeBankSlotResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;

public class BankInventory extends Inventory {
	
	private User user;

	public BankInventory(GameServer server, User user, int size) {
		super(server, size);
		this.user = user;
	}
	
	public void moveBank(byte slot, byte dir) {
		if (dir != 1 && dir != -1) {
			return;
		}
		if (slot < 1 || slot > getSize()) {
			return;
		}

		move(slot, dir);
		updateBankUserInv();
		sendBankOk();
	}
	
	public void updateBankUserInv(short slot) {
		// Actualiza un solo slot
		// Actualiza el inventario
		if (getObject(slot).objid > 0) {
			sendBanObj(slot, getObject(slot));
		} else {
			user.sendPacket(new ChangeBankSlotResponse((byte) slot, (short) 0, "", (short)0, (short)0, (byte)0, (short)0, (short)0, (short)0, 0));
		}
	}

	public void updateBankUserInv() {
		// Actualiza todos los slots
		for (short i = 1; i <= Constants.MAX_BANCOINVENTORY_SLOTS; i++) {
			// Actualiza el inventario
			updateBankUserInv(i);
		}
	}

	public void sendBanObj(short slot, InventoryObject obj_inv) {
		if (obj_inv != null) {
			ObjectInfo info = server.findObj(obj_inv.objid);
			user.sendPacket(new ChangeBankSlotResponse(
					(byte) slot, info.ObjIndex, info.Nombre, (short)obj_inv.cant, info.GrhIndex,
					info.objType.value(), info.MaxHIT, info.MinHIT, info.MaxDef, info.Valor));
		}
	}

	public void quitarBancoInvItem(short slot, int cant) {
		// Quita un Obj
		getObject(slot).cant -= cant;
		if (getObject(slot).cant <= 0) {
			getObject(slot).objid = 0;
			getObject(slot).cant = 0;
		}
	}
	
	public boolean addObject(short objid, int cant) {
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (int i = 1; i <= getSize(); i++) {
			if (getObject(i).objid == objid && getObject(i).cant + cant <= Constants.MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = getEmptySlot();
		}
		if (slot_inv == 0) {
			user.sendMessage("No tienes mas espacio en el banco!!", FontType.FONTTYPE_INFO);
			return false;
		}
		// Mete el obj en el slot
		getObject(slot_inv).objid = objid;
		getObject(slot_inv).cant += cant;
		return true;
	}
	
	public void sendBankOk() {
		user.sendPacket(new BankOKResponse());
	}
	

	public void bankExtractItem(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		user.sendUpdateUserStats();
		if (getObject(slot).cant > 0) {
			if (cant > getObject(slot).cant) {
				cant = getObject(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userReciveObj(slot, cant);
			// Actualizamos el inventario del usuario
			user.sendInventoryToUser();
			// Actualizamos el banco
			updateBankUserInv();
			// ventana update
			sendBankOk();
		}
	}

	private void userReciveObj(short slot, int cant) {
		if (getObject(slot).cant <= 0) {
			return;
		}
		short objid = getObject(slot).objid;
		// ¿Ya tiene un objeto de este tipo?
		int slot_inv = 0;
		for (short i = 1; i <= user.getUserInv().getSize(); i++) {
			if (user.getUserInv().getObject(i).objid == objid 
					&& user.getUserInv().getObject(i).cant + cant <= Constants.MAX_INVENTORY_OBJS) {
				slot_inv = i;
				break;
			}
		}
		// Sino se fija por un slot vacio
		if (slot_inv == 0) {
			slot_inv = user.getUserInv().getEmptySlot();
		}
		if (slot_inv == 0) {
			user.sendMessage("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
			return;
		}
		// Mete el obj en el slot
		if (user.getUserInv().getObject(slot_inv).cant + cant <= Constants.MAX_INVENTORY_OBJS) {
			user.getUserInv().getObject(slot_inv).objid = objid;
			user.getUserInv().getObject(slot_inv).cant += cant;
			quitarBancoInvItem(slot, cant);
		} else {
			user.sendMessage("No podés tener mas objetos.", FontType.FONTTYPE_INFO);
		}
	}

	public void userDepositaItem(short slot, int cant) {
		// El usuario deposita un item
		user.sendUpdateUserStats();
		if (user.getUserInv().getObject(slot).cant > 0 && !user.getUserInv().getObject(slot).equipado) {
			if (cant > 0 && cant > user.getUserInv().getObject(slot).cant) {
				cant = this.user.getUserInv().getObject(slot).cant;
			}
			// Agregamos el obj que compro al inventario
			userDejaObj(slot, cant);
			// Actualizamos el inventario del usuario
			user.sendInventoryToUser();
			// Actualizamos el inventario del banco
			updateBankUserInv();
			// Actualizamos la ventana del banco
			sendBankOk();
		}
	}

	private void userDejaObj(short slot, int cant) {
		if (cant < 1) {
			return;
		}
		short objid = user.getUserInv().getObject(slot).objid;
		if (addObject(objid, cant)) {
			user.getUserInv().quitarUserInvItem(slot, cant);
		}
	}


	public void bankExtract(short slot, int cant) {
		// Comando RETI
		// Retirar un item de la bóveda del banco.
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!user.checkAlive()) {
			return;
		}
		Npc npc = user.getNearNpcSelected(Constants.DISTANCE_CASHIER);
		if (npc != null) {
			// ¿El Npc puede comerciar?
			if (npc.isBankCashier()) {
				// User retira el item del slot rdata
				bankExtractItem(slot, cant);
			} else {
				user.sendMessage("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}

	public void iniciarDeposito() {
		// Hacemos un Update del inventario del usuario
		updateBankUserInv();
		// Actualizamos el dinero
		user.sendUpdateUserStats();
		user.sendPacket(new BankInitResponse());
		user.getFlags().Comerciando = true;
	}

	public void bankEnd() {
		// Comando FINBAN
		// User sale del modo BANCO
		user.getFlags().Comerciando = false;
		user.sendPacket(new BankEndResponse());
	}

	public void bankStart() {
		// Comando /BOVEDA
		// Abrir bóveda del banco.
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!user.checkAlive()) {
			return;
		}
		Npc npc = user.getNearNpcSelected(Constants.DISTANCE_CASHIER);
		if (npc != null) {
			if (npc.isBankCashier()) {
				iniciarDeposito();
			} else {
				user.sendMessage("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}
	
	public void bankDeposit(short slot, int amount) {
		// Comando DEPO
		// Depositar un item en la bóveda del banco.
		// ¿Esta el user muerto? Si es asi no puede comerciar
		if (!user.checkAlive()) {
			return;
		}
		Npc npc = user.getNearNpcSelected(Constants.DISTANCE_CASHIER);
		if (npc != null) {
			// ¿El Npc puede comerciar?
			if (npc.isBankCashier()) {
				// User deposita el item del slot rdata
				userDepositaItem(slot, amount);
			} else {
				user.sendMessage("No te puedo ayudar. Busca al banquero.", FontType.FONTTYPE_INFO);
			}
		}
	}
	
	
}
