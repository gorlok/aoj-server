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
	
	short m_hechizos[] = new short[MAX_HECHIZOS];
	
	private Player player;
	private GameServer server;
	
	public UserSpells(GameServer server, Player player) {
		this.server = server;
		this.player = player;
	}
	
	public int getCount() {
		return m_hechizos.length;
	}
	
	public short getSpell(int slot) {
		return m_hechizos[slot -1];
	}
	
	public void setSpell(int slot, short spell) {
		m_hechizos[slot - 1] = spell;
	}
	
	public boolean isSlotEmpty(int slot) {
		return getSpell(slot) == 0;
	}
	
	private boolean isSlotValid(int slot) {
		return slot > 0 && slot < m_hechizos.length;
	}
	
	public void enviarHechizos() {
		for (int slot = 1; slot <= m_hechizos.length; slot++) {
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
			player.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) 0, "(Vacío)"));
			return;
		}
		Spell spell = server.getHechizo(getSpell(slot));
		player.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) spell.getId(), spell.getName()));
	}

	public void sendMeSpellInfo(short slot) {
		if (slot < 1 || slot > MAX_HECHIZOS) {
			player.enviarMensaje("¡Primero selecciona el hechizo.!", FontType.FONTTYPE_INFO);
		} else {
			int numHechizo = m_hechizos[slot - 1];
			if (numHechizo > 0) {
				Spell hechizo = server.getHechizo(numHechizo);
				player.enviarMensaje("||%%%%%%%%%%%% INFO DEL HECHIZO %%%%%%%%%%%%",
					FontType.FONTTYPE_INFO);
				player.enviarMensaje("||Nombre: " + hechizo.Nombre, FontType.FONTTYPE_INFO);
				player.enviarMensaje("||Descripcion: " + hechizo.Desc, FontType.FONTTYPE_INFO);
				player.enviarMensaje("||Skill requerido: " + hechizo.MinSkill
						+ " de magia.", FontType.FONTTYPE_INFO);
				player.enviarMensaje("||Mana necesario: " + hechizo.ManaRequerido,
					FontType.FONTTYPE_INFO);
				player.enviarMensaje("||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
					FontType.FONTTYPE_INFO);
			}
		}
	}

	public void castSpell(short slot) {
		
		System.out.println("ENTRO");
		if (slot < 1 || slot > m_hechizos.length) {
			return;
		}
		
		System.out.println(" " + slot + "");
		
		player.flags().Hechizo = m_hechizos[slot - 1];
		log.info("doLanzarHechizo =====> " + player.flags().Hechizo);
	}

	public boolean tieneHechizo(int numHechizo) {
		for (short element : m_hechizos) {
			if (element == numHechizo) {
				return true;
			}
		}
		return false;
	}

	private int slotLibreHechizos() {
		for (int i = 0; i < m_hechizos.length; i++) {
			if (m_hechizos[i] == 0) {
				return i + 1;
			}
		}
		return 0;
	}

	public void agregarHechizo(int slot) {
		int oid = player.userInv().getObjeto(slot).objid;
		ObjectInfo objHechizo = server.getObjectInfoStorage().getInfoObjeto(oid);
		short numHechizo = objHechizo.HechizoIndex;
		if (!tieneHechizo(numHechizo)) {
			// Buscamos un slot vacio
			int slotLibre = slotLibreHechizos();
			if (slotLibre == 0) {
				player.enviarMensaje("No tienes espacio para más hechizos.",
					FontType.FONTTYPE_INFO);
			} else {
				// Actualizamos la lista de hechizos,
				changeUserHechizo(slotLibre, numHechizo);
				// y quitamos el item del inventario.
				player.userInv().quitarUserInvItem(slot, 1);
			}
		} else {
			player.enviarMensaje("Ya tienes ese hechizo.", FontType.FONTTYPE_INFO);
		}
	}

	public void moveSpell(int slot, short direction) {
		if (direction == 1) {
			// Move spell upward
			if (slot == 1) {
				player.enviarMensaje("No puedes mover el hechizo en esa direccion.",
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
				player.enviarMensaje("No puedes mover el hechizo en esa direccion.",
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
				if (player.flags().TargetUser > 0) {
					handleHechizoUsuario(hechizo);
				} else {
					player.enviarMensaje("Este hechizo actua solo sobre usuarios.",
						FontType.FONTTYPE_INFO);
				}
				break;
			case uNPC:
				if (player.flags().TargetNpc > 0) {
					handleHechizoNPC(hechizo);
				} else {
					player.enviarMensaje("Este hechizo solo afecta a los npcs.",
						FontType.FONTTYPE_INFO);
				}
				break;
			case uUsuariosYnpc:
				if (player.flags().TargetUser > 0) {
					handleHechizoUsuario(hechizo);
				} else if (player.flags().TargetNpc > 0) {
					handleHechizoNPC(hechizo);
				} else {
					player.enviarMensaje("Target inválido.", FontType.FONTTYPE_INFO);
				}
				break;
			case uTerreno:
				handleHechizoTerreno(hechizo);
				break;
			}
		}
		player.flags().Trabajando = false;
	}

	public boolean hechizoEstadoUsuario() {
		Spell hechizo = server.getHechizo(player.flags().Hechizo);
		Player targetUser = server.playerById(player.flags().TargetUser);
		if (hechizo.Invisibilidad == 1) {
			targetUser.flags().Invisible = true;
			//mapa.enviarATodos(MSG_NOVER, targetUser.m_id, 1);
			infoHechizo();
			return true;
		}
		if (hechizo.Envenena == 1) {
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
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
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
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
				if (!player.puedeAtacar(targetUser)) {
					return false;
				}
				if (player != targetUser) {
					player.usuarioAtacadoPorUsuario(targetUser);
				}
				targetUser.flags().Paralizado = true;
				targetUser.m_counters.Paralisis = IntervaloParalizado;
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
					if (player != targetUser) {
						player.m_reputacion.incNoble(500);
						player.enviarMensaje(
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
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Ceguera = true;
			targetUser.m_counters.Ceguera = IntervaloParalizado;
			//targetUser.enviar(MSG_CEGU);
			infoHechizo();
			return true;
		}
		if (hechizo.Estupidez == 1) {
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Estupidez = true;
			targetUser.m_counters.Ceguera = IntervaloParalizado;
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
				player.enviarMensaje("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
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
				player.enviarMensaje("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
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
			player.enviarMensaje("El npc es inmune a este hechizo.", FONTTYPE_FIGHT);
		}
		if (hechizo.RemoverParalisis == 1) {
			if (npc.estaParalizado()) {
				infoHechizo();
				npc.desparalizar();
				return true;
			}
			player.enviarMensaje("El npc no está paralizado.", FONTTYPE_FIGHT);
		}
		return false;
	}

	public boolean hechizoPropNPC(Npc npc, Spell hechizo) {
		// Salud
		if (hechizo.SubeHP == 1) {
			// Curar la salud
			int cura = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			cura += Util.porcentaje(cura, 3 * player.stats().ELV);
			infoHechizo();
			npc.m_estads.addMinHP(cura);
			player.enviarMensaje("Has curado " + cura
					+ " puntos de salud a la criatura.", FONTTYPE_FIGHT);
			return true;
		} else if (hechizo.SubeHP == 2) {
			// Dañar la salud
			if (!npc.getAttackable()) {
				player.enviarMensaje("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			if (npc.getPetUserOwner() != null
					&& (player.flags().Seguro || player.userFaction().ArmadaReal)) {
				if (!npc.getPetUserOwner().isCriminal()) {
					if (player.userFaction().ArmadaReal) {
						player.enviarMensaje("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
							FontType.FONTTYPE_WARNING);
					} else {
						player.enviarMensaje("Tienes el seguro activado. Presiona la tecla *.",
							FontType.FONTTYPE_WARNING);
					}
					return false;
				}
			}
			
			//FIX by AGUSH: we check if user can attack to guardians
			if (npc.isNpcGuard() && player.hasSafeLock()) {
				player.enviarMensaje("Tienes el seguro activado. Presiona la tecla *.",
						FontType.FONTTYPE_INFO);
				return false;
			}
			
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * player.stats().ELV);
			infoHechizo();
			player.npcAtacado(npc);
			if (npc.getSonidoAtaqueExitoso() > 0) {
				player.enviarSonido(npc.getSonidoAtaqueExitoso());
			}
			npc.m_estads.quitarHP(daño);
			player.enviarMensaje("Le has causado " + daño
					+ " puntos de daño a la criatura!", FONTTYPE_FIGHT);
			npc.calcularDarExp(player, daño);
			if (npc.m_estads.MinHP < 1) {
				npc.muereNpc(player);
			}
			return true;
		}
		return false;
	}

	public boolean hechizoPropUsuario() {
		Spell hechizo = server.getHechizo(player.flags().Hechizo);
		Player targetUser = server.playerById(player.flags().TargetUser);
		// Hambre
		if (hechizo.SubeHam == 1) {
			// Aumentar hambre
			infoHechizo();
			int daño = Util.Azar(hechizo.MinHam, hechizo.MaxHam);
			targetUser.m_estads.aumentarHambre(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has restaurado " + daño
						+ " puntos de hambre a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha restaurado "
						+ daño + " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has restaurado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			targetUser.enviarEstadsHambreSed();
			return true;
		} else if (hechizo.SubeHam == 2) {
			// Quitar hambre
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinHam, hechizo.MaxHam);
			targetUser.m_estads.quitarHambre(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has quitado " + daño
						+ " puntos de hambre a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha quitado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has quitado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			targetUser.enviarEstadsHambreSed();
			if (targetUser.m_estads.eaten < 1) {
				targetUser.m_estads.eaten = 0;
				targetUser.flags().Hambre = true;
			}
			return true;
		}
		// Sed
		if (hechizo.SubeSed == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSed, hechizo.MaxSed);
			targetUser.m_estads.aumentarSed(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has restaurado " + daño
						+ " puntos de sed a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha restaurado "
						+ daño + " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has restaurado " + daño
						+ " puntos de sed.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeSed == 2) {
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSed, hechizo.MaxSed);
			targetUser.m_estads.quitarSed(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has quitado " + daño
						+ " puntos de sed a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha quitado " + daño
						+ " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje(
					"Te has quitado " + daño + " puntos de sed.",
					FONTTYPE_FIGHT);
			}
			if (targetUser.m_estads.drinked < 1) {
				targetUser.m_estads.drinked = 0;
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
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
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
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
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
			daño += Util.porcentaje(daño, 3 * player.stats().ELV);
			infoHechizo();
			targetUser.m_estads.addMinHP(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has restaurado " + daño
						+ " puntos de vida a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha restaurado "
						+ daño + " puntos de vida.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has restaurado " + daño
						+ " puntos de vida.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeHP == 2) {
			if (player == targetUser) {
				player.enviarMensaje("No puedes atacarte a ti mismo.",
					FONTTYPE_FIGHT);
				return false;
			}
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * player.stats().ELV);
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.m_estads.quitarHP(daño);
			player.enviarMensaje("Le has quitado " + daño + " puntos de vida a "
					+ targetUser.m_nick, FONTTYPE_FIGHT);
			targetUser.enviarMensaje(player.getNick() + " te ha quitado " + daño
					+ " puntos de vida.", FONTTYPE_FIGHT);
			// Muere
			if (targetUser.m_estads.MinHP < 1) {
				player.contarMuerte(targetUser);
				targetUser.m_estads.MinHP = 0;
				player.actStats(targetUser);
				targetUser.userDie();
			}
			return true;
		}
		// Mana
		if (hechizo.SubeMana == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinMana, hechizo.MaxMana);
			targetUser.m_estads.aumentarMana(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has restaurado " + daño
						+ " puntos de mana a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha restaurado "
						+ daño + " puntos de mana.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has restaurado " + daño
						+ " puntos de mana.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeMana == 2) {
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinMana, hechizo.MaxMana);
			if (player != targetUser) {
				player.enviarMensaje("Le has quitado " + daño
						+ " puntos de mana a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha quitado " + daño
						+ " puntos de mana.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has quitado " + daño
						+ " puntos de mana.", FONTTYPE_FIGHT);
			}
			targetUser.m_estads.quitarMana(daño);
			return true;
		}
		// Stamina
		if (hechizo.SubeSta == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSta, hechizo.MaxSta);
			targetUser.m_estads.aumentarStamina(daño);
			if (player != targetUser) {
				player.enviarMensaje("Le has restaurado " + daño
						+ " puntos de energia a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha restaurado "
						+ daño + " puntos de energia.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has restaurado " + daño
						+ " puntos de energia.", FONTTYPE_FIGHT);
			}
			return true;
		} else if (hechizo.SubeSta == 2) {
			if (!player.puedeAtacar(targetUser)) {
				return false;
			}
			if (player != targetUser) {
				player.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSta, hechizo.MaxSta);
			if (player != targetUser) {
				player.enviarMensaje("Le has quitado " + daño
						+ " puntos de energia a " + targetUser.m_nick,
					FONTTYPE_FIGHT);
				targetUser.enviarMensaje(player.getNick() + " te ha quitado " + daño
						+ " puntos de energia.", FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje("Te has quitado " + daño
						+ " puntos de energia.", FONTTYPE_FIGHT);
			}
			targetUser.m_estads.quitarStamina(daño);
			return true;
		}
		return false;
	}

	public boolean puedeLanzar(Spell hechizo) {
		if (!player.checkAlive("No puedes lanzar hechizos porque estas muerto.")) {
			return false;
		}
		MapPos targetPos = MapPos.mxy(player.flags().TargetMap,
			player.flags().TargetX, player.flags().TargetY);
		if (player.pos().distance(targetPos) > MAX_DISTANCIA_MAGIA) {
			player.enviarMensaje("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
			return false;
		}
		if (player.stats().mana < hechizo.ManaRequerido) {
			player.enviarMensaje("No tienes suficiente mana.", FontType.FONTTYPE_INFO);
			return false;
		}
		if (player.skills().get(Skill.SKILL_Magia) < hechizo.MinSkill) {
			player.enviarMensaje(
					"No tienes suficientes puntos de magia para lanzar este hechizo.",
					FontType.FONTTYPE_INFO);
			return false;
		}
		if (player.stats().stamina == 0
				|| player.stats().stamina < hechizo.StaRequerida) {
			player.enviarMensaje("Estas muy cansado para lanzar este hechizo.",
				FontType.FONTTYPE_INFO);
			return false;
		}
		return true;
	}

	private boolean hechizoInvocacion() {
		if (player.getUserPets().isFullPets()) {
			player.enviarMensaje("No puedes invocar más mascotas!", FontType.FONTTYPE_INFO);
			return false;
		}
		
		Map mapa = server.getMap(player.pos().map);
		
		if (mapa.esZonaSegura()) {
			player.enviarMensaje("¡Estás en una zona segura!", FontType.FONTTYPE_INFO);
			return false;
		}
		
		boolean exito = false;
		MapPos targetPos = MapPos.mxy(player.flags().TargetMap,
			player.flags().TargetX, player.flags().TargetY);
		Spell hechizo = server.getHechizo(player.flags().Hechizo);
		for (int i = 0; i < hechizo.Cant; i++) {
			// Considero que hubo exito si se pudo invocar alguna criatura.
			exito = exito || (player.crearMascotaInvocacion(hechizo.NumNpc, targetPos) != null); 
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
			player.subirSkill(Skill.SKILL_Magia);
			player.stats().quitarMana(hechizo.ManaRequerido);
			player.stats().quitarStamina(hechizo.StaRequerida);
			player.sendUpdateUserStats();
		}
	}

	private void handleHechizoUsuario(Spell hechizo) {
		boolean exito = false;
		Player target = server.playerById(player.flags().TargetUser);
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
			player.subirSkill(Skill.SKILL_Magia);
			player.stats().quitarMana(hechizo.ManaRequerido);
			player.stats().quitarStamina(hechizo.StaRequerida);
			player.sendUpdateUserStats();
			player.flags().TargetUser = 0;
		}
	}

	private void handleHechizoNPC(Spell hechizo) {
		boolean exito = false;
		Npc targetNPC = server.npcById(player.flags().TargetNpc);
		switch (hechizo.Tipo) {
		case uEstado: // Afectan estados (por ejem : Envenenamiento)
			exito = hechizoEstadoNPC(targetNPC, hechizo);
			break;
		case uPropiedades: // Afectan HP,MANA,STAMINA,ETC
			exito = hechizoPropNPC(targetNPC, hechizo);
			break;
		}
		if (exito) {
			player.subirSkill(Skill.SKILL_Magia);
			player.flags().TargetNpc = 0;
			player.stats().quitarMana(hechizo.ManaRequerido);
			player.stats().quitarStamina(hechizo.StaRequerida);
			player.sendUpdateUserStats();
		}
	}

	public void infoHechizo() {
		Map mapa = server.getMap(player.pos().map);
		Spell hechizo = server.getHechizo(player.flags().Hechizo);
		player.decirPalabrasMagicas(hechizo.PalabrasMagicas);
		player.enviarSonido(hechizo.WAV);
		if (player.flags().TargetUser > 0) {
			mapa.sendCreateFX(player.pos().x, player.pos().y, player.flags().TargetUser,
				hechizo.FXgrh, hechizo.loops);
		} else if (player.flags().TargetNpc > 0) {
			mapa.sendCreateFX(player.pos().x, player.pos().y, player.flags().TargetNpc,
				hechizo.FXgrh, hechizo.loops);
		}
		if (player.flags().TargetUser > 0) {
			if (player.getId() != player.flags().TargetUser) {
				Player target = server.playerById(player.flags().TargetUser);
				player.enviarMensaje(hechizo.HechiceroMsg + " " + target.m_nick,
					FONTTYPE_FIGHT);
				target.enviarMensaje(player.getNick() + " " + hechizo.TargetMsg,
					FONTTYPE_FIGHT);
			} else {
				player.enviarMensaje(hechizo.PropioMsg, FONTTYPE_FIGHT);
			}
		} else if (player.flags().TargetNpc > 0) {
			player.enviarMensaje(hechizo.HechiceroMsg + "la criatura.",
				FONTTYPE_FIGHT);
		}
	}

}
