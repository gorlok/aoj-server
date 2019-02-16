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

import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.protocol.ServerPacketID;

//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_CEGU;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_DUMB;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_NOVER;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_PARADOK;
//import static org.ArgentumOnline.server.protocol.ClientMessage.MSG_SHS;

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
	
	private Client client;
	private AojServer server;
	
	public UserSpells(AojServer server, Client client) {
		this.server = server;
		this.client = client;
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
			//client.enviar(MSG_SHS, slot, 0, "(None)");
			client.enviar(ServerPacketID.MSG_SHS, (byte) slot, (short) 0, "(Vacío)");
			return;
		}
		Spell spell = server.getHechizo(getSpell(slot));
		//client.enviar(MSG_SHS, slot, spell.getId(), spell.getName());
		client.enviar(ServerPacketID.MSG_SHS, (byte) slot, (short) spell.getId(), spell.getName());
	}

	public void sendMeSpellInfo(short slot) {
		if (slot < 1 || slot > MAX_HECHIZOS) {
			client.enviarMensaje("¡Primero selecciona el hechizo.!", FontType.INFO);
		} else {
			int numHechizo = m_hechizos[slot - 1];
			if (numHechizo > 0) {
				Spell hechizo = server.getHechizo(numHechizo);
				client.enviarMensaje("||%%%%%%%%%%%% INFO DEL HECHIZO %%%%%%%%%%%%",
					FontType.INFO);
				client.enviarMensaje("||Nombre: " + hechizo.Nombre, FontType.INFO);
				client.enviarMensaje("||Descripcion: " + hechizo.Desc, FontType.INFO);
				client.enviarMensaje("||Skill requerido: " + hechizo.MinSkill
						+ " de magia.", FontType.INFO);
				client.enviarMensaje("||Mana necesario: " + hechizo.ManaRequerido,
					FontType.INFO);
				client.enviarMensaje("||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
					FontType.INFO);
			}
		}
	}

	public void castSpell(short slot) {
		
		System.out.println("ENTRO");
		if (slot < 1 || slot > m_hechizos.length) {
			return;
		}
		
		System.out.println(" " + slot + "");
		
		client.getFlags().Hechizo = m_hechizos[slot - 1];
		log.info("doLanzarHechizo =====> " + client.getFlags().Hechizo);
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
		ObjectInfo objHechizo = server.getInfoObjeto(client.getInv().getObjeto(slot).objid);
		short numHechizo = objHechizo.HechizoIndex;
		if (!tieneHechizo(numHechizo)) {
			// Buscamos un slot vacio
			int slotLibre = slotLibreHechizos();
			if (slotLibre == 0) {
				client.enviarMensaje("No tienes espacio para más hechizos.",
					FontType.INFO);
			} else {
				// Actualizamos la lista de hechizos,
				changeUserHechizo(slotLibre, numHechizo);
				// y quitamos el item del inventario.
				client.getInv().quitarUserInvItem(slot, 1);
			}
		} else {
			client.enviarMensaje("Ya tienes ese hechizo.", FontType.INFO);
		}
	}

	public void moveSpell(int slot, short direction) {
		if (direction == 1) {
			// Move spell upward
			if (slot == 1) {
				client.enviarMensaje("No puedes mover el hechizo en esa direccion.",
					FontType.INFO);
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
				client.enviarMensaje("No puedes mover el hechizo en esa direccion.",
					FontType.INFO);
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
				if (client.getFlags().TargetUser > 0) {
					handleHechizoUsuario(hechizo);
				} else {
					client.enviarMensaje("Este hechizo actua solo sobre usuarios.",
						FontType.INFO);
				}
				break;
			case uNPC:
				if (client.getFlags().TargetNpc > 0) {
					handleHechizoNPC(hechizo);
				} else {
					client.enviarMensaje("Este hechizo solo afecta a los npcs.",
						FontType.INFO);
				}
				break;
			case uUsuariosYnpc:
				if (client.getFlags().TargetUser > 0) {
					handleHechizoUsuario(hechizo);
				} else if (client.getFlags().TargetNpc > 0) {
					handleHechizoNPC(hechizo);
				} else {
					client.enviarMensaje("Target inválido.", FontType.INFO);
				}
				break;
			case uTerreno:
				handleHechizoTerreno(hechizo);
				break;
			}
		}
		client.getFlags().Trabajando = false;
	}

	public boolean hechizoEstadoUsuario() {
		Spell hechizo = server.getHechizo(client.getFlags().Hechizo);
		Client targetUser = server.getCliente(client.getFlags().TargetUser);
		if (hechizo.Invisibilidad == 1) {
			targetUser.m_flags.Invisible = true;
			//mapa.enviarATodos(MSG_NOVER, targetUser.m_id, 1);
			infoHechizo();
			return true;
		}
		if (hechizo.Envenena == 1) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.m_flags.Envenenado = true;
			infoHechizo();
			return true;
		}
		if (hechizo.CuraVeneno == 1) {
			targetUser.m_flags.Envenenado = false;
			infoHechizo();
			return true;
		}
		if (hechizo.Maldicion == 1) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.m_flags.Maldicion = true;
			infoHechizo();
			return true;
		}
		if (hechizo.RemoverMaldicion == 1) {
			targetUser.m_flags.Maldicion = false;
			infoHechizo();
			return true;
		}
		if (hechizo.Bendicion == 1) {
			targetUser.m_flags.Bendicion = true;
			infoHechizo();
			return true;
		}
		if (hechizo.Paraliza == 1) {
			if (!targetUser.m_flags.Paralizado) {
				if (!client.puedeAtacar(targetUser)) {
					return false;
				}
				if (client != targetUser) {
					client.usuarioAtacadoPorUsuario(targetUser);
				}
				targetUser.m_flags.Paralizado = true;
				targetUser.m_counters.Paralisis = IntervaloParalizado;
				targetUser.enviar(ServerPacketID.paradOk);
				infoHechizo();
				return true;
			}
		}
		if (hechizo.RemoverParalisis == 1) {
			if (targetUser.m_flags.Paralizado) {
				targetUser.m_flags.Paralizado = false;
				targetUser.enviar(ServerPacketID.paradOk);
				infoHechizo();
				return true;
			}
		}
		if (hechizo.Revivir == 1) {
			if (!targetUser.isAlive()) {
				if (!targetUser.esCriminal()) {
					if (client != targetUser) {
						client.m_reputacion.incNoble(500);
						client.enviarMensaje(
								"¡Los Dioses te sonrien, has ganado 500 puntos de nobleza!.",
								FontType.INFO);
					}
				}
				targetUser.revivirUsuario();
			}
			infoHechizo();
			return true;
		}
		if (hechizo.Ceguera == 1) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.m_flags.Ceguera = true;
			targetUser.m_counters.Ceguera = IntervaloParalizado;
			//targetUser.enviar(MSG_CEGU);
			infoHechizo();
			return true;
		}
		if (hechizo.Estupidez == 1) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.m_flags.Estupidez = true;
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
				client.enviarMensaje("No puedes atacar a ese npc.", FontType.INFO);
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
				client.enviarMensaje("No puedes atacar a ese npc.", FontType.INFO);
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
		if (hechizo.Paraliza == 1) {
			if (!npc.afectaParalisis()) {
				infoHechizo();
				npc.paralizar();
				return true;
			}
			client.enviarMensaje("El npc es inmune a este hechizo.",
				FontType.FIGHT);
		}
		if (hechizo.RemoverParalisis == 1) {
			if (npc.estaParalizado()) {
				infoHechizo();
				npc.desparalizar();
				return true;
			}
			client.enviarMensaje("El npc no está paralizado.", FontType.FIGHT);
		}
		return false;
	}

	public boolean hechizoPropNPC(Npc npc, Spell hechizo) {
		// Salud
		if (hechizo.SubeHP == 1) {
			// Curar la salud
			int cura = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			cura += Util.porcentaje(cura, 3 * client.getEstads().ELV);
			infoHechizo();
			npc.m_estads.addMinHP(cura);
			client.enviarMensaje("Has curado " + cura
					+ " puntos de salud a la criatura.", FontType.FIGHT);
			return true;
		} else if (hechizo.SubeHP == 2) {
			// Dañar la salud
			if (!npc.getAttackable()) {
				client.enviarMensaje("No puedes atacar a ese npc.", FontType.INFO);
				return false;
			}
			if (npc.getPetUserOwner() != null
					&& (client.getFlags().Seguro || client.getFaccion().ArmadaReal)) {
				if (!npc.getPetUserOwner().esCriminal()) {
					if (client.getFaccion().ArmadaReal) {
						client.enviarMensaje("Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
							FontType.WARNING);
					} else {
						client.enviarMensaje("Tienes el seguro activado. Presiona la tecla *.",
							FontType.WARNING);
					}
					return false;
				}
			}
			
			//FIX by AGUSH: we check if user can attack to guardians
			if (npc.m_NPCtype == Npc.NPCTYPE_GUARDIAS && client.tieneSeguro()) {
				client.enviarMensaje("Tienes el seguro activado. Presiona la tecla *.",
						FontType.INFO);
				return false;
			}
			
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * client.getEstads().ELV);
			infoHechizo();
			client.npcAtacado(npc);
			if (npc.getSonidoAtaqueExitoso() > 0) {
				client.enviarSonido(npc.getSonidoAtaqueExitoso());
			}
			npc.m_estads.quitarHP(daño);
			client.enviarMensaje("Le has causado " + daño
					+ " puntos de daño a la criatura!", FontType.FIGHT);
			npc.calcularDarExp(client, daño);
			if (npc.m_estads.MinHP < 1) {
				npc.muereNpc(client);
			}
			return true;
		}
		return false;
	}

	public boolean hechizoPropUsuario() {
		Spell hechizo = server.getHechizo(client.getFlags().Hechizo);
		Client targetUser = server.getCliente(client.getFlags().TargetUser);
		// Hambre
		if (hechizo.SubeHam == 1) {
			// Aumentar hambre
			infoHechizo();
			int daño = Util.Azar(hechizo.MinHam, hechizo.MaxHam);
			targetUser.m_estads.aumentarHambre(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has restaurado " + daño
						+ " puntos de hambre a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha restaurado "
						+ daño + " puntos de hambre.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has restaurado " + daño
						+ " puntos de hambre.", FontType.FIGHT);
			}
			targetUser.enviarEstadsHambreSed();
			return true;
		} else if (hechizo.SubeHam == 2) {
			// Quitar hambre
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinHam, hechizo.MaxHam);
			targetUser.m_estads.quitarHambre(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has quitado " + daño
						+ " puntos de hambre a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha quitado " + daño
						+ " puntos de hambre.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has quitado " + daño
						+ " puntos de hambre.", FontType.FIGHT);
			}
			targetUser.enviarEstadsHambreSed();
			if (targetUser.m_estads.eaten < 1) {
				targetUser.m_estads.eaten = 0;
				targetUser.m_flags.Hambre = true;
			}
			return true;
		}
		// Sed
		if (hechizo.SubeSed == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSed, hechizo.MaxSed);
			targetUser.m_estads.aumentarSed(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has restaurado " + daño
						+ " puntos de sed a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha restaurado "
						+ daño + " puntos de sed.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has restaurado " + daño
						+ " puntos de sed.", FontType.FIGHT);
			}
			return true;
		} else if (hechizo.SubeSed == 2) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSed, hechizo.MaxSed);
			targetUser.m_estads.quitarSed(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has quitado " + daño
						+ " puntos de sed a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha quitado " + daño
						+ " puntos de sed.", FontType.FIGHT);
			} else {
				client.enviarMensaje(
					"Te has quitado " + daño + " puntos de sed.",
					FontType.FIGHT);
			}
			if (targetUser.m_estads.drinked < 1) {
				targetUser.m_estads.drinked = 0;
				targetUser.m_flags.Sed = true;
			}
			return true;
		}
		// <-------- Agilidad ---------->
		if (hechizo.SubeAgilidad == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinAgilidad, hechizo.MaxAgilidad);
			targetUser.m_flags.DuracionEfecto = 1200;
			targetUser.m_estads.aumentarAtributo(ATRIB_AGILIDAD, daño);
			targetUser.m_flags.TomoPocion = true;
			return true;
		} else if (hechizo.SubeAgilidad == 2) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.m_flags.TomoPocion = true;
			int daño = Util.Azar(hechizo.MinAgilidad, hechizo.MaxAgilidad);
			targetUser.m_flags.DuracionEfecto = 700;
			targetUser.m_estads.disminuirAtributo(ATRIB_AGILIDAD, daño);
			return true;
		}
		// <-------- Fuerza ---------->
		if (hechizo.SubeFuerza == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinFuerza, hechizo.MaxFuerza);
			targetUser.m_flags.DuracionEfecto = 1200;
			targetUser.m_estads.aumentarAtributo(ATRIB_FUERZA, daño);
			targetUser.m_flags.TomoPocion = true;
			return true;
		} else if (hechizo.SubeFuerza == 2) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.m_flags.TomoPocion = true;
			int daño = Util.Azar(hechizo.MinFuerza, hechizo.MaxFuerza);
			targetUser.m_flags.DuracionEfecto = 700;
			targetUser.m_estads.disminuirAtributo(ATRIB_FUERZA, daño);
			return true;
		}
		// Salud
		if (hechizo.SubeHP == 1) {
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * client.getEstads().ELV);
			infoHechizo();
			targetUser.m_estads.addMinHP(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has restaurado " + daño
						+ " puntos de vida a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha restaurado "
						+ daño + " puntos de vida.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has restaurado " + daño
						+ " puntos de vida.", FontType.FIGHT);
			}
			return true;
		} else if (hechizo.SubeHP == 2) {
			if (client == targetUser) {
				client.enviarMensaje("No puedes atacarte a ti mismo.",
					FontType.FIGHT);
				return false;
			}
			int daño = Util.Azar(hechizo.MinHP, hechizo.MaxHP);
			daño += Util.porcentaje(daño, 3 * client.getEstads().ELV);
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			targetUser.m_estads.quitarHP(daño);
			client.enviarMensaje("Le has quitado " + daño + " puntos de vida a "
					+ targetUser.m_nick, FontType.FIGHT);
			targetUser.enviarMensaje(client.getNick() + " te ha quitado " + daño
					+ " puntos de vida.", FontType.FIGHT);
			// Muere
			if (targetUser.m_estads.MinHP < 1) {
				client.contarMuerte(targetUser);
				targetUser.m_estads.MinHP = 0;
				client.actStats(targetUser);
				targetUser.userDie();
			}
			return true;
		}
		// Mana
		if (hechizo.SubeMana == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinMana, hechizo.MaxMana);
			targetUser.m_estads.aumentarMana(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has restaurado " + daño
						+ " puntos de mana a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha restaurado "
						+ daño + " puntos de mana.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has restaurado " + daño
						+ " puntos de mana.", FontType.FIGHT);
			}
			return true;
		} else if (hechizo.SubeMana == 2) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinMana, hechizo.MaxMana);
			if (client != targetUser) {
				client.enviarMensaje("Le has quitado " + daño
						+ " puntos de mana a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha quitado " + daño
						+ " puntos de mana.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has quitado " + daño
						+ " puntos de mana.", FontType.FIGHT);
			}
			targetUser.m_estads.quitarMana(daño);
			return true;
		}
		// Stamina
		if (hechizo.SubeSta == 1) {
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSta, hechizo.MaxSta);
			targetUser.m_estads.aumentarStamina(daño);
			if (client != targetUser) {
				client.enviarMensaje("Le has restaurado " + daño
						+ " puntos de energia a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha restaurado "
						+ daño + " puntos de energia.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has restaurado " + daño
						+ " puntos de energia.", FontType.FIGHT);
			}
			return true;
		} else if (hechizo.SubeSta == 2) {
			if (!client.puedeAtacar(targetUser)) {
				return false;
			}
			if (client != targetUser) {
				client.usuarioAtacadoPorUsuario(targetUser);
			}
			infoHechizo();
			int daño = Util.Azar(hechizo.MinSta, hechizo.MaxSta);
			if (client != targetUser) {
				client.enviarMensaje("Le has quitado " + daño
						+ " puntos de energia a " + targetUser.m_nick,
					FontType.FIGHT);
				targetUser.enviarMensaje(client.getNick() + " te ha quitado " + daño
						+ " puntos de energia.", FontType.FIGHT);
			} else {
				client.enviarMensaje("Te has quitado " + daño
						+ " puntos de energia.", FontType.FIGHT);
			}
			targetUser.m_estads.quitarStamina(daño);
			return true;
		}
		return false;
	}

	public boolean puedeLanzar(Spell hechizo) {
		if (!client.checkAlive("No puedes lanzar hechizos porque estas muerto.")) {
			return false;
		}
		MapPos targetPos = MapPos.mxy(client.getFlags().TargetMap,
			client.getFlags().TargetX, client.getFlags().TargetY);
		if (client.getPos().distance(targetPos) > MAX_DISTANCIA_MAGIA) {
			client.enviarMensaje("Estás demasiado lejos.", FontType.INFO);
			return false;
		}
		if (client.getEstads().mana < hechizo.ManaRequerido) {
			client.enviarMensaje("No tienes suficiente mana.", FontType.INFO);
			return false;
		}
		if (client.getEstads().userSkills[Skill.SKILL_Magia] < hechizo.MinSkill) {
			client.enviarMensaje(
					"No tienes suficientes puntos de magia para lanzar este hechizo.",
					FontType.INFO);
			return false;
		}
		if (client.getEstads().stamina == 0
				|| client.getEstads().stamina < hechizo.StaRequerida) {
			client.enviarMensaje("Estas muy cansado para lanzar este hechizo.",
				FontType.INFO);
			return false;
		}
		return true;
	}

	private boolean hechizoInvocacion() {
		if (client.m_cantMascotas >= MAXMASCOTAS) {
			client.enviarMensaje("No puedes invocar más mascotas!", FontType.INFO);
			return false;
		}
		
		Map mapa = server.getMapa(client.getPos().map);
		
		if (mapa.esZonaSegura()) {
			client.enviarMensaje("¡Estás en una zona segura!", FontType.INFO);
			return false;
		}
		
		boolean exito = false;
		MapPos targetPos = MapPos.mxy(client.getFlags().TargetMap,
			client.getFlags().TargetX, client.getFlags().TargetY);
		Spell hechizo = server.getHechizo(client.getFlags().Hechizo);
		for (int i = 0; i < hechizo.Cant; i++) {
			// Considero que hubo exito si se pudo invocar alguna criatura.
			exito = exito || (client.crearMascotaInvocacion(hechizo.NumNpc, targetPos) != null); 
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
			client.subirSkill(Skill.SKILL_Magia);
			client.getEstads().quitarMana(hechizo.ManaRequerido);
			client.getEstads().quitarStamina(hechizo.StaRequerida);
			client.refreshStatus(3);
			client.refreshStatus(4);
		}
	}

	private void handleHechizoUsuario(Spell hechizo) {
		boolean exito = false;
		Client target = server.getCliente(client.getFlags().TargetUser);
		if (target == null || target.esGM()) {
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
			client.subirSkill(Skill.SKILL_Magia);
			client.getEstads().quitarMana(hechizo.ManaRequerido);
			client.getEstads().quitarStamina(hechizo.StaRequerida);
			client.refreshStatus(3);
			client.refreshStatus(4);
			//target.enviarEstadsUsuario();
			client.getFlags().TargetUser = 0;
		}
	}

	private void handleHechizoNPC(Spell hechizo) {
		boolean exito = false;
		Npc targetNPC = server.getNPC(client.getFlags().TargetNpc);
		switch (hechizo.Tipo) {
		case uEstado: // Afectan estados (por ejem : Envenenamiento)
			exito = hechizoEstadoNPC(targetNPC, hechizo);
			break;
		case uPropiedades: // Afectan HP,MANA,STAMINA,ETC
			exito = hechizoPropNPC(targetNPC, hechizo);
			break;
		}
		if (exito) {
			client.subirSkill(Skill.SKILL_Magia);
			client.getFlags().TargetNpc = 0;
			client.getEstads().quitarMana(hechizo.ManaRequerido);
			client.getEstads().quitarStamina(hechizo.StaRequerida);
			client.refreshStatus(3);
			client.refreshStatus(4);
		}
	}

	public void infoHechizo() {
		Map mapa = server.getMapa(client.getPos().map);
		Spell hechizo = server.getHechizo(client.getFlags().Hechizo);
		client.decirPalabrasMagicas(hechizo.PalabrasMagicas);
		client.enviarSonido(hechizo.WAV);
		if (client.getFlags().TargetUser > 0) {
			mapa.enviarCFX(client.getPos().x, client.getPos().y, client.getFlags().TargetUser,
				hechizo.FXgrh, hechizo.loops);
		} else if (client.getFlags().TargetNpc > 0) {
			mapa.enviarCFX(client.getPos().x, client.getPos().y, client.getFlags().TargetNpc,
				hechizo.FXgrh, hechizo.loops);
		}
		if (client.getFlags().TargetUser > 0) {
			if (client.getId() != client.getFlags().TargetUser) {
				Client target = server.getCliente(client.getFlags().TargetUser);
				client.enviarMensaje(hechizo.HechiceroMsg + " " + target.m_nick,
					FontType.FIGHT);
				target.enviarMensaje(client.getNick() + " " + hechizo.TargetMsg,
					FontType.FIGHT);
			} else {
				client.enviarMensaje(hechizo.PropioMsg, FontType.FIGHT);
			}
		} else if (client.getFlags().TargetNpc > 0) {
			client.enviarMensaje(hechizo.HechiceroMsg + "la criatura.",
				FontType.FIGHT);
		}
	}

}
