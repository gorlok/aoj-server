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

import static org.argentumonline.server.util.FontType.FONTTYPE_FIGHT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Clazz;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.Pos;
import org.argentumonline.server.Skill;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.map.Tile.Trigger;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcType;
import org.argentumonline.server.protocol.BlindResponse;
import org.argentumonline.server.protocol.ChangeSpellSlotResponse;
import org.argentumonline.server.protocol.CreateFXResponse;
import org.argentumonline.server.protocol.DumbResponse;
import org.argentumonline.server.protocol.ParalizeOKResponse;
import org.argentumonline.server.protocol.SetInvisibleResponse;
import org.argentumonline.server.protocol.UpdateManaResponse;
import org.argentumonline.server.protocol.UpdateStaResponse;
import org.argentumonline.server.user.User.DuelStatus;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Util;

/**
 * @author Gorlok
 */
public class UserSpells implements Constants {

	private static Logger log = LogManager.getLogger();

	short spells[] = new short[MAX_SPELLS];

	private User user;
	private GameServer server;

	public UserSpells(GameServer server, User user) {
		this.server = server;
		this.user = user;
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
			this.user.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) 0, "(Vacío)"));
			return;
		}
		Spell spell = this.server.getSpell(getSpell(slot));
		this.user.sendPacket(new ChangeSpellSlotResponse((byte) slot, (short) spell.getId(), spell.getName()));
	}

	public void sendSpellInfo(short slot) {
		// Comando INFS"
		if (slot < 1 || slot > MAX_SPELLS) {
			this.user.sendMessage("¡Primero selecciona el hechizo.!", FontType.FONTTYPE_INFO);
		} else {
			int spellIndex = this.spells[slot - 1];
			if (spellIndex > 0) {
				Spell spell = this.server.getSpell(spellIndex);
				this.user.sendMessage("||%%%%%%%%%%%% INFO DEL HECHIZO %%%%%%%%%%%%",
						FontType.FONTTYPE_INFO);
				this.user.sendMessage("||Nombre: " + spell.Nombre, FontType.FONTTYPE_INFO);
				this.user.sendMessage("||Descripcion: " + spell.Desc, FontType.FONTTYPE_INFO);
				this.user.sendMessage("||Skill requerido: " + spell.MinSkill
						+ " de magia.", FontType.FONTTYPE_INFO);
				this.user.sendMessage("||Mana necesario: " + spell.ManaRequerido,
						FontType.FONTTYPE_INFO);
				this.user.sendMessage("||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
						FontType.FONTTYPE_INFO);
			}
		}
	}

	public void castSpell(short slot) {
		if (slot < 1 || slot > this.spells.length) {
			return;
		}
		this.user.getFlags().Hechizo = this.spells[slot - 1];
		log.info("castSpell =====> " + this.user.getFlags().Hechizo);
	}

	public boolean hasSpell(int spellIndex) {
		for (short spell : this.spells) {
			if (spell == spellIndex) {
				return true;
			}
		}
		return false;
	}

	private int findFreeSlot() {
		for (int i = 0; i < this.spells.length; i++) {
			if (this.spells[i] == 0) {
				return i + 1;
			}
		}
		return 0;
	}

	public void addSpell(int slot) {
		int oid = this.user.getUserInv().getObject(slot).objid;
		ObjectInfo spellObj = this.server.getObjectInfoStorage().getInfoObjeto(oid);
		short spellIndex = spellObj.HechizoIndex;
		if (!hasSpell(spellIndex)) {
			// Buscamos un slot vacio
			int slotLibre = findFreeSlot();
			if (slotLibre == 0) {
				this.user.sendMessage("No tienes espacio para más hechizos.", FontType.FONTTYPE_INFO);
			} else {
				// Actualizamos la lista de hechizos,
				changeUserSpell(slotLibre, spellIndex);
				// y quitamos el item del inventario.
				this.user.getUserInv().quitarUserInvItem(slot, 1);
			}
		} else {
			this.user.sendMessage("Ya tienes ese hechizo.", FontType.FONTTYPE_INFO);
		}
	}

	public void moveSpell(int slot, short direction) {
		if (direction == -1) {
			// Move spell upward
			if (slot == 1) {
				this.user.sendMessage("No puedes mover el hechizo en esa direccion.",
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
				this.user.sendMessage("No puedes mover el hechizo en esa direccion.",
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
				if (this.user.getFlags().TargetUser > 0) {
					handleSpellTargetUser(spell);
				} else {
					this.user.sendMessage("Este hechizo actua solo sobre usuarios.",
							FontType.FONTTYPE_INFO);
				}
				break;
				
			case NPC:
				if (this.user.getFlags().TargetNpc > 0) {
					handleSpellTargetNPC(spell);
				} else {
					this.user.sendMessage("Este hechizo solo afecta a los npcs.",
							FontType.FONTTYPE_INFO);
				}
				break;
				
			case USER_AND_NPC:
				if (this.user.getFlags().TargetUser > 0) {
					handleSpellTargetUser(spell);
				} else if (this.user.getFlags().TargetNpc > 0) {
					handleSpellTargetNPC(spell);
				} else {
					this.user.sendMessage("Target inválido.", FontType.FONTTYPE_INFO);
				}
				break;
				
			case TERRAIN:
				handleSpellTargetTerrain(spell);
				break;
				
			default:
				break;
			}
		}
		this.user.getFlags().Trabajando = false;
		this.user.getFlags().Oculto = false;
	}

	private boolean statusSpellTargetUser() {
		// HechizoEstadoUsuario
		Spell spell = this.server.getSpell(this.user.getFlags().Hechizo);
		User targetUser = this.server.userById(this.user.getFlags().TargetUser);
		
		boolean result = false;
		if (spell.Invisibilidad) {
			if (!targetUser.isAlive()) {
				this.user.sendMessage("¡Está muerto!", FontType.FONTTYPE_INFO);
				return false;
			}
			
		    if (targetUser.m_saliendo) {
		    	if (this.user != targetUser) {
		    		this.user.sendMessage("¡El hechizo no tiene efecto!", FontType.FONTTYPE_INFO);
		    		return false;
		    	} else {
		    		this.user.sendMessage("¡No puedes volverte invisible mientras estás saliendo!", FontType.FONTTYPE_INFO);
		    		return false;
		    	}
		    }
		    
		    // No usar invi mapas InviSinEfecto
		    Map tuMap = server.getMap(targetUser.pos().map);
		    if (tuMap.isInviSinEfecto()) {
		    	this.user.sendMessage("¡La invisibilidad no funciona aquí!", FontType.FONTTYPE_INFO);
		    	return false;
		    }

		    // Para poder tirar invi a un pk en el ring
		    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !user.isCriminal()) {
		    		if (user.isRoyalArmy()) {
				    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (user.hasSafeLock()) {
				    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		                user.turnCriminal();
		    		}
		    	}
		    }
		    
		    // Los usuarios no pueden usar este hechizo con los GMs.
		    if (!user.getFlags().isGM() && targetUser.getFlags().isGM()) {
		    	return false;
		    }
		    
			targetUser.getFlags().Invisible = true;
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
			if (targetUser.isSailing() || user.isSailing()) {
				return false;
			}
		    // Los usuarios no pueden usar este hechizo con los GMs.
		    if (!user.getFlags().isGM() && targetUser.getFlags().isGM()) {
		    	return false;
		    }
		    if (user.getFlags().Mimetizado) {
		    	this.user.sendMessage("Ya te encuentras transformado. El hechizo no ha tenido efecto", FontType.FONTTYPE_INFO);
		    	return false;
		    }
		    if (targetUser.getFlags().AdminInvisible) {
		    	return false;
		    }
		    
		    // Guardo el char original en mimetizado
		    user.mimetizeChar().copyFrom(user.infoChar());
		    user.getFlags().Mimetizado = true;
	        // Le copio el char del target
		    user.infoChar().copyFrom(targetUser.infoChar());
		    user.sendCharacterChange();
		    sendInfoSpell();
			result = true;
		}

		if (spell.Envenena) {
			if (targetUser == user) {
		    	this.user.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.getFlags().Envenenado = true;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.CuraVeneno) {
			if (!targetUser.isAlive()) {
				this.user.sendMessage("¡Está muerto!", FontType.FONTTYPE_INFO);
				return false;
			}
			
		    // Los usuarios no pueden usar este hechizo con los GMs.
		    if (!user.getFlags().isGM() && targetUser.getFlags().isGM()) {
		    	return false;
		    }
			
		    // Para poder usar con un pk en el ring
		    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !user.isCriminal()) {
		    		if (user.isRoyalArmy()) {
				    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (user.hasSafeLock()) {
				    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		                user.getReputation().disminuyeNoblezaAumentaBandido(user, user.getReputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			
			targetUser.getFlags().Envenenado = false;
			sendInfoSpell();
			result = true;
		}

		if (spell.Maldicion) {
			if (targetUser == user) {
		    	this.user.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.getFlags().Maldicion = true;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.RemoverMaldicion) {
			targetUser.getFlags().Maldicion = false;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.Bendicion) {
			targetUser.getFlags().Bendicion = true;
			sendInfoSpell();
			result = true;
		}
		
		if (spell.isParaliza() || spell.isInmoviliza()) {
			if (targetUser == user) {
		    	this.user.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!targetUser.getFlags().Paralizado) {
				if (!this.user.puedeAtacar(targetUser)) {
					return false;
				}
				if (this.user != targetUser) {
					this.user.usuarioAtacadoPorUsuario(targetUser);
				}
				
	            if (targetUser.getUserInv().tieneAnilloEquipado() && targetUser.getUserInv().getAnillo().ObjIndex == SUPERANILLO) {
			    	targetUser.sendMessage("Tu anillo rechaza los efectos del hechizo.", FontType.FONTTYPE_INFO);
			    	user.sendMessage("¡El hechizo no tuvo efecto, ha sido rechazado!", FontType.FONTTYPE_INFO);
			    	return false;
	            }
				
				targetUser.getFlags().Paralizado = true;
				if (spell.inmoviliza) {
					targetUser.getFlags().Inmovilizado = true;
				}
				targetUser.getCounters().Paralisis = IntervaloParalizado;
				targetUser.sendPacket(new ParalizeOKResponse());
				sendInfoSpell();
				result = true;
			}
		}
		
		if (spell.RemoverParalisis) {
			if (targetUser.getFlags().Paralizado) {
				
			    // Para poder usar con un pk en el ring
			    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
			    	if (targetUser.isCriminal() && !user.isCriminal()) {
			    		if (user.isRoyalArmy()) {
					    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
					    	return false;
			    		}
			    		if (user.hasSafeLock()) {
					    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
					    	return false;
			    		} else {
			                user.turnCriminal();
			    		}
			    	}
			    }
				
				targetUser.getFlags().Paralizado = false;
				targetUser.getFlags().Inmovilizado = false;
				targetUser.sendPacket(new ParalizeOKResponse());
				sendInfoSpell();
				result = true;
			}
		}
		
		if (spell.RemoverEstupidez) {
			if (targetUser.isDumb()) {
			    // Para poder usar con un pk en el ring
			    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
			    	if (targetUser.isCriminal() && !user.isCriminal()) {
			    		if (user.isRoyalArmy()) {
					    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
					    	return false;
			    		}
			    		if (user.hasSafeLock()) {
					    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
					    	return false;
			    		} else {
			    			user.getReputation().disminuyeNoblezaAumentaBandido(user, user.getReputation().nobleRep * 0.5f, 10000);
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
		        if (user.getFlags().SeguroResu) {
		        	this.user.sendMessage("¡El espíritu no tiene intenciones de regresar al mundo de los vivos!", FontType.FONTTYPE_INFO);
		        	return false;
		        }
		    
		        // No usar resu en mapas con ResuSinEfecto
				Map map = this.server.getMap(targetUser.pos().map);
		        if (map.isResuSinEfecto()) {
		        	this.user.sendMessage("¡Revivir no está permitido aqui! Retirate de la Zona si deseas utilizar el Hechizo.", FontType.FONTTYPE_INFO);
		        	return false;
		        }
		        
		        // No podemos resucitar si nuestra barra de energía no está llena. (GD: 29/04/07)
		        if (!user.getStats().isFullStamina()) {
		        	this.user.sendMessage("No puedes resucitar si no tienes tu barra de energía llena.", FontType.FONTTYPE_INFO);
		        	return false;
		        }
		        
		        // revisamos si tiene vara, laud, o flauta
		        if (user.clazz() == Clazz.Mage) {
		        	if (!user.getUserInv().tieneArmaEquipada() || user.getUserInv().getArma().StaffPower == 0) {
			        	this.user.sendMessage("Necesitas un báculo para este hechizo.", FontType.FONTTYPE_INFO);
			        	return false;
		        	}
		        	if (user.getUserInv().getArma().StaffPower < spell.NeedStaff) {
			        	this.user.sendMessage("Necesitas un mejor báculo para este hechizo.", FontType.FONTTYPE_INFO);
			        	return false;
		        	}
		        } else if (user.clazz() == Clazz.Bard) {
		            if (!user.getUserInv().tieneAnilloEquipado() || user.getUserInv().getAnillo().ObjIndex != LAUDMAGICO) {
			        	this.user.sendMessage("Necesitas un instrumento mágico para devolver la vida.", FontType.FONTTYPE_INFO);
			        	return false;
		            }
		        } else if (user.clazz() == Clazz.Druid) {
		            if (!user.getUserInv().tieneAnilloEquipado() || user.getUserInv().getAnillo().ObjIndex != FLAUTAMAGICA) {
			        	this.user.sendMessage("Necesitas un instrumento mágico para devolver la vida.", FontType.FONTTYPE_INFO);
			        	return false;
		            }
		        }
		        
		        boolean eraCriminal = user.isCriminal();
		        
			    // Para poder usar con un pk en el ring
			    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
			    	if (targetUser.isCriminal() && !user.isCriminal()) {
			    		if (user.isRoyalArmy()) {
					    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
					    	return false;
			    		}
			    		if (user.hasSafeLock()) {
					    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
					    	return false;
			    		} else {
			    			user.turnCriminal();
			    		}
			    	}
			    }
		        
				if (!targetUser.isCriminal()) {
					if (this.user != targetUser) {
						this.user.reputation.incNoble(500);
						this.user.sendMessage(
								"¡Los Dioses te sonrien, has ganado 500 puntos de nobleza!.",
								FontType.FONTTYPE_INFO);
					}
				}
				
				if (!eraCriminal && user.isCriminal()) {
					user.refreshCharStatus();
				}

				targetUser.getStats().drinked = 0;
				targetUser.getFlags().Sed = true;
				targetUser.getStats().eaten = 0;
				targetUser.getFlags().Hambre = true;
				targetUser.refreshCharStatus();
				targetUser.revive();
				
				sendInfoSpell();
				user.getStats().mana = 0;
				user.getStats().stamina = 0;
		        
		        // Agregado para quitar la penalización de vida en el ring y cambio de ecuacion. (NicoNZ)
				if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		            // Solo saco vida si es User. no quiero que exploten GMs por ahi.
		            if (!user.getFlags().isGM()) {
		            	user.getStats().MinHP = (int) (user.getStats().MinHP * (1 - user.getStats().ELV * 0.015f));
		            }
				}
		        
				if (user.getStats().MinHP <= 0) {
					user.userDie();
					user.sendMessage("El esfuerzo de resucitar fue demasiado grande.", FontType.FONTTYPE_INFO);
					return false;
				} else {
					user.sendMessage("El esfuerzo de resucitar te ha debilitado.", FontType.FONTTYPE_INFO);
					result = true;
				}			
			}
		}
		
		if (spell.Ceguera) {
			if (targetUser == user) {
		    	this.user.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			targetUser.getFlags().Ceguera = true;
			targetUser.getCounters().Ceguera = IntervaloParalizado / 3;
			targetUser.sendPacket(new BlindResponse());
			sendInfoSpell();
			result = true;
		}
		
		if (spell.estupidez) {
			if (targetUser == user) {
		    	this.user.sendMessage("No puedes atacarte a ti mismo.", FontType.FONTTYPE_INFO);
		    	return false;
			}
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			
			if (!targetUser.getFlags().Estupidez) {
				targetUser.getFlags().Estupidez = true;
				targetUser.getCounters().Ceguera = IntervaloParalizado;
			}
			targetUser.sendPacket(new DumbResponse());
			sendInfoSpell();
			result = true;
		}
		
		return result;
	}

	private boolean statusSpellTargetNpc(Npc npc, Spell spell) {
		// hechizoEstadoNPC
		if (spell.Invisibilidad) {
			sendInfoSpell();
			npc.makeInvisible();
			return true;
		}
		if (spell.Envenena) {
			if (!npc.isAttackable()) {
				this.user.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
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
				this.user.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
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
				npc.paralize();
				npc.unimmobilize();
				return true;
			}
			this.user.sendMessage("El npc es inmune a este hechizo.", FONTTYPE_FIGHT);
		}
		if (spell.RemoverParalisis) {
			if (npc.isParalized() || npc.isInmovilized()) {
				if (npc.getPetUserOwner() == this.user) {
					sendInfoSpell();
					npc.unparalize();
					npc.unimmobilize();// FIXME revisar...
					return true;
				} else if (npc.npcType() == NpcType.NPCTYPE_ROYAL_GUARD) {
					if (user.isRoyalArmy()) {
						sendInfoSpell();
						npc.unparalize();
						npc.unimmobilize();// FIXME revisar...
						return true;
					} else {
						user.sendMessage("Solo puedes Remover la Parálisis de los Guardias si perteneces a su facción.", FontType.FONTTYPE_INFO);
						return false;
					}
				} else if (npc.npcType() == NpcType.NPCTYPE_GUARDAS_CHAOS) {
					if (user.isDarkLegion()) {
						sendInfoSpell();
						npc.unparalize();
						npc.unimmobilize();// FIXME revisar...
						return true;
					} else {
						user.sendMessage("Solo puedes Remover la Parálisis de los Guardias si perteneces a su facción.", FontType.FONTTYPE_INFO);
						return false;
					}
				}
				user.sendMessage("Solo puedes Remover la Parálisis de los NPCs que te consideren su amo", FontType.FONTTYPE_INFO);
			}
			this.user.sendMessage("El npc no está paralizado.", FONTTYPE_FIGHT);
		}
		return false;
	}

	private boolean propertySpellTargetNpc(Npc npc, Spell spell) {
		// HechizoPropNPC
		// Salud
		if (spell.SubeHP == 1) {
			// Curar la salud
			int cura = Util.random(spell.MinHP, spell.MaxHP);
			cura += Util.percentage(cura, 3 * this.user.getStats().ELV);
			sendInfoSpell();
			npc.stats.addHP(cura);
			this.user.sendMessage("Has curado " + cura
					+ " puntos de salud a la criatura.", FONTTYPE_FIGHT);
			return true;
			
		} else if (spell.SubeHP == 2) {
			// Dañar la salud
			if (!npc.isAttackable()) {
				//this.user.sendMessage("No puedes atacar a ese npc.", FontType.FONTTYPE_INFO);
				return false;
			}
			
			this.user.npcAtacado(npc);
			int damage = Util.random(spell.MinHP, spell.MaxHP);
			damage += Util.percentage(damage, 3 * this.user.getStats().ELV);
			
			if (spell.StaffAffected) {
				if (user.clazz() == Clazz.Mage) {
					if (user.getUserInv().tieneArmaEquipada()) {
		                damage = (damage * (user.getUserInv().getArma().StaffDamageBonus + 70)) / 100;
                        // Aumenta daño segun el staff-
                        // Daño = (Daño* (70 + BonifBáculo)) / 100
					} else {
						damage = (int) (damage * 0.7f); // Baja daño a 70% del original
					}
				}
			}
			if (user.getUserInv().tieneAnilloEquipado() && 
					(user.getUserInv().getAnillo().ObjIndex == LAUDMAGICO || user.getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA)) {
				damage = (int) (damage * 1.04f); // laud magico de los bardos
			}

			sendInfoSpell();
			
			if (npc.getSonidoAtaqueExitoso() > 0) {
				this.user.sendWave(npc.getSonidoAtaqueExitoso());
			}
			
		    // Quizas tenga defenza magica el NPC. Pablo (ToxicWaste)
		    damage = damage - npc.stats().defensaMagica;
		    if (damage < 0) {
		    	damage = 0;
		    }
			
			npc.stats.removeHP(damage);
			this.user.sendMessage("Le has causado " + damage
					+ " puntos de daño a la criatura!", FONTTYPE_FIGHT);
			npc.calcularDarExp(this.user, damage);
			if (npc.stats.MinHP < 1) {
				npc.muereNpc(this.user);
			}
			
			/*
			if (npc.getPetUserOwner() != null
					&& (this.user.flags().Seguro || this.user.userFaction().ArmadaReal)) {
				if (!npc.getPetUserOwner().isCriminal()) {
					if (this.user.userFaction().ArmadaReal) {
						this.user.sendMessage(
								"Los soldados del Ejercito Real tienen prohibido atacar a ciudadanos y sus mascotas.",
								FontType.FONTTYPE_WARNING);
					} else {
						this.user.sendMessage("Tienes el seguro activado. Presiona la tecla *.",
								FontType.FONTTYPE_WARNING);
					}
					return false;
				}
			}
			// FIX by AGUSH: we check if user can attack to guardians
			if (npc.isNpcGuard() && this.user.hasSafeLock()) {
				this.user.sendMessage("Tienes el seguro activado. Presiona la tecla *.",
						FontType.FONTTYPE_INFO);
				return false;
			}
			*/

			return true;
		}
		return false;
	}

	private boolean propertySpellTargetUser() {
		// HechizoPropUsuario
		Spell spell = this.server.getSpell(this.user.getFlags().Hechizo);
		User targetUser = this.server.userById(this.user.getFlags().TargetUser);
		
		if (!targetUser.isAlive()) {
	    	this.user.sendMessage("No podés lanzar ese hechizo a un muerto.", FontType.FONTTYPE_INFO);
	    	return false;
		}

		// Hambre
		if (spell.SubeHam == 1) {
			// Aumentar hambre
			sendInfoSpell();
			int damage = Util.random(spell.MinHam, spell.MaxHam);
			targetUser.stats.aumentarHambre(damage);
			if (this.user != targetUser) {
				this.user.sendMessage("Le has restaurado " + damage
						+ " puntos de hambre a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha restaurado "
						+ damage + " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has restaurado " + damage
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			targetUser.sendUpdateHungerAndThirst();
			return true;

		} else if (spell.SubeHam == 2) {
			// Quitar hambre
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			} else {
				// no puede darse hambre a si mismo
				return false;
			}
			sendInfoSpell();
			int damage = Util.random(spell.MinHam, spell.MaxHam);
			targetUser.stats.quitarHambre(damage);
			if (this.user != targetUser) {
				this.user.sendMessage("Le has provocado " + damage
						+ " puntos de hambre a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha provocado " + damage
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has provocado " + damage
						+ " puntos de hambre.", FONTTYPE_FIGHT);
			}
			if (targetUser.stats.eaten < 1) {
				targetUser.stats.eaten = 0;
				targetUser.getFlags().Hambre = true;
			}
			targetUser.sendUpdateHungerAndThirst();
			return true;
		}

		// Sed
		if (spell.SubeSed == 1) {
			sendInfoSpell();
			int damage = Util.random(spell.MinSed, spell.MaxSed);
			targetUser.stats.aumentarSed(damage);
			targetUser.sendUpdateHungerAndThirst();
			if (this.user != targetUser) {
				this.user.sendMessage("Le has restaurado " + damage
						+ " puntos de sed a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha restaurado "
						+ damage + " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has restaurado " + damage
						+ " puntos de sed.", FONTTYPE_FIGHT);
			}
			return true;

		} else if (spell.SubeSed == 2) {
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			} else {
				// no puede darse sed a si mismo
				return false;
			}
			sendInfoSpell();
			int damage = Util.random(spell.MinSed, spell.MaxSed);
			targetUser.stats.quitarSed(damage);
			if (this.user != targetUser) {
				this.user.sendMessage("Le has provocado " + damage
						+ " puntos de sed a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha provocado " + damage
						+ " puntos de sed.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage(
						"Te has provocado " + damage + " puntos de sed.",
						FONTTYPE_FIGHT);
			}
			if (targetUser.stats.drinked < 1) {
				targetUser.stats.drinked = 0;
				targetUser.getFlags().Sed = true;
			}
			targetUser.sendUpdateHungerAndThirst();
			return true;
		}

		// <-------- Agilidad ---------->
		if (spell.SubeAgilidad == 1) {
		    // Para poder usar con un pk en el ring
		    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !user.isCriminal()) {
		    		if (user.isRoyalArmy()) {
				    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (user.hasSafeLock()) {
				    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		    			user.getReputation().disminuyeNoblezaAumentaBandido(user, user.getReputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			sendInfoSpell();
			int damage = Util.random(spell.MinAgilidad, spell.MaxAgilidad);
			targetUser.getFlags().DuracionEfecto = 1200;
			targetUser.getStats().attr().modifyByEffect(Attribute.AGILITY, damage);
			targetUser.getFlags().TomoPocion = true;
			return true;

		} else if (spell.SubeAgilidad == 2) {
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			targetUser.getFlags().TomoPocion = true;
			int damage = Util.random(spell.MinAgilidad, spell.MaxAgilidad);
			targetUser.getFlags().DuracionEfecto = 700;
			targetUser.getStats().attr().modifyByEffect(Attribute.AGILITY, -damage);
			return true;
		}
		
		// <-------- Fuerza ---------->
		if (spell.SubeFuerza == 1) {
		    // Para poder usar con un pk en el ring
		    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !user.isCriminal()) {
		    		if (user.isRoyalArmy()) {
				    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (user.hasSafeLock()) {
				    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		    			user.getReputation().disminuyeNoblezaAumentaBandido(user, user.getReputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			sendInfoSpell();
			int damage = Util.random(spell.MinFuerza, spell.MaxFuerza);
			targetUser.getFlags().DuracionEfecto = 1200;
			targetUser.getStats().attr().modifyByEffect(Attribute.STRENGTH, damage);
			targetUser.getFlags().TomoPocion = true;
			return true;

		} else if (spell.SubeFuerza == 2) {
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			targetUser.getFlags().TomoPocion = true;
			int damage = Util.random(spell.MinFuerza, spell.MaxFuerza);
			targetUser.getFlags().DuracionEfecto = 700;
			targetUser.getStats().attr().modifyByEffect(Attribute.STRENGTH, -damage);
			return true;
		}

		// Salud
		if (spell.SubeHP == 1) {
			if (!targetUser.isAlive()) {
				this.user.sendMessage("¡Está muerto!", FONTTYPE_FIGHT);
				return false;
			}
		    // Para poder usar con un pk en el ring
		    if (user.duelStatus(targetUser) != DuelStatus.DUEL_ALLOWED) {
		    	if (targetUser.isCriminal() && !user.isCriminal()) {
		    		if (user.isRoyalArmy()) {
				    	this.user.sendMessage("Los miembros de la Armada Real no pueden ayudar a los criminales", FontType.FONTTYPE_INFO);
				    	return false;
		    		}
		    		if (user.hasSafeLock()) {
				    	this.user.sendMessage("Para ayudar criminales debes sacarte el seguro, y te volverás criminal como ellos", FontType.FONTTYPE_INFO);
				    	return false;
		    		} else {
		    			user.getReputation().disminuyeNoblezaAumentaBandido(user, user.getReputation().nobleRep * 0.5f, 10000);
		    		}
		    	}
		    }
			int damage = Util.random(spell.MinHP, spell.MaxHP);
			damage += Util.percentage(damage, 3 * this.user.getStats().ELV);
			sendInfoSpell();
			targetUser.stats.addHP(damage);
			targetUser.sendUpdateHP((short) targetUser.getStats().MinHP);
			if (this.user != targetUser) {
				this.user.sendMessage("Le has restaurado " + damage
						+ " puntos de vida a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha restaurado "
						+ damage + " puntos de vida.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has restaurado " + damage
						+ " puntos de vida.", FONTTYPE_FIGHT);
			}
			return true;
			
		} else if (spell.SubeHP == 2) {
			if (this.user == targetUser) {
				this.user.sendMessage("No puedes atacarte a ti mismo.",
						FONTTYPE_FIGHT);
				return false;
			}
			int damage = Util.random(spell.MinHP, spell.MaxHP);
			damage += Util.percentage(damage, 3 * this.user.getStats().ELV);
			
			// Potenciador de ataque Báculo
			if (spell.StaffAffected) {
				if (user.clazz() == Clazz.Mage) {
					if (user.getUserInv().tieneArmaEquipada() && user.getUserInv().getArma().StaffDamageBonus > 0) {
						damage = (damage * (user.getUserInv().getArma().StaffDamageBonus + 70)) / 100; 
					} else {
						damage = (int) (damage * 0.7f); // Baja daño a 70% del original
					}
				}
			}
			// Potenciador de ataque Laúd o Flauta
			if (user.getUserInv().tieneAnilloEquipado() && (user.getUserInv().getAnillo().ObjIndex == LAUDMAGICO || user.getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA)) {
				damage = (int) (damage * 1.04f);  // laud magico de los bardos
			}
			
			// Defensa mágica Sombreros
			if (targetUser.getUserInv().tieneCascoEquipado()) {
				damage = damage - Util.random(
						targetUser.getUserInv().getCasco().DefensaMagicaMin,
						targetUser.getUserInv().getCasco().DefensaMagicaMax);  // sombreros antimagia
			}
			// Defensa mágica Anillos
			if (targetUser.getUserInv().tieneAnilloEquipado()) {
				damage = damage - Util.random(
						targetUser.getUserInv().getAnillo().DefensaMagicaMin,
						targetUser.getUserInv().getAnillo().DefensaMagicaMax);  // anillos antimagia
			}
			if (damage < 0) {
				damage = 0;
			}
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			targetUser.stats.removeHP(damage);
			targetUser.sendUpdateHP((short) targetUser.getStats().MinHP);
			this.user.sendMessage("Le has quitado " + damage + " puntos de vida a "
					+ targetUser.getUserName(), FONTTYPE_FIGHT);
			targetUser.sendMessage(this.user.getUserName() + " te ha quitado " + damage
					+ " puntos de vida.", FONTTYPE_FIGHT);
			// Muere
			if (targetUser.stats.MinHP < 1) {
				this.user.contarMuerte(targetUser);
				targetUser.stats.MinHP = 0;
				this.user.actStats(targetUser);
				targetUser.userDie();
			}
			return true;
		}

		// Mana
		if (spell.SubeMana == 1) {
			sendInfoSpell();
			int damage = Util.random(spell.MinMana, spell.MaxMana);
			targetUser.stats.aumentarMana(damage);
			targetUser.sendPacket(new UpdateManaResponse((short) targetUser.getStats().mana));
			if (this.user != targetUser) {
				this.user.sendMessage("Le has restaurado " + damage
						+ " puntos de mana a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha restaurado "
						+ damage + " puntos de mana.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has restaurado " + damage
						+ " puntos de mana.", FONTTYPE_FIGHT);
			}
			return true;

		} else if (spell.SubeMana == 2) {
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			int damage = Util.random(spell.MinMana, spell.MaxMana);
			targetUser.stats.quitarMana(damage);
			targetUser.sendPacket(new UpdateManaResponse((short) targetUser.getStats().mana));
			if (this.user != targetUser) {
				this.user.sendMessage("Le has quitado " + damage
						+ " puntos de mana a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha quitado " + damage
						+ " puntos de mana.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has quitado " + damage
						+ " puntos de mana.", FONTTYPE_FIGHT);
			}
			return true;
		}

		// Stamina
		if (spell.SubeSta == 1) {
			sendInfoSpell();
			int damage = Util.random(spell.MinSta, spell.MaxSta);
			targetUser.stats.aumentarStamina(damage);
			targetUser.sendPacket(new UpdateStaResponse((short) targetUser.getStats().stamina));
			if (this.user != targetUser) {
				this.user.sendMessage("Le has restaurado " + damage
						+ " puntos de energia a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha restaurado "
						+ damage + " puntos de energia.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has restaurado " + damage
						+ " puntos de energia.", FONTTYPE_FIGHT);
			}
			return true;

		} else if (spell.SubeSta == 2) {
			if (!this.user.puedeAtacar(targetUser)) {
				return false;
			}
			if (this.user != targetUser) {
				this.user.usuarioAtacadoPorUsuario(targetUser);
			}
			sendInfoSpell();
			int damage = Util.random(spell.MinSta, spell.MaxSta);
			targetUser.stats.quitarStamina(damage);
			targetUser.sendPacket(new UpdateStaResponse((short) targetUser.getStats().stamina));
			if (this.user != targetUser) {
				this.user.sendMessage("Le has quitado " + damage
						+ " puntos de energia a " + targetUser.getUserName(),
						FONTTYPE_FIGHT);
				targetUser.sendMessage(this.user.getUserName() + " te ha quitado " + damage
						+ " puntos de energia.", FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage("Te has quitado " + damage
						+ " puntos de energia.", FONTTYPE_FIGHT);
			}
			return true;
		}
		return false;
	}

	private boolean canCastSpell(Spell spell) {
		if (!this.user.checkAlive("No puedes lanzar hechizos porque estas muerto.")) {
			return false;
		}
		
	    if (spell.NeedStaff > 0) {
	        if (this.user.clazz() == Clazz.Mage) {
	            if (this.user.getUserInv().tieneArmaEquipada()) {
	            	if (this.user.getUserInv().getArma().StaffPower < spell.NeedStaff) {
	            		this.user.sendMessage("No posees un báculo lo suficientemente poderoso para que puedas lanzar el conjuro.", FontType.FONTTYPE_INFO);
	            		return false;
	            	}
	            } else {
	            	this.user.sendMessage("No puedes lanzar este conjuro sin la ayuda de un báculo.", FontType.FONTTYPE_INFO);
	                return false;
	            }
	        }
	    }
		
	    if (this.user.skills().get(Skill.SKILL_Magia) < spell.MinSkill) {
	    	this.user.sendMessage(
	    			"No tienes suficientes puntos de magia para lanzar este hechizo.",
	    			FontType.FONTTYPE_INFO);
	    	return false;
	    }
	    
	    if (this.user.getStats().isTooTired() || this.user.getStats().stamina < spell.StaRequerida) {
	    	if (this.user.gender() == UserGender.GENERO_MAN) {
	    		this.user.sendMessage("Estas muy cansado para lanzar este hechizo.", FontType.FONTTYPE_INFO);
	    	} else {
	    		this.user.sendMessage("Estas muy cansada para lanzar este hechizo.", FontType.FONTTYPE_INFO);
	    	}
	    	return false;
	    }
	    

	    float druidManaBonus;
	    if (this.user.clazz() == Clazz.Druid) {
	        if (this.user.getUserInv().tieneAnilloEquipado() 
	        		&& this.user.getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA) {
	            
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
	    
	    if (this.user.getStats().mana < spell.ManaRequerido * druidManaBonus) {
	    	this.user.sendMessage("No tienes suficiente mana.", FontType.FONTTYPE_INFO);
	    	return false;
	    }
	    
		MapPos targetPos = MapPos.mxy(
				this.user.getFlags().TargetMap,
				this.user.getFlags().TargetX,
				this.user.getFlags().TargetY);
		if (this.user.pos().distance(targetPos) > MAX_DISTANCIA_MAGIA) {
			this.user.sendMessage("Estás demasiado lejos.", FontType.FONTTYPE_INFO);
			return false;
		}

		return true;
	}

	private boolean summonSpell() {
		if (this.user.getUserPets().isFullPets()) {
			//this.user.sendMessage("No puedes invocar más mascotas!", FontType.FONTTYPE_INFO);
			return false;
		}

		// No permitimos se invoquen criaturas en zonas seguras
		Map map = this.server.getMap(this.user.pos().map);
		if (map.isSafeMap() || map.getTrigger(user.pos().x, user.pos().y) == Trigger.TRIGGER_ZONA_SEGURA) {
			this.user.sendMessage("En zona segura no puedes invocar criaturas.", FontType.FONTTYPE_INFO);
			return false;
		}
				
		boolean success = false;
		MapPos targetPos = MapPos.mxy(
								this.user.getFlags().TargetMap,
								this.user.getFlags().TargetX, 
								this.user.getFlags().TargetY);
		Spell hechizo = this.server.getSpell(this.user.getFlags().Hechizo);
		
		for (int i = 0; i < hechizo.Cant; i++) {
			// Considero que hubo success si se pudo invocar alguna criatura.
			success = success || (this.user.crateSummonedPet(hechizo.NumNpc, targetPos) != null);
		}
		sendInfoSpell();
		return success;
	}

	private void handleSpellTargetTerrain(Spell spell) {
		if (!user.isInCombatMode()) {
			this.user.sendMessage("Debes estar en modo de combate para lanzar este hechizo.", FontType.FONTTYPE_INFO);
			return;
		}
		
		boolean success = false;
		switch (spell.spellAction) {
		case SUMMON:
			success = summonSpell();
			break;
			
		case STATUS:
			success = statusSpellTargetTerrain();
			break;
			
		default:
			break;
		}
		
		if (success) {
			this.user.riseSkill(Skill.SKILL_Magia);
			
			if (user.clazz() == Clazz.Druid && user.getUserInv().tieneAnilloEquipado() 
					&& user.getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA) {
				this.user.getStats().quitarMana((int) (spell.ManaRequerido * 0.7f));
			} else {
				this.user.getStats().quitarMana(spell.ManaRequerido);
			}
			this.user.getStats().quitarStamina(spell.StaRequerida);
			this.user.sendUpdateUserStats();
		}
	}

	private boolean statusSpellTargetTerrain() {
		// HechizoTerrenoEstado
		MapPos targetPos = MapPos.mxy(
			this.user.getFlags().TargetMap,
			this.user.getFlags().TargetX, 
			this.user.getFlags().TargetY);
		Map map = this.server.getMap(targetPos.map);
		Spell spell = this.server.getSpell(this.user.getFlags().Hechizo);
		
		if (spell.RemueveInvisibilidadParcial) {
			for (byte x = (byte) (targetPos.x - 8); x <= targetPos.x + 8; x++) {
				for (byte y = (byte) (targetPos.y - 8); y <= targetPos.y + 8; y++) {
					if (Pos.isValid(x, y)) {
						if (map.hasUser(x, y)) {
							User tmpUser = map.getUser((byte)x, (byte)y);
							if (tmpUser.isInvisible() && !tmpUser.getFlags().AdminInvisible) {
								map.sendToArea(x, y, new CreateFXResponse(tmpUser.getId(), spell.FXgrh, (short)spell.loops));
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
		boolean success = false;
		User target = this.server.userById(this.user.getFlags().TargetUser);
		if (target == null || target.getFlags().isGM()) {
			return;
		}
		switch (spell.spellAction) {
		case STATUS: // Afectan estados (por ejem : Envenenamiento)
			success = statusSpellTargetUser();
			break;
			
		case PROPERTIES: // Afectan HP,MANA,STAMINA,ETC
			success = propertySpellTargetUser();
			break;
			
		default:
			break;
		}
		
		if (success) {
			this.user.riseSkill(Skill.SKILL_Magia);
			
		    // Agregado para que los druidas, al tener equipada la flauta magica, el coste de mana de mimetismo es de 50% menos.
		    if (user.clazz() == Clazz.Druid 
		    		&& user.getUserInv().tieneAnilloEquipado() 
		    		&& user.getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA
		    		&& spell.Mimetiza){
        		this.user.getStats().quitarMana((int) (spell.ManaRequerido * 0.5f));
		    } else {
        		this.user.getStats().quitarMana(spell.ManaRequerido);
		    }
			
			this.user.getStats().quitarStamina(spell.StaRequerida);
			this.user.sendUpdateUserStats();
			this.user.getFlags().TargetUser = 0;
		}
	}

	private void handleSpellTargetNPC(Spell spell) {
		// HandleHechizoNPC
		boolean success = false;
		Npc targetNPC = this.server.npcById(this.user.getFlags().TargetNpc);
		switch (spell.spellAction) {
		case STATUS: // Afectan estados (por ejem : Envenenamiento)
			success = statusSpellTargetNpc(targetNPC, spell);
			break;
			
		case PROPERTIES: // Afectan HP,MANA,STAMINA,ETC
			success = propertySpellTargetNpc(targetNPC, spell);
			break;
			
		default:
			break;
		}
		if (success) {
			this.user.riseSkill(Skill.SKILL_Magia);
			this.user.getFlags().TargetNpc = 0;
			
		    // Bonificación para druidas.
		    if (user.clazz() == Clazz.Druid 
		    		&& user.getUserInv().tieneAnilloEquipado() 
		    		&& user.getUserInv().getAnillo().ObjIndex == FLAUTAMAGICA 
		    		&& spell.Mimetiza) {
		    	this.user.getStats().quitarMana((int) (spell.ManaRequerido * 0.5f));
		    } else {
		    	this.user.getStats().quitarMana(spell.ManaRequerido);
		    }
			
			this.user.getStats().quitarStamina(spell.StaRequerida);
			this.user.sendUpdateUserStats();
		}
	}

	public void sendInfoSpell() {
		Map map = this.server.getMap(this.user.pos().map);
		Spell spell = this.server.getSpell(this.user.getFlags().Hechizo);
		this.user.sayMagicWords(spell.PalabrasMagicas);
		this.user.sendWave(spell.WAV);
		if (this.user.getFlags().TargetUser > 0) {
			map.sendCreateFX(this.user.pos().x, this.user.pos().y, this.user.getFlags().TargetUser,
					spell.FXgrh, spell.loops);
		} else if (this.user.getFlags().TargetNpc > 0) {
			map.sendCreateFX(this.user.pos().x, this.user.pos().y, this.user.getFlags().TargetNpc,
					spell.FXgrh, spell.loops);
		}
		if (this.user.getFlags().TargetUser > 0) {
			if (this.user.getId() != this.user.getFlags().TargetUser) {
				User target = this.server.userById(this.user.getFlags().TargetUser);
				user.sendMessage(spell.HechiceroMsg + " " + (target.showName ? target.getUserName() : "alguien"), FONTTYPE_FIGHT);
				target.sendMessage((user.showName ? user.getUserName() : "Alguien") + " " + spell.TargetMsg, FONTTYPE_FIGHT);
			} else {
				this.user.sendMessage(spell.PropioMsg, FONTTYPE_FIGHT);
			}
		} else if (this.user.getFlags().TargetNpc > 0) {
			this.user.sendMessage(spell.HechiceroMsg + "la criatura.",
					FONTTYPE_FIGHT);
		}
	}

	public void loadSpells(IniFile ini) {
		for (int slot = 1; slot <= getCount(); slot++) {
			setSpell(slot, ini.getShort("HECHIZOS", "H" + slot));
		}
	}

}
