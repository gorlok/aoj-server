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
package org.ArgentumOnline.server.user;

import static org.ArgentumOnline.server.util.FontType.FONTTYPE_FIGHT;

import org.ArgentumOnline.server.Clazz;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.Pos;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.map.Tile.Trigger;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcType;
import org.ArgentumOnline.server.protocol.BlindResponse;
import org.ArgentumOnline.server.protocol.ChangeSpellSlotResponse;
import org.ArgentumOnline.server.protocol.CreateFXResponse;
import org.ArgentumOnline.server.protocol.DumbResponse;
import org.ArgentumOnline.server.protocol.ParalizeOKResponse;
import org.ArgentumOnline.server.protocol.SetInvisibleResponse;
import org.ArgentumOnline.server.protocol.UpdateManaResponse;
import org.ArgentumOnline.server.protocol.UpdateStaResponse;
import org.ArgentumOnline.server.user.Player.DuelStatus;
import org.ArgentumOnline.server.user.UserAttributes.Attribute;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gorlok
 */
public class UserSpells implements Constants {

	private static Logger log = LogManager.getLogger();

	short spells[] = new short[MAX_SPELLS];

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
		return this.spells[slot - 1];
	}

	public void setSpell(int slot, short spell) {
		this.spells[slot - 1] = spell;
	}

	private boolean isSlotEmpty(int slot) {
		return getSpell(slot) == 0;
	}

	private boolean isSlotValid(int slot) {
		return slot > 0 && slot < this.spells.length;
	}

	public void sendSpells() {
		for (int slot = 1; slot <= this.spells.length; slot++) {
			sendSpell(slot);
		}
	}

	private void changeUserSpell(int slot, short spell) {
		setSpell(slot, spell);
		sendSpell(slot);
	}

	private void sendSpell(int slot) {
		if (!isSlotValid(slot)) {
			return;
		}
		if (isSlotEmpty(slot)) {
			this.player.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) 0, "(Vacío)"));
			return;
		}
		Spell spell = this.server.getSpell(getSpell(slot));
		this.player.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) spell.getId(), spell.getName()));
	}

	public void sendSpellInfo(short slot) {
		// Comando INFS"
		if (slot < 1 || slot > MAX_SPELLS) {
			this.player.sendMessage("¡Primero selecciona el hechizo.!", FontType.FONTTYPE_INFO);
		} else {
			int spellIndex = this.spells[slot - 1];
			if (spellIndex > 0) {
				Spell spell = this.server.getSpell(spellIndex);
				this.player.sendMessage("||%%%%%%%%%%%% INFO DEL HECHIZO %%%%%%%%%%%%",
						FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Nombre: " + spell.Nombre, FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Descripcion: " + spell.Desc, FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Skill requerido: " + spell.MinSkill
						+ " de magia.", FontType.FONTTYPE_INFO);
				this.player.sendMessage("||Mana necesario: " + spell.ManaRequerido,
						FontType.FONTTYPE_INFO);
				this.player.sendMessage("||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void castSpell(short slot) {
		if (slot < 1 || slot > this.spells.length) {
			return;
		}
		this.player.flags().Hechizo = this.spells[slot - 1];
		log.info("castSpell =====> " + this.player.flags().Hechizo);
	}

	public boolean hasSpell(int spellIndex) {
		for (short spell : this.spells) {
			if (spell == spellIndex) {
				return true;
			}
		}
		return false;
	}

	private int freeSpellSlot() {
		for (int i = 0; i < this.spells.length; i++) {
			if (this.spells[i] == 0) {
				return i + 1;
			}
		}
		return 0;
	}

	public void addSpell(int slot) {
		int oid = this.player.userInv().getObjeto(slot).objid;
		ObjectInfo spellObj = this.server.getObjectInfoStorage().getInfoObjeto(oid);
		short spellIndex = spellObj.HechizoIndex;
		if (!hasSpell(spellIndex)) {
			// Buscamos un slot vacio
			int slotLibre = freeSpellSlot();
			if (slotLibre == 0) {
				this.player.sendMessage("No tienes espacio para más hechizos.", FontType.FONTTYPE_INFO);
			} else {
				// Actualizamos la lista de hechizos,
				changeUserSpell(slotLibre, spellIndex);
				// y quitamos el item del inventario.
				this.player.userInv().quitarUserInvItem(slot, 1);
			}
		} else {
			this.player.sendMessage("Ya tienes ese hechizo.", FontType.FONTTYPE_INFO);
		}
	}

	public void moveSpell(int slot, short direction) {
		if (direction == -1) {
			// Move spell upward
			if (slot == 1) {
				this.player.sendMessage("No puedes mover el hechizo en esa direccion.",
						FontType.FONTTYPE_INFO);
				return;
			}
			short spell = getSpell(slot);
			setSpell(slot, getSpell(slot - 1));
			setSpell(slot - 1, spell);
			sendSpell(slot - 1);
			sendSpell(slot);
		} else if (direction == 1) {
			// Move spell downward
			if (slot == MAX_SPELLS) {
				this.player.sendMessage("No puedes mover el hechizo en esa direccion.",
						FontType.FONTTYPE_INFO);
				return;
			}
			short spell = getSpell(slot);
			setSpell(slot, getSpell(slot + 1));
			setSpell(slot + 1, spell);
			sendSpell(slot);
			sendSpell(slot + 1);
		}
	}

	public void hitSpell(Spell spell) {
		// LanzarHechizo
		if (canCastSpell(spell)) {
			switch (spell.Target) {
			case USER:
				if (this.player.flags().TargetUser > 0) {
					handleSpellTargetUser(spell);
				} else {
					this.player.sendMessage("Este hechizo actua solo sobre usuarios.",
							FontType.FONTTYPE_INFO);
				}
				break;
			case NPC:
				if (this.player.flags().TargetNpc > 0) {
					handleSpellTargetNPC(spell);
				} else {
					this.player.sendMessage("Este hechizo solo afecta a los npcs.",
							FontType.FONTTYPE_INFO);
				}
				break;
			case USER_AND_NPC:
				if (this.player.flags().TargetUser > 0) {
					handleSpellTargetUser(spell);
				} else if (this.player.flags().TargetNpc > 0) {
					handleSpellTargetNPC(spell);
				} else {
					this.player.sendMessage("Target inválido.", FontType.FONTTYPE_INFO);
				}
				break;
			case TERRAIN:
				handleSpellTargetTerrain(spell);
				break;
			}
		}
		this.player.flags().Trabajando = false;
		this.player.flags().Oculto = false;
	}

	private boolean hechizoEstadoUsuario() {
		// HechizoEstadoUsuario
		Spell spell = this.server.getSpell(this.player.flags().Hechizo);
		Player targetUser = this.server.playerById(this.player.flags().TargetUser);
		
		boolean result = false;
		if (spell.Invisibilidad) {
			if (!targetUser.isAlive()) {
				this.player.sendMessage("¡Está muerto!", FontType.FONTTYPE_INFO);
				return false;
			}
			
		    if (targetUser.m_saliendo) {
		    	if (this.player != targetUser) {
		    		this.player.sendMessage("¡El hechizo no tiene efecto!", FontType.FONTTYPE_INFO);
		    		return false;
		    	} else {
		    		this.player.sendMessage("¡No puedes volverte invisible mientras estás saliendo!", FontType.FONTTYPE_INFO);
		    		return false;
		    	}
		    }
		    
		    // No usar invi mapas InviSinEfecto
		    Map tuMap = server.getMap(targetUser.pos().map);
		    if (tuMap.InviSinEfecto) {
		    	this.player.sendMessage("¡La invisibilidad no funciona aquí!", FontType.FONTTYPE_INFO);
		    	return false;
		    }

		    // Para poder tirar invi a un pk en el ring
		    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !player.isCriminal()) {
		    		if (player.isRoyalArmy()) {
				    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (player.hasSafeLock()) {
				    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		                player.turnCriminal();
		    		}
		    	}
		    }
		    
		    // Los usuarios no pueden usar este hechizo con los GMs.
		    if (!player.flags().isGM() && targetUser.flags().isGM()) {
		    	return false;
		    }
		    
			targetUser.flags().Invisible = true;
			Map map = this.server.getMap(targetUser.pos().map);
			map.sendToArea(targetUser.pos().x, targetUser.pos().y, 
					new SetInvisibleResponse(targetUser.getId(), (byte) 1)); // FIXME está comentado en vb6
			sendInfoSpell();
			result = true;
		}
		
		if (spell.Mimetiza) {
			if (!targetUser.isAlive()) {
				return false;
			}
			if (targetUser.isSailing() || player.isSailing()) {
				return false;
			}
		    // Los usuarios no pueden usar este hechizo con los GMs.
		    if (!player.flags().isGM() && targetUser.flags().isGM()) {
		    	return false;
		    }
		    if (player.flags().Mimetizado) {
		    	this.player.sendMessage("Ya te encuentras transformado. El hechizo no ha tenido efecto", FontType.FONTTYPE_INFO);
		    	return false;
		    }
		    if (targetUser.flags().AdminInvisible) {
		    	return false;
		    }
		    
		    // Guardo el char original en mimetizado
		    player.mimetizeChar().copyFrom(player.infoChar());
		    player.flags().Mimetizado = true;
	        // Le copio el char del target
		    player.infoChar().copyFrom(targetUser.infoChar());
		    player.sendCharacterChange();
		    sendInfoSpell();
			result = true;
		}

		if (spell.Envenena) {
			if (targetUser == player) {
		    	this.player.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Envenenado = true;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.CuraVeneno) {
			if (!targetUser.isAlive()) {
				this.player.sendMessage("¡Está muerto!", FontType.FONTTYPE_INFO);
				return false;
			}
			
		    // Los usuarios no pueden usar este hechizo con los GMs.
		    if (!player.flags().isGM() && targetUser.flags().isGM()) {
		    	return false;
		    }
			
		    // Para poder usar con un pk en el ring
		    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !player.isCriminal()) {
		    		if (player.isRoyalArmy()) {
				    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (player.hasSafeLock()) {
				    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		                player.reputation().disminuyeNoblezaAumentaBandido(player, player.reputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			
			targetUser.flags().Envenenado = false;
			sendInfoSpell();
			result = true;
		}

		if (spell.Maldicion) {
			if (targetUser == player) {
		    	this.player.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Maldicion = true;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.RemoverMaldicion) {
			targetUser.flags().Maldicion = false;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.Bendicion) {
			targetUser.flags().Bendicion = true;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.isParaliza() || spell.isInmoviliza()) {
			if (targetUser == player) {
		    	this.player.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!targetUser.flags().Paralizado) {
				if (!this.player.puedeAtacar(targetUser)) {
					return false;
				}
				if (this.player != targetUser) {
					this.player.usuarioAtacadoPorUsuario(targetUser);
				}
				
	            if (targetUser.userInv().tieneAnilloEquipado() && targetUser.userInv().getAnillo().ObjIndex == SUPERANILLO) {
			    	targetUser.sendMessage("Tu anillo rechaza los efectos del hechizo.", FontType.FONTTYPE_INFO);
			    	player.sendMessage("¡El hechizo no tuvo efecto, ha sido rechazado!", FontType.FONTTYPE_INFO);
			    	return false;
	            }
				
				targetUser.flags().Paralizado = true;
				if (spell.inmoviliza) {
					targetUser.flags().Inmovilizado = true;
				}
				targetUser.counters.Paralisis = IntervaloParalizado;
				targetUser.sendPacket(new ParalizeOKResponse());
				sendInfoSpell();
				result = true;
			}
		}
		
		if (spell.RemoverParalisis) {
			if (targetUser.flags().Paralizado) {
				
			    // Para poder usar con un pk en el ring
			    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
			    	if (targetUser.isCriminal() && !player.isCriminal()) {
			    		if (player.isRoyalArmy()) {
					    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
					    	return false;
			    		}
			    		if (player.hasSafeLock()) {
					    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
					    	return false;
			    		} else {
			                player.turnCriminal();
			    		}
			    	}
			    }
				
				targetUser.flags().Paralizado = false;
				targetUser.flags().Inmovilizado = false;
				targetUser.sendPacket(new ParalizeOKResponse());
				sendInfoSpell();
				result = true;
			}
		}
		
		if (spell.RemoverEstupidez) {
			if (targetUser.isDumb()) {
			    // Para poder usar con un pk en el ring
			    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
			    	if (targetUser.isCriminal() && !player.isCriminal()) {
			    		if (player.isRoyalArmy()) {
					    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
					    	return false;
			    		}
			    		if (player.hasSafeLock()) {
					    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
					    	return false;
			    		} else {
			    			player.reputation().disminuyeNoblezaAumentaBandido(player, player.reputation().nobleRep * 0.5f, 10000);
			    		}
			    	}
			    }
				targetUser.makeNoDumb();
				sendInfoSpell();
				result = true;
			}
		}

		if (spell.Revivir) {
			if (!targetUser.isAlive()) {
				
		        // Seguro de resurreccion (solo afecta a los hechizos, no al sacerdote ni al comando de GM)
		        if (player.flags().SeguroResu) {
		        	this.player.sendMessage("¡El espíritu no tiene intenciones de regresar al mundo de los vivos!", FontType.FONTTYPE_INFO);
		        	return false;
		        }
		    
		        // No usar resu en mapas con ResuSinEfecto
				Map map = this.server.getMap(targetUser.pos().map);
		        if (map.ResuSinEfecto) {
		        	this.player.sendMessage("¡Revivir no está permitido aqui! Retirate de la Zona si deseas utilizar el Hechizo.", FontType.FONTTYPE_INFO);
		        	return false;
		        }
		        
		        // No podemos resucitar si nuestra barra de energía no está llena. (GD: 29/04/07)
		        if (!player.stats().isFullStamina()) {
		        	this.player.sendMessage("No puedes resucitar si no tienes tu barra de energía llena.", FontType.FONTTYPE_INFO);
		        	return false;
		        }
		        
		        // revisamos si tiene vara, laud, o flauta
		        if (player.clazz() == Clazz.Mage) {
		        	if (!player.userInv().tieneArmaEquipada() || player.userInv().getArma().StaffPower == 0) {
			        	this.player.sendMessage("Necesitas un báculo para este hechizo.", FontType.FONTTYPE_INFO);
			        	return false;
		        	}
		        	if (player.userInv().getArma().StaffPower < spell.NeedStaff) {
			        	this.player.sendMessage("Necesitas un mejor báculo para este hechizo.", FontType.FONTTYPE_INFO);
			        	return false;
		        	}
		        } else if (player.clazz() == Clazz.Bard) {
		            if (!player.userInv().tieneAnilloEquipado() || player.userInv().getAnillo().ObjIndex != LAUDMAGICO) {
			        	this.player.sendMessage("Necesitas un instrumento mágico para devolver la vida.", FontType.FONTTYPE_INFO);
			        	return false;
		            }
		        } else if (player.clazz() == Clazz.Druid) {
		            if (!player.userInv().tieneAnilloEquipado() || player.userInv().getAnillo().ObjIndex != FLAUTAMAGICA) {
			        	this.player.sendMessage("Necesitas un instrumento mágico para devolver la vida.", FontType.FONTTYPE_INFO);
			        	return false;
		            }
		        }
		        
		        boolean eraCriminal = player.isCriminal();
		        
			    // Para poder usar con un pk en el ring
			    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
			    	if (targetUser.isCriminal() && !player.isCriminal()) {
			    		if (player.isRoyalArmy()) {
					    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
					    	return false;
			    		}
			    		if (player.hasSafeLock()) {
					    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
					    	return false;
			    		} else {
			    			player.turnCriminal();
			    		}
			    	}
			    }
		        
				if (!targetUser.isCriminal()) {
					if (this.player != targetUser) {
						this.player.reputation.incNoble(500);
						this.player.sendMessage(
								"¡Los Dioses te sonrien, has ganado 500 puntos de nobleza!.",
								FontType.FONTTYPE_INFO);
					}
				}
				
				if (!eraCriminal && player.isCriminal()) {
					player.refreshCharStatus();
				}

				targetUser.stats().drinked = 0;
				targetUser.flags().Sed = true;
				targetUser.stats().eaten = 0;
				targetUser.flags().Hambre = true;
				targetUser.refreshCharStatus();
				targetUser.revive();
				
				sendInfoSpell();
				player.stats().mana = 0;
				player.stats().stamina = 0;
		        
		        // Agregado para quitar la penalización de vida en el ring y cambio de ecuacion. (NicoNZ)
				if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		            // Solo saco vida si es User. no quiero que exploten GMs por ahi.
		            if (!player.flags().isGM()) {
		            	player.stats().MinHP = (int) (player.stats().MinHP * (1 - player.stats().ELV * 0.015f));
		            }
				}
		        
				if (player.stats().MinHP <= 0) {
					player.userDie();
					player.sendMessage("El esfuerzo de resucitar fue demasiado grande.", FontType.FONTTYPE_INFO);
					return false;
				} else {
					player.sendMessage("El esfuerzo de resucitar te ha debilitado.", FontType.FONTTYPE_INFO);
					result = true;
				}			
			}
		}
		
		if (spell.Ceguera) {
			if (targetUser == player) {
		    	this.player.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.flags().Ceguera = true;
			targetUser.counters.Ceguera = IntervaloParalizado / 3;
			targetUser.sendPacket(new BlindResponse());
			sendInfoSpell();
			result = true;
		}
		
		if (spell.estupidez) {
			if (targetUser == player) {
		    	this.player.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			
			if (!targetUser.flags().Estupidez) {
				targetUser.flags().Estupidez = true;
				targetUser.counters.Ceguera = IntervaloParalizado;
			}
			targetUser.sendPacket(new DumbResponse());
			sendInfoSpell();
			result = true;
		}
		
		return result;
	}

	private boolean hechizoEstadoNPC(Npc npc, Spell spell) {
		// hechizoEstadoNPC
		if (spell.Invisibilidad) {
			sendInfoSpell();
			npc.makeInvisible();
			return true;
		}
		if (spell.Envenena) {
			if (!npc.isAttackable()) {
				this.player.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			sendInfoSpell();
			npc.envenenar();
			return true;
		}
		if (spell.CuraVeneno) {
			sendInfoSpell();
			npc.curarVeneno();
			return true;
		}
		if (spell.Maldicion) {
			if (!npc.isAttackable()) {
				this.player.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			sendInfoSpell();
			npc.turnDamned();
			return true;
		}
		if (spell.RemoverMaldicion) {
			sendInfoSpell();
			npc.removeDamned();
			return true;
		}
		if (spell.Bendicion) {
			// No hay contra-hechizo de bendicion???
			sendInfoSpell();
			npc.turnBlessed();
			return true;
		}
		if (spell.isParaliza()) {
			if (!npc.afectaParalisis()) {
				sendInfoSpell();
				npc.paralizar();
				npc.desinmovilizar();
				return true;
			}
			this.player.sendMessage("El npc es inmune a este hechizo.", FONTTYPE_FIGHT);
		}
		if (spell.RemoverParalisis) {
			if (npc.isParalized() || npc.isInmovilized()) {
				if (npc.getPetUserOwner() == this.player) {
					sendInfoSpell();
					npc.desparalizar();
					npc.desinmovilizar();// FIXME revisar...
					return true;
				} else if (npc.npcType() == NpcType.NPCTYPE_GUARDIAS_REAL) {
					if (player.isRoyalArmy()) {
						sendInfoSpell();
						npc.desparalizar();
						npc.desinmovilizar();// FIXME revisar...
						return true;
					} else {
						player.sendMessage("Solo puedes Remover la Parálisis de los Guardias si perteneces a su facción.", FontType.FONTTYPE_INFO);
						return false;
					}
				} else if (npc.npcType() == NpcType.NPCTYPE_GUARDIAS_CAOS) {
					if (player.isDarkLegion()) {
						sendInfoSpell();
						npc.desparalizar();
						npc.desinmovilizar();// FIXME revisar...
						return true;
					} else {
						player.sendMessage("Solo puedes Remover la Parálisis de los Guardias si perteneces a su facción.", FontType.FONTTYPE_INFO);
						return false;
					}
				}
				player.sendMessage("Solo puedes Remover la Parálisis de los NPCs que te consideren su amo", FontType.FONTTYPE_INFO);
			}
			this.player.sendMessage("El npc no está paralizado.", FONTTYPE_FIGHT);
		}
		return false;
	}

	private boolean hechizoPropNPC(Npc npc, Spell spell) {
		// HechizoPropNPC
		// Salud
		if (spell.SubeHP == 1) {
			// Curar la salud
			int cura = Util.Azar(spell.MinHP, spell.MaxHP);
			cura += Util.porcentaje(cura, 3 * this.player.stats().ELV);
			sendInfoSpell();
			npc.stats.addHP(cura);
			this.player.sendMessage("Has curado " + cura
					+ " puntos de salud a la criatura.", FONTTYPE_FIGHT);
			return true;
			
		} else if (spell.SubeHP == 2) {
			// Dañar la salud
			if (!npc.isAttackable()) {
				//this.player.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			
			this.player.npcAtacado(npc);
			int daño = Util.Azar(spell.MinHP, spell.MaxHP);
			daño += Util.porcentaje(daño, 3 * this.player.stats().ELV);
			
			if (spell.StaffAffected) {
				if (player.clazz() == Clazz.Mage) {
					if (player.userInv().tieneArmaEquipada()) {
		                daño = (daño * (player.userInv().getArma().StaffDamageBonus + 70)) / 100;
                        // Aumenta daño segun el staff-
                        // Daño = (Daño* (70 + BonifBáculo)) / 100
					} else {
						daño = (int) (daño * 0.7f); // Baja daño a 70% del original
					}
				}
			}
			if (player.userInv().tieneAnilloEquipado() && 
					(player.userInv().getAnillo().ObjIndex == LAUDMAGICO || player.userInv().getAnillo().ObjIndex == FLAUTAMAGICA)) {
				daño = (int) (daño * 1.04f); // laud magico de los bardos
			}

			sendInfoSpell();
			
			if (npc.getSonidoAtaqueExitoso() > 0) {
				this.player.sendWave(npc.getSonidoAtaqueExitoso());
			}
			
		    // Quizas tenga defenza magica el NPC. Pablo (ToxicWaste)
		    daño = daño - npc.stats().defensaMagica;
		    if (daño < 0) {
		    	daño = 0;
		    }
			
			npc.stats.removeHP(daño);
			this.player.sendMessage("Le has causado " + daño
					+ " puntos de daño a la criatura!", FONTTYPE_FIGHT);
			npc.calcularDarExp(this.player, daño);
			if (npc.stats.MinHP < 1) {
				npc.muereNpc(this.player);
			}
			
			/*
			if (npc.getPetUserOwner() != null
					&& (this.player.flags().Seguro || this.player.userFaction().ArmadaReal)) {
				if (!npc.getPetUserOwner().isCriminal()) {
					if (this.player.userFaction().ArmadaReal) {
						this.player.sendMessage(
								"Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
								FontType.FONTTYPE_WARNING);
					} else {
						this.player.sendMessage("Tienes el seguro activado. Presiona la tecla *.",
								FontType.FONTTYPE_WARNING);
					}
					return false;
				}
			}
			// FIX by AGUSH: we check if user can attack to guardians
			if (npc.isNpcGuard() && this.player.hasSafeLock()) {
				this.player.sendMessage("Tienes el seguro activado. Presiona la tecla *.",
						FontType.FONTTYPE_INFO);
				return false;
			}
			*/

			return true;
		}
		return false;
	}

	private boolean hechizoPropUsuario() {
		// HechizoPropUsuario
		Spell spell = this.server.getSpell(this.player.flags().Hechizo);
		Player targetUser = this.server.playerById(this.player.flags().TargetUser);
		
		if (!targetUser.isAlive()) {
	    	this.player.sendMessage("No podés lanzar ese hechizo a un muerto.", FontType.FONTTYPE_INFO);
	    	return false;
		}

		// Hambre
		if (spell.SubeHam == 1) {
			// Aumentar hambre
			sendInfoSpell();
			int daño = Util.Azar(spell.MinHam, spell.MaxHam);
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

		} else if (spell.SubeHam == 2) {
			// Quitar hambre
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			} else {
				// no puede darse hambre a si mismo
				return false;
			}
			sendInfoSpell();
			int daño = Util.Azar(spell.MinHam, spell.MaxHam);
			targetUser.stats.quitarHambre(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has provocado " + daño
						+ " puntos de hambre a " + targetUser.userName,
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha provocado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage("Te has provocado " + daño
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			if (targetUser.stats.eaten < 1) {
				targetUser.stats.eaten = 0;
				targetUser.flags().Hambre = true;
			}
			targetUser.sendUpdateHungerAndThirst();
			return true;
		}

		// Sed
		if (spell.SubeSed == 1) {
			sendInfoSpell();
			int daño = Util.Azar(spell.MinSed, spell.MaxSed);
			targetUser.stats.aumentarSed(daño);
			targetUser.sendUpdateHungerAndThirst();
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

		} else if (spell.SubeSed == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			} else {
				// no puede darse sed a si mismo
				return false;
			}
			sendInfoSpell();
			int daño = Util.Azar(spell.MinSed, spell.MaxSed);
			targetUser.stats.quitarSed(daño);
			if (this.player != targetUser) {
				this.player.sendMessage("Le has provocado " + daño
						+ " puntos de sed a " + targetUser.userName,
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.player.getNick() + " te ha provocado " + daño
						+ " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage(
						"Te has provocado " + daño + " puntos de sed.",
						FONTTYPE_FIGHT);
			}
			if (targetUser.stats.drinked < 1) {
				targetUser.stats.drinked = 0;
				targetUser.flags().Sed = true;
			}
			targetUser.sendUpdateHungerAndThirst();
			return true;
		}

		// <-------- Agilidad ---------->
		if (spell.SubeAgilidad == 1) {
		    // Para poder usar con un pk en el ring
		    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !player.isCriminal()) {
		    		if (player.isRoyalArmy()) {
				    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (player.hasSafeLock()) {
				    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		    			player.reputation().disminuyeNoblezaAumentaBandido(player, player.reputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			sendInfoSpell();
			int daño = Util.Azar(spell.MinAgilidad, spell.MaxAgilidad);
			targetUser.flags().DuracionEfecto = 1200;
			targetUser.stats().attr().modifyByEffect(Attribute.AGILIDAD, daño);
			targetUser.flags().TomoPocion = true;
			return true;

		} else if (spell.SubeAgilidad == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			targetUser.flags().TomoPocion = true;
			int daño = Util.Azar(spell.MinAgilidad, spell.MaxAgilidad);
			targetUser.flags().DuracionEfecto = 700;
			targetUser.stats().attr().modifyByEffect(Attribute.AGILIDAD, -daño);
			return true;
		}
		
		// <-------- Fuerza ---------->
		if (spell.SubeFuerza == 1) {
		    // Para poder usar con un pk en el ring
		    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !player.isCriminal()) {
		    		if (player.isRoyalArmy()) {
				    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (player.hasSafeLock()) {
				    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		    			player.reputation().disminuyeNoblezaAumentaBandido(player, player.reputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			sendInfoSpell();
			int daño = Util.Azar(spell.MinFuerza, spell.MaxFuerza);
			targetUser.flags().DuracionEfecto = 1200;
			targetUser.stats().attr().modifyByEffect(Attribute.FUERZA, daño);
			targetUser.flags().TomoPocion = true;
			return true;

		} else if (spell.SubeFuerza == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			targetUser.flags().TomoPocion = true;
			int daño = Util.Azar(spell.MinFuerza, spell.MaxFuerza);
			targetUser.flags().DuracionEfecto = 700;
			targetUser.stats().attr().modifyByEffect(Attribute.FUERZA, -daño);
			return true;
		}

		// Salud
		if (spell.SubeHP == 1) {
			if (!targetUser.isAlive()) {
				this.player.sendMessage("¡Está muerto!", FONTTYPE_FIGHT);
				return false;
			}
		    // Para poder usar con un pk en el ring
		    if (player.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !player.isCriminal()) {
		    		if (player.isRoyalArmy()) {
				    	this.player.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (player.hasSafeLock()) {
				    	this.player.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		    			player.reputation().disminuyeNoblezaAumentaBandido(player, player.reputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			int daño = Util.Azar(spell.MinHP, spell.MaxHP);
			daño += Util.porcentaje(daño, 3 * this.player.stats().ELV);
			sendInfoSpell();
			targetUser.stats.addHP(daño);
			targetUser.sendUpdateHP((short) targetUser.stats().MinHP);
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
			
		} else if (spell.SubeHP == 2) {
			if (this.player == targetUser) {
				this.player.sendMessage("No puedes atacarte a ti mismo.",
						FONTTYPE_FIGHT);
				return false;
			}
			int daño = Util.Azar(spell.MinHP, spell.MaxHP);
			daño += Util.porcentaje(daño, 3 * this.player.stats().ELV);
			
			// Potenciador de ataque Báculo
			if (spell.StaffAffected) {
				if (player.clazz() == Clazz.Mage) {
					if (player.userInv().tieneArmaEquipada() && player.userInv().getArma().StaffDamageBonus > 0) {
						daño = (daño * (player.userInv().getArma().StaffDamageBonus + 70)) / 100; 
					} else {
						daño = (int) (daño * 0.7f); // Baja daño a 70% del original
					}
				}
			}
			// Potenciador de ataque Laúd o Flauta
			if (player.userInv().tieneAnilloEquipado() && (player.userInv().getAnillo().ObjIndex == LAUDMAGICO || player.userInv().getAnillo().ObjIndex == FLAUTAMAGICA)) {
				daño = (int) (daño * 1.04f);  // laud magico de los bardos
			}
			
			// Defensa mágica Sombreros
			if (targetUser.userInv().tieneCascoEquipado()) {
				daño = daño - Util.Azar(
						targetUser.userInv().getCasco().DefensaMagicaMin,
						targetUser.userInv().getCasco().DefensaMagicaMax);  // sombreros antimagia
			}
			// Defensa mágica Anillos
			if (targetUser.userInv().tieneAnilloEquipado()) {
				daño = daño - Util.Azar(
						targetUser.userInv().getAnillo().DefensaMagicaMin,
						targetUser.userInv().getAnillo().DefensaMagicaMax);  // anillos antimagia
			}
			if (daño < 0) {
				daño = 0;
			}
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			targetUser.stats.removeHP(daño);
			targetUser.sendUpdateHP((short) targetUser.stats().MinHP);
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
		if (spell.SubeMana == 1) {
			sendInfoSpell();
			int daño = Util.Azar(spell.MinMana, spell.MaxMana);
			targetUser.stats.aumentarMana(daño);
			targetUser.sendPacket(new UpdateManaResponse((short) targetUser.stats().mana));
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

		} else if (spell.SubeMana == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			int daño = Util.Azar(spell.MinMana, spell.MaxMana);
			targetUser.stats.quitarMana(daño);
			targetUser.sendPacket(new UpdateManaResponse((short) targetUser.stats().mana));
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
			return true;
		}

		// Stamina
		if (spell.SubeSta == 1) {
			sendInfoSpell();
			int daño = Util.Azar(spell.MinSta, spell.MaxSta);
			targetUser.stats.aumentarStamina(daño);
			targetUser.sendPacket(new UpdateStaResponse((short) targetUser.stats().stamina));
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

		} else if (spell.SubeSta == 2) {
			if (!this.player.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.player != targetUser) {
				this.player.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			int daño = Util.Azar(spell.MinSta, spell.MaxSta);
			targetUser.stats.quitarStamina(daño);
			targetUser.sendPacket(new UpdateStaResponse((short) targetUser.stats().stamina));
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
			return true;
		}
		return false;
	}

	private boolean canCastSpell(Spell spell) {
		if (!this.player.checkAlive("No puedes lanzar hechizos porque estas muerto.")) {
			return false;
		}
		
	    if (spell.NeedStaff > 0) {
	        if (this.player.clazz() == Clazz.Mage) {
	            if (this.player.userInv().tieneArmaEquipada()) {
	            	if (this.player.userInv().getArma().StaffPower < spell.NeedStaff) {
	            		this.player.sendMessage("No posees un báculo lo suficientemente poderoso para que puedas lanzar el conjuro.", FontType.FONTTYPE_INFO);
	            		return false;
	            	}
	            } else {
	            	this.player.sendMessage("No puedes lanzar este conjuro sin la ayuda de un báculo.", FontType.FONTTYPE_INFO);
	                return false;
	            }
	        }
	    }
		
	    if (this.player.skills().get(Skill.SKILL_Magia) < spell.MinSkill) {
	    	this.player.sendMessage(
	    			"No tienes suficientes puntos de magia para lanzar este hechizo.",
	    			FontType.FONTTYPE_INFO);
	    	return false;
	    }
	    
	    if (this.player.stats().isTooTired() || this.player.stats().stamina < spell.StaRequerida) {
	    	if (this.player.gender() == UserGender.GENERO_HOMBRE) {
	    		this.player.sendMessage("Estas muy cansado para lanzar este hechizo.", FontType.FONTTYPE_INFO);
	    	} else {
	    		this.player.sendMessage("Estas muy cansada para lanzar este hechizo.", FontType.FONTTYPE_INFO);
	    	}
	    	return false;
	    }
	    

	    float druidManaBonus;
	    if (this.player.clazz() == Clazz.Druid) {
	        if (this.player.userInv().tieneAnilloEquipado() 
	        		&& this.player.userInv().getAnillo().ObjIndex == FLAUTAMAGICA) {
	            
	        	if (spell.Mimetiza) {
	                druidManaBonus = 0.5f;
	        	} else if (spell.spellAction == SpellAction.SUMMON) {
	                druidManaBonus = 0.7f;
	        	} else {
	                druidManaBonus = 1f;
	        	}
	        } else {
	            druidManaBonus = 1f;
	        }
	    } else {
	        druidManaBonus = 1f;
	    }	    
	    
	    if (this.player.stats().mana < spell.ManaRequerido * druidManaBonus) {
	    	this.player.sendMessage("No tienes suficiente mana.", FontType.FONTTYPE_INFO);
	    	return false;
	    }
	    
		MapPos targetPos = MapPos.mxy(
				this.player.flags().TargetMap,
				this.player.flags().TargetX,
				this.player.flags().TargetY);
		if (this.player.pos().distance(targetPos) > MAX_DISTANCIA_MAGIA) {
			this.player.sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
			return false;
		}

		return true;
	}

	private boolean hechizoInvocacion() {
		if (this.player.getUserPets().isFullPets()) {
			//this.player.sendMessage("No puedes invocar más mascotas!", FontType.FONTTYPE_INFO);
			return false;
		}

		// No permitimos se invoquen criaturas en zonas seguras
		Map map = this.server.getMap(this.player.pos().map);
		if (map.isSafeMap() || map.getTrigger(player.pos().x, player.pos().y) == Trigger.TRIGGER_ZONA_SEGURA) {
			this.player.sendMessage("En zona segura no puedes invocar criaturas.", FontType.FONTTYPE_INFO);
			return false;
		}
				
		boolean exito = false;
		MapPos targetPos = MapPos.mxy(
								this.player.flags().TargetMap,
								this.player.flags().TargetX, 
								this.player.flags().TargetY);
		Spell hechizo = this.server.getSpell(this.player.flags().Hechizo);
		
		for (int i = 0; i < hechizo.Cant; i++) {
			// Considero que hubo exito si se pudo invocar alguna criatura.
			exito = exito || (this.player.crateSummonedPet(hechizo.NumNpc, targetPos) != null);
		}
		sendInfoSpell();
		return exito;
	}

	private void handleSpellTargetTerrain(Spell spell) {
		if (!player.isInCombatMode()) {
			this.player.sendMessage("Debes estar en modo de combate para lanzar este hechizo.", FontType.FONTTYPE_INFO);
			return;
		}
		
		boolean exito = false;
		switch (spell.spellAction) {
		case SUMMON:
			exito = hechizoInvocacion();
			break;
		case STATUS:
			exito = hechizoTerrenoEstado();
			break;
		}
		
		if (exito) {
			this.player.riseSkill(Skill.SKILL_Magia);
			
			if (player.clazz() == Clazz.Druid && player.userInv().tieneAnilloEquipado() 
					&& player.userInv().getAnillo().ObjIndex == FLAUTAMAGICA) {
				this.player.stats().quitarMana((int) (spell.ManaRequerido * 0.7f));
			} else {
				this.player.stats().quitarMana(spell.ManaRequerido);
			}
			this.player.stats().quitarStamina(spell.StaRequerida);
			this.player.sendUpdateUserStats();
		}
	}

	private boolean hechizoTerrenoEstado() {
		// HechizoTerrenoEstado
		MapPos targetPos = MapPos.mxy(
			this.player.flags().TargetMap,
			this.player.flags().TargetX, 
			this.player.flags().TargetY);
		Map map = this.server.getMap(targetPos.map);
		Spell spell = this.server.getSpell(this.player.flags().Hechizo);
		
		if (spell.RemueveInvisibilidadParcial) {
			for (byte x = (byte) (targetPos.x - 8); x <= targetPos.x + 8; x++) {
				for (byte y = (byte) (targetPos.y - 8); y <= targetPos.y + 8; y++) {
					if (Pos.isValid(x, y)) {
						if (map.hasPlayer(x, y)) {
							Player tmpPlayer = map.getPlayer((byte)x, (byte)y);
							if (tmpPlayer.isInvisible() && !tmpPlayer.flags().AdminInvisible) {
								map.sendToArea(x, y, new CreateFXResponse(tmpPlayer.getId(), spell.FXgrh, (short)spell.loops));
							}
						}
					}
				}
			}
			sendInfoSpell();	
		}
		return true;
	}

	private void handleSpellTargetUser(Spell spell) {
		// HandleHechizoUsuario
		boolean exito = false;
		Player target = this.server.playerById(this.player.flags().TargetUser);
		if (target == null || target.flags().isGM()) {
			return;
		}
		switch (spell.spellAction) {
		case STATUS: // Afectan estados (por ejem : Envenenamiento)
			exito = hechizoEstadoUsuario();
			break;
			
		case PROPERTIES: // Afectan HP,MANA,STAMINA,ETC
			exito = hechizoPropUsuario();
			break;
		}
		
		if (exito) {
			this.player.riseSkill(Skill.SKILL_Magia);
			
		    // Agregado para que los druidas, al tener equipada la flauta magica, el coste de mana de mimetismo es de 50% menos.
		    if (player.clazz() == Clazz.Druid 
		    		&& player.userInv().tieneAnilloEquipado() 
		    		&& player.userInv().getAnillo().ObjIndex == FLAUTAMAGICA
		    		&& spell.Mimetiza){
        		this.player.stats().quitarMana((int) (spell.ManaRequerido * 0.5f));
		    } else {
        		this.player.stats().quitarMana(spell.ManaRequerido);
		    }
			
			this.player.stats().quitarStamina(spell.StaRequerida);
			this.player.sendUpdateUserStats();
			this.player.flags().TargetUser = 0;
		}
	}

	private void handleSpellTargetNPC(Spell spell) {
		// HandleHechizoNPC
		boolean exito = false;
		Npc targetNPC = this.server.npcById(this.player.flags().TargetNpc);
		switch (spell.spellAction) {
		case STATUS: // Afectan estados (por ejem : Envenenamiento)
			exito = hechizoEstadoNPC(targetNPC, spell);
			break;
		case PROPERTIES: // Afectan HP,MANA,STAMINA,ETC
			exito = hechizoPropNPC(targetNPC, spell);
			break;
		}
		if (exito) {
			this.player.riseSkill(Skill.SKILL_Magia);
			this.player.flags().TargetNpc = 0;
			
		    // Bonificación para druidas.
		    if (player.clazz() == Clazz.Druid 
		    		&& player.userInv().tieneAnilloEquipado() 
		    		&& player.userInv().getAnillo().ObjIndex == FLAUTAMAGICA 
		    		&& spell.Mimetiza) {
		    	this.player.stats().quitarMana((int) (spell.ManaRequerido * 0.5f));
		    } else {
		    	this.player.stats().quitarMana(spell.ManaRequerido);
		    }
			
			this.player.stats().quitarStamina(spell.StaRequerida);
			this.player.sendUpdateUserStats();
		}
	}

	public void sendInfoSpell() {
		Map map = this.server.getMap(this.player.pos().map);
		Spell spell = this.server.getSpell(this.player.flags().Hechizo);
		this.player.sayMagicWords(spell.PalabrasMagicas);
		this.player.sendWave(spell.WAV);
		if (this.player.flags().TargetUser > 0) {
			map.sendCreateFX(this.player.pos().x, this.player.pos().y, this.player.flags().TargetUser,
					spell.FXgrh, spell.loops);
		} else if (this.player.flags().TargetNpc > 0) {
			map.sendCreateFX(this.player.pos().x, this.player.pos().y, this.player.flags().TargetNpc,
					spell.FXgrh, spell.loops);
		}
		if (this.player.flags().TargetUser > 0) {
			if (this.player.getId() != this.player.flags().TargetUser) {
				Player target = this.server.playerById(this.player.flags().TargetUser);
				player.sendMessage(spell.HechiceroMsg + " " + (target.showName ? target.userName : "alguien"), FONTTYPE_FIGHT);
				target.sendMessage((player.showName ? player.getNick() : "Alguien") + " " + spell.TargetMsg, FONTTYPE_FIGHT);
			} else {
				this.player.sendMessage(spell.PropioMsg, FONTTYPE_FIGHT);
			}
		} else if (this.player.flags().TargetNpc > 0) {
			this.player.sendMessage(spell.HechiceroMsg + "la criatura.",
					FONTTYPE_FIGHT);
		}
	}

	public void loadSpells(IniFile ini) {
		for (int slot = 1; slot <= getCount(); slot++) {
			setSpell(slot, ini.getShort("HECHIZOS", "H" + slot));
		}
	}

}
