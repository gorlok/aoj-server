/**
 * UserSpells.java
 *
 * Created on 27/may/2007
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

import static org.ArgentumOnline.server.util.FontType.FONTTYPE_FIGHT;

import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.ChangeSpellSlotResponse;
import org.ArgentumOnline.server.protocol.ParalizeOKResponse;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gorlok
 */
public class UserSpells implements Constants {

	private static Logger log = LogManager.getLogger();

	short spells[] = new short[MAX_HECHIZOS];

	private Player player;
	private GameServer server;

	public UserSpells(GameServer server, Player player) {
		this.server = server;
		this.player = player;
	}

	public int getCount() {
		return this.spells.length;
	}

	public short getSpell(int slot) {
		return this.spells[slot -1];
	}

	public void setSpell(int slot, short spell) {
		this.spells[slot - 1] = spell;
	}

	public boolean isSlotEmpty(int slot) {
		return getSpell(slot) == 0;
	}

	private boolean isSlotValid(int slot) {
		return slot > 0 && slot < this.spells.length;
	}

	public void enviarHechizos() {
		for (int slot = 1; slot <= this.spells.length; slot++) {
			enviarHechizo(slot);
		}
	}

	public void changeUserHechizo(int slot, short hechizo) {
		setSpell(slot, hechizo);
		enviarHechizo(slot);
	}

	public void enviarHechizo(int slot) {
		if (!isSlotValid(slot)) {
			return;
		}
		if (isSlotEmpty(slot)) {
			this.player.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) 0, "(Vacío)"));
			return;
		}
		Spell spell = this.server.getHechizo(getSpell(slot));
		this.player.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) spell.getId(), spell.getName()));
	}

	public void sendMeSpellInfo(short slot) {
		if (slot < 1 || slot > MAX_HECHIZOS) {
			this.player.sendMessage("¡Primero selecciona el hechizo.!", FontType.FONTTYPE_INFO);
		} else {
			int numHechizo = this.spells[slot - 1];
			if (numHechizo > 0) {
				Spell hechizo = this.server.getHechizo(numHechizo);
				this.player.sendMessage("||%%%%%%%%%%%% INFO DEL HECHIZO %%%%%%%%%%%%",
					FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Nombre: " + hechizo.Nombre, FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Descripcion: " + hechizo.Desc, FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Skill requerido: " + hechizo.MinSkill
						+ " de magia.", FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Mana necesario: " + hechizo.ManaRequerido,
					FontType.FONTTYPE_INFO);
				this.player.sendMessage("||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
					FontType.FONTTYPE_INFO);
			}
		}
	}

	public void castSpell(short slot) {

		System.out.println("ENTRO");
		if (slot < 1 || slot > this.spells.length) {
			return;
		}

		System.out.println(" " + slot + "");

		this.player.flags().Hechizo = this.spells[slot - 1];
		log.info("doLanzarHechizo =====> " + this.player.flags().Hechizo);
	}

	public boolean tieneHechizo(int numHechizo) {
		for (short element : this.spells) {
			if (element == numHechizo) {
				return true;
			}
		}
		return false;
	}

	private int slotLibreHechizos() {
		for (int i = 0; i < this.spells.length; i++) {
			if (this.spells[i] == 0) {
				return i + 1;
			}
		}
		return 0;
	}

	public void agregarHechizo(int slot) {
		int oid = this.player.userInv().getObjeto(slot).objid;
		ObjectInfo objHechizo = this.server.getObjectInfoStorage().getInfoObjeto(oid);
		short numHechizo = objHechizo.HechizoIndex;
		if (!tieneHechizo(numHechizo)) {
			// Buscamos un slot vacio
			int slotLibre = slotLibreHechizos();
			if (slotLibre == 0) {
				this.player.sendMessage("No tienes espacio para más hechizos.",
					FontType.FONTTYPE_INFO);
			} else {
				// Actualizamos la lista de hechizos,
				changeUserHechizo(slotLibre, numHechizo);
				// y quitamos el item del inventario.
				this.player.userInv().quitarUserInvItem(slot, 1);
			}
		} else {
			this.player.sendMessage("Ya tienes ese hechizo.", FontType.FONTTYPE_INFO);
		}
	}

	public void moveSpell(int slot, short direction) {
		if (direction == 1) {
			// Move spell upward
			if (slot == 1) {
				this.player.sendMessage("No puedes mover el hechizo en esa direccion.",
					FontType.FONTTYPE_INFO);
				return;
			}
			short spell = getSpell(slot);
			setSpell(slot, getSpell(slot - 1));
			setSpell(slot - 1, spell);
			enviarHechizo(slot - 1);
			enviarHechizo(slot);
		} else if (direction == 2) {
			// Move spell downward
			if (slot == MAX_HECHIZOS) {
				this.player.sendMessage("No puedes mover el hechizo en esa direccion.",
					FontType.FONTTYPE_INFO);
				return;
			}
			short spell = getSpell(slot);
			setSpell(slot, getSpell(slot + 1));
			setSpell(slot + 1, spell);
			enviarHechizo(slot);
			enviarHechizo(slot + 1);
		}
	}

	public void lanzarHechizo(Spell hechizo) {
		if (puedeLanzar(hechizo)) {
			switch (hechizo.Target) {
			case uUsuarios:
				if (this.player.flags().TargetUser > 0) {
					handleHechizoUsuario(hechizo);
				} else {
					this.player.sendMessage("Este hechizo actua solo sobre usuarios.",
						FontType.FONTTYPE_INFO);
				}
				break;
			case uNPC:
				if (this.player.flags().TargetNpc > 0) {
					handleHechizoNPC(hechizo);
				} else {
					this.player.sendMessage("Este hechizo solo afecta a los npcs.",
						FontType.FONTTYPE_INFO);
				}
				break;
			case uUsuariosYnpc:
				if (this.player.flags().TargetUser > 0) {
					handleHechizoUsuario(hechizo);
				} else if (this.player.flags().TargetNpc > 0) {
					handleHechizoNPC(hechizo);
				} else {
					this.player.sendMessage("Target inválido.", FontType.FONTTYPE_INFO);
				}
				break;
			case uTerreno:
				handleHechizoTerreno(hechizo);
				break;
			}
		}
		this.player.flags().Trabajando = false;
	}

	public boolean hechizoEstadoUsuario() {
		Spell hechizo = this.server.getHechizo(this.player.flags().Hechizo);
		Player targetUser = this.server.playerById(this.player.flags().TargetUser);
		if (hechizo.Invisibilidad == 1) {
			targetUser.flags().Invisible = true;
			//mapa.enviarATodos(MSG_NOVER, targetUser.m_id, 1);
			infoHechizo();
			return true;
		}
		if (hechizo.Envenena == 1) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Envenenado = true;
			infoHechizo();
			return true;
		}
		if (hechizo.CuraVeneno == 1) {
			targetUser.flags().Envenenado = false;
			infoHechizo();
			return true;
		}
		if (hechizo.Maldicion == 1) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Maldicion = true;
			infoHechizo();
			return true;
		}
		if (hechizo.RemoverMaldicion == 1) {
			targetUser.flags().Maldicion = false;
			infoHechizo();
			return true;
		}
		if (hechizo.Bendicion == 1) {
			targetUser.flags().Bendicion = true;
			infoHechizo();
			return true;
		}
		if (hechizo.isParaliza()) {
			if (!targetUser.flags().Paralizado) {
				if (!this.player.puedeAtacar(targetUser)) {
					return false;
				}
				if (this.player != targetUser) {
					this.player.usuarioAtacadoPorUsuario(targetUser);
				}
				targetUser.flags().Paralizado = true;
				targetUser.counters.Paralisis = IntervaloParalizado;
				targetUser.sendPacket(new ParalizeOKResponse());
				infoHechizo();
				return true;
			}
		}
		if (hechizo.RemoverParalisis == 1) {
			if (targetUser.flags().Paralizado) {
				targetUser.flags().Paralizado = false;
				targetUser.sendPacket(new ParalizeOKResponse());
				infoHechizo();
				return true;
			}
		}
		if (hechizo.Revivir == 1) {
			if (!targetUser.isAlive()) {
				if (!targetUser.isCriminal()) {
					if (this.player != targetUser) {
						this.player.reputation.incNoble(500);
						this.player.sendMessage(
								"¡Los Dioses te sonrien, has ganado 500 puntos de nobleza!.",
								FontType.FONTTYPE_INFO);
					}
				}
				targetUser.revive();
			}
			infoHechizo();
			return true;
		}
		if (hechizo.Ceguera == 1) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Ceguera = true;
			targetUser.counters.Ceguera = IntervaloParalizado;
			//targetUser.enviar(MSG_CEGU);
			infoHechizo();
			return true;
		}
		if (hechizo.Estupidez == 1) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Estupidez = true;
			targetUser.counters.Ceguera = IntervaloParalizado;
			//targetUser.enviar(MSG_DUMB);
			infoHechizo();
			return true;
		}
		return false;
	}

	public boolean hechizoEstadoNPC(Npc npc, Spell hechizo) {
		if (hechizo.Invisibilidad == 1) {
			infoHechizo();
			npc.hacerInvisible();
			return true;
		}
		if (hechizo.Envenena == 1) {
			if (!npc.getAttackable()) {
				this.player.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			infoHechizo();
			npc.envenenar();
			return true;
		}
		if (hechizo.CuraVeneno == 1) {
			infoHechizo();
			npc.curarVeneno();
			return true;
		}
		if (hechizo.Maldicion == 1) {
			if (!npc.getAttackable()) {
				this.player.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			infoHechizo();
			npc.volverMaldito();
			return true;
		}
		if (hechizo.RemoverMaldicion == 1) {
			infoHechizo();
			npc.quitarMaldicion();
			return true;
		}
		if (hechizo.Bendicion == 1) {
			// No hay contra-hechizo de bendicion???
			infoHechizo();
			npc.volverBendito();
			return true;
		}
		if (hechizo.isParaliza()) {
			if (!npc.afectaParalisis()) {
				infoHechizo();
				npc.paralizar();
				return true;
			}
			this.player.sendMessage("El npc es inmune a este hechizo.", FONTTYPE_FIGHT);
		}
		if (hechizo.RemoverParalisis == 1) {
			if (npc.estaParalizado()) {
				infoHechizo();
				npc.desparalizar();
				return true;
			}
			this.player.sendMessage("El npc no está paralizado.", FONTTYPE_FIGHT);
		}
		return false;
	}

	public boolean hechizoPropNPC(Npc npc, Spell hechizo) {
		// Salud
		if (hechizo.SubeHP == 1) {
			// Curar la salud
			int cura = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			cura += Util.porcentaje(cura, 3 * this.player.stats().ELV);
			infoHechizo();
			npc.stats.addMinHP(cura);
			this.player.sendMessage("Has curado " + cura
					+ " puntos de salud a la criatura.", FONTTYPE_FIGHT);
			return true;
		} else if (hechizo.SubeHP == 2) {
			// Dañar la salud
			if (!npc.getAttackable()) {
				this.player.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			if (npc.getPetUserOwner() != null
					&& (this.player.flags().Seguro || this.player.userFaction().ArmadaReal)) {
				if (!npc.getPetUserOwner().isCriminal()) {
					if (this.player.userFaction().ArmadaReal) {
						this.player.sendMessage("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
							FontType.FONTTYPE_WARNING);
					} else {
						this.player.sendMessage("Tienes el seguro activado. Presiona la tecla *.",
							FontType.FONTTYPE_WARNING);
					}
					return false;
				}
			}

			//FIX by AGUSH: we check if user can attack to guardians
			if (npc.isNpcGuard() && this.player.hasSafeLock()) {
				this.player.sendMessage("Tienes el seguro activado. Presiona la tecla *.",
						FontType.FONTTYPE_INFO);
				return false;
			}

			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * this.player.stats().ELV);
			infoHechizo();
			this.player.npcAtacado(npc);
			if (npc.getSonidoAtaqueExitoso() > 0) {
				this.player.enviarSonido(npc.getSonidoAtaqueExitoso());
			}
			npc.stats.quitarHP(daño);
			this.player.sendMessage("Le has causado " + daño
					+ " puntos de daño a la criatura!", FONTTYPE_FIGHT);
			npc.calcularDarExp(this.player, daño);
			if (npc.stats.MinHP < 1) {
				npc.muereNpc(this.player);
			}
			return true;
		}
		return false;
	}

	public boolean hechizoPropUsuario() {
		Spell hechizo = this.server.getHechizo(this.player.flags().Hechizo);
		Player targetUser = this.server.playerById(this.player.flags().TargetUser);
		// Hambre
		if (hechizo.SubeHam == 1) {
			// Aumentar hambre
			infoHechizo();
			int daño = Util.Azar(hechizo.MinHam, hechizo.MaxHam);
			targetUser.stats.aumentarHambre(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has restaurado " + daño
						+ " puntos de hambre a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha restaurado "
						+ daño + " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has restaurado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			targetUser.sendUpdateHungerAndThirst();
			return true;
		} else if (hechizo.SubeHam == 2) {
			// Quitar hambre
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinHam, hechizo.MaxHam);
			targetUser.stats.quitarHambre(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has quitado " + daño
						+ " puntos de hambre a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha quitado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has quitado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			targetUser.sendUpdateHungerAndThirst();
			if (targetUser.stats.eaten < 1) {
				targetUser.stats.eaten = 0;
				targetUser.flags().Hambre = true;
			}
			return true;
		}
		// Sed
		if (hechizo.SubeSed == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSed, hechizo.MaxSed);
			targetUser.stats.aumentarSed(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has restaurado " + daño
						+ " puntos de sed a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha restaurado "
						+ daño + " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has restaurado " + daño
						+ " puntos de sed.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeSed == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSed, hechizo.MaxSed);
			targetUser.stats.quitarSed(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has quitado " + daño
						+ " puntos de sed a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha quitado " + daño
						+ " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage(
					"Te has quitado " + daño + " puntos de sed.",
					FONTTYPE_FIGHT);
			}
			if (targetUser.stats.drinked < 1) {
				targetUser.stats.drinked = 0;
				targetUser.flags().Sed = true;
			}
			return true;
		}
		// <-------- Agilidad ---------->
		if (hechizo.SubeAgilidad == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinAgilidad, hechizo.MaxAgilidad);
			targetUser.flags().DuracionEfecto = 1200;
			targetUser.stats().attr().modify(Attribute.AGILIDAD, daño);
			targetUser.flags().TomoPocion = true;
			return true;
		} else if (hechizo.SubeAgilidad == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.flags().TomoPocion = true;
			int daño = Util.Azar(hechizo.MinAgilidad, hechizo.MaxAgilidad);
			targetUser.flags().DuracionEfecto = 700;
			targetUser.stats().attr().modify(Attribute.AGILIDAD, -daño);
			return true;
		}
		// <-------- Fuerza ---------->
		if (hechizo.SubeFuerza == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinFuerza, hechizo.MaxFuerza);
			targetUser.flags().DuracionEfecto = 1200;
			targetUser.stats().attr().modify(Attribute.FUERZA, daño);
			targetUser.flags().TomoPocion = true;
			return true;
		} else if (hechizo.SubeFuerza == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.flags().TomoPocion = true;
			int daño = Util.Azar(hechizo.MinFuerza, hechizo.MaxFuerza);
			targetUser.flags().DuracionEfecto = 700;
			targetUser.stats().attr().modify(Attribute.FUERZA, -daño);
			return true;
		}
		// Salud
		if (hechizo.SubeHP == 1) {
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * this.player.stats().ELV);
			infoHechizo();
			targetUser.stats.addMinHP(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has restaurado " + daño
						+ " puntos de vida a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha restaurado "
						+ daño + " puntos de vida.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has restaurado " + daño
						+ " puntos de vida.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeHP == 2) {
			if (this.player == targetUser) {
				this.player.sendMessage("No puedes atacarte a ti mismo.",
					FONTTYPE_FIGHT);
				return false;
			}
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * this.player.stats().ELV);
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.stats.quitarHP(daño);
			this.player.sendMessage("Le has quitado " + daño + " puntos de vida a "
					+ targetUser.userName, FONTTYPE_FIGHT);
			targetUser.sendMessage(this.player.getNick() + " te ha quitado " + daño
					+ " puntos de vida.", FONTTYPE_FIGHT);
			// Muere
			if (targetUser.stats.MinHP < 1) {
				this.player.contarMuerte(targetUser);
				targetUser.stats.MinHP = 0;
				this.player.actStats(targetUser);
				targetUser.userDie();
			}
			return true;
		}
		// Mana
		if (hechizo.SubeMana == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinMana, hechizo.MaxMana);
			targetUser.stats.aumentarMana(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has restaurado " + daño
						+ " puntos de mana a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha restaurado "
						+ daño + " puntos de mana.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has restaurado " + daño
						+ " puntos de mana.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeMana == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinMana, hechizo.MaxMana);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has quitado " + daño
						+ " puntos de mana a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha quitado " + daño
						+ " puntos de mana.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has quitado " + daño
						+ " puntos de mana.", FONTTYPE_FIGHT);
			}
			targetUser.stats.quitarMana(daño);
			return true;
		}
		// Stamina
		if (hechizo.SubeSta == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSta, hechizo.MaxSta);
			targetUser.stats.aumentarStamina(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has restaurado " + daño
						+ " puntos de energia a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha restaurado "
						+ daño + " puntos de energia.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has restaurado " + daño
						+ " puntos de energia.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeSta == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSta, hechizo.MaxSta);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has quitado " + daño
						+ " puntos de energia a " + targetUser.userName,
					FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha quitado " + daño
						+ " puntos de energia.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has quitado " + daño
						+ " puntos de energia.", FONTTYPE_FIGHT);
			}
			targetUser.stats.quitarStamina(daño);
			return true;
		}
		return false;
	}

	public boolean puedeLanzar(Spell hechizo) {
		if (!this.player.checkAlive("No puedes lanzar hechizos porque estas muerto.")) {
			return false;
		}
		MapPos targetPos = MapPos.mxy(this.player.flags().TargetMap,
			this.player.flags().TargetX, this.player.flags().TargetY);
		if (this.player.pos().distance(targetPos) > MAX_DISTANCIA_MAGIA) {
			this.player.sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
			return false;
		}
		if (this.player.stats().mana < hechizo.ManaRequerido) {
			this.player.sendMessage("No tienes suficiente mana.", FontType.FONTTYPE_INFO);
			return false;
		}
		if (this.player.skills().get(Skill.SKILL_Magia) < hechizo.MinSkill) {
			this.player.sendMessage(
					"No tienes suficientes puntos de magia para lanzar este hechizo.",
					FontType.FONTTYPE_INFO);
			return false;
		}
		if (this.player.stats().stamina == 0
				|| this.player.stats().stamina < hechizo.StaRequerida) {
			this.player.sendMessage("Estas muy cansado para lanzar este hechizo.",
				FontType.FONTTYPE_INFO);
			return false;
		}
		return true;
	}

	private boolean hechizoInvocacion() {
		if (this.player.getUserPets().isFullPets()) {
			this.player.sendMessage("No puedes invocar más mascotas!", FontType.FONTTYPE_INFO);
			return false;
		}

		Map mapa = this.server.getMap(this.player.pos().map);

		if (mapa.esZonaSegura()) {
			this.player.sendMessage("¡Estás en una zona segura!", FontType.FONTTYPE_INFO);
			return false;
		}

		boolean exito = false;
		MapPos targetPos = MapPos.mxy(this.player.flags().TargetMap,
			this.player.flags().TargetX, this.player.flags().TargetY);
		Spell hechizo = this.server.getHechizo(this.player.flags().Hechizo);
		for (int i = 0; i < hechizo.Cant; i++) {
			// Considero que hubo exito si se pudo invocar alguna criatura.
			exito = exito || (this.player.crearMascotaInvocacion(hechizo.NumNpc, targetPos) != null);
		}
		infoHechizo();
		return exito;
	}

	private void handleHechizoTerreno(Spell hechizo) {
		boolean exito = false;
		switch (hechizo.Tipo) {
		case uInvocacion:
			exito = hechizoInvocacion();
			break;
		}
		if (exito) {
			this.player.subirSkill(Skill.SKILL_Magia);
			this.player.stats().quitarMana(hechizo.ManaRequerido);
			this.player.stats().quitarStamina(hechizo.StaRequerida);
			this.player.sendUpdateUserStats();
		}
	}

	private void handleHechizoUsuario(Spell hechizo) {
		boolean exito = false;
		Player target = this.server.playerById(this.player.flags().TargetUser);
		if (target == null || target.isGM()) {
			return;
		}
		switch (hechizo.Tipo) {
		case uEstado: // Afectan estados (por ejem : Envenenamiento)
			exito = hechizoEstadoUsuario();
			break;
		case uPropiedades: // Afectan HP,MANA,STAMINA,ETC
			exito = hechizoPropUsuario();
			break;
		}
		if (exito) {
			this.player.subirSkill(Skill.SKILL_Magia);
			this.player.stats().quitarMana(hechizo.ManaRequerido);
			this.player.stats().quitarStamina(hechizo.StaRequerida);
			this.player.sendUpdateUserStats();
			this.player.flags().TargetUser = 0;
		}
	}

	private void handleHechizoNPC(Spell hechizo) {
		boolean exito = false;
		Npc targetNPC = this.server.npcById(this.player.flags().TargetNpc);
		switch (hechizo.Tipo) {
		case uEstado: // Afectan estados (por ejem : Envenenamiento)
			exito = hechizoEstadoNPC(targetNPC, hechizo);
			break;
		case uPropiedades: // Afectan HP,MANA,STAMINA,ETC
			exito = hechizoPropNPC(targetNPC, hechizo);
			break;
		}
		if (exito) {
			this.player.subirSkill(Skill.SKILL_Magia);
			this.player.flags().TargetNpc = 0;
			this.player.stats().quitarMana(hechizo.ManaRequerido);
			this.player.stats().quitarStamina(hechizo.StaRequerida);
			this.player.sendUpdateUserStats();
		}
	}

	public void infoHechizo() {
		Map mapa = this.server.getMap(this.player.pos().map);
		Spell hechizo = this.server.getHechizo(this.player.flags().Hechizo);
		this.player.decirPalabrasMagicas(hechizo.PalabrasMagicas);
		this.player.enviarSonido(hechizo.WAV);
		if (this.player.flags().TargetUser > 0) {
			mapa.sendCreateFX(this.player.pos().x, this.player.pos().y, this.player.flags().TargetUser,
				hechizo.FXgrh, hechizo.loops);
		} else if (this.player.flags().TargetNpc > 0) {
			mapa.sendCreateFX(this.player.pos().x, this.player.pos().y, this.player.flags().TargetNpc,
				hechizo.FXgrh, hechizo.loops);
		}
		if (this.player.flags().TargetUser > 0) {
			if (this.player.getId() != this.player.flags().TargetUser) {
				Player target = this.server.playerById(this.player.flags().TargetUser);
				this.player.sendMessage(hechizo.HechiceroMsg + " " + target.userName,
					FONTTYPE_FIGHT);
				target.sendMessage(this.player.getNick() + " " + hechizo.TargetMsg,
					FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage(hechizo.PropioMsg, FONTTYPE_FIGHT);
			}
		} else if (this.player.flags().TargetNpc > 0) {
			this.player.sendMessage(hechizo.HechiceroMsg + "la criatura.",
				FONTTYPE_FIGHT);
		}
	}

}
