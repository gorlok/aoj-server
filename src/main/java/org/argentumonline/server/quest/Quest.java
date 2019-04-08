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
package org.argentumonline.server.quest;

import static org.argentumonline.server.util.Color.COLOR_BLANCO;

import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapPos;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.npc.NpcType;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Util;

/**
 *
 * @author gorlok
 */
public class Quest implements Constants {
	private static Logger log = LogManager.getLogger();

	short nroQuest;

	boolean DaObj = false;
	boolean DaExp = false;
	boolean DaOro = false;

	short Obj = 0;
	int Exp = 0;
	int Oro = 0;

	short CriaturaIndex = 0;

	final static int CANT_NPCS = 5;
	MapPos Coordenadas[] = new MapPos[CANT_NPCS];
	String Pista[] = new String[CANT_NPCS];

	short Objetivo = 0;
	short NPCs = 0;
	short Usuarios = 0;
	short Criminales = 0;
	short Ciudadanos = 0;
	short AmigoNpc = 0;

	/** Matar npcs */
	public final static short OBJETIVO_MATAR_NPCS = 1;
	/** Matar usuarios (Neutral), crimis (Armada) o ciudadanos (Caos). */
	public final static short OBJETIVO_MATAR_USUARIOS = 2;
	/** Encontrar un npc */
	public final static short OBJETIVO_ENCONTRAR_NPC = 3;
	/** Matar a un npc específico */
	public final static short OBJETIVO_MATAR_UN_NPC = 4;

	GameServer server;

	/** Creates a new instance of Quest */
	public Quest(GameServer server, short nroQuest) {
		this.server = server;
		this.nroQuest = nroQuest;
	}

	public short getNroQuest() {
		return this.nroQuest;
	}

	@Override
	public String toString() {
		return "Quest(nro=" + this.nroQuest + ",objetivo=" + this.Objetivo + ")";
	}

	public void load(IniFile ini) {
		String seccion = "Quest" + this.nroQuest;
		for (short j = 0; j < CANT_NPCS; j++) {
			String s = ini.getString(seccion, "Coordenadas" + (j + 1));
			if (s.length() == 0) {
				break;
			}
			try {
				StringTokenizer st = new StringTokenizer(s, "-");
				short mapa = Short.parseShort(st.nextToken());
				short x = Short.parseShort(st.nextToken());
				short y = Short.parseShort(st.nextToken());
				this.Coordenadas[j] = MapPos.mxy(mapa, x, y);
				this.Pista[j] = s;
			} catch (Exception e) {
				log.fatal("Error en cargarQuests(): quest=" + this.nroQuest + " npc=#" + (j + 1), e);
			}
		}
		this.CriaturaIndex = ini.getShort(seccion, "CriaturaIndex");
		this.DaExp = (ini.getShort(seccion, "DaExp") == 1);
		this.DaObj = (ini.getShort(seccion, "DaObj") == 1);
		this.DaOro = (ini.getShort(seccion, "DaOro") == 1);
		this.Obj = ini.getShort(seccion, "Obj");
		this.Exp = ini.getInt(seccion, "Exp");
		this.Oro = ini.getInt(seccion, "Oro");
		this.Objetivo = ini.getShort(seccion, "Objetivo");
		this.NPCs = ini.getShort(seccion, "Npcs");
		this.Usuarios = ini.getShort(seccion, "Usuarios");
		this.Ciudadanos = ini.getShort(seccion, "Ciudadanos");
		this.Criminales = ini.getShort(seccion, "Criminales");
		this.AmigoNpc = ini.getShort(seccion, "AmigoNpc");
	}

	public void hacerQuest(User user, Npc npc) {
		try {
			if (user.isNewbie()) {
				user.sendTalk(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
				return;
			}
			if (user.quest().m_enQuest) {
				user.sendTalk(COLOR_BLANCO,
						"Debes terminar primero la quest que estás realizando para empezar otra!", npc.getId());
				return;
			}
			if (user.quest().m_nroQuest >= this.server.questCount()) {
				user.sendTalk(COLOR_BLANCO, "Ya has realizados todas las quest!", npc.getId());
				return;
			}
			user.nextQuest();
			Quest quest = user.quest().getQuest();
			int azar = 0;
			switch (quest.Objetivo) {
			case OBJETIVO_MATAR_NPCS:
				user.sendTalk(COLOR_BLANCO, "Debes matar " + this.NPCs + " npcs para recibir tu recompensa!",
						npc.getId());
				break;
			case OBJETIVO_MATAR_USUARIOS:
				if (!user.userFaction().ArmadaReal && !user.userFaction().FuerzasCaos) {
					user.sendTalk(COLOR_BLANCO,
							"Debes matar " + this.Usuarios + " usuarios para recibir tu recompensa!", npc.getId());
				} else if (user.userFaction().ArmadaReal) {
					user.sendTalk(COLOR_BLANCO,
							"Debes matar " + this.Criminales + " criminales para recibir tu recompensa!", npc.getId());
				} else if (user.userFaction().FuerzasCaos) {
					user.sendTalk(COLOR_BLANCO,
							"Debes matar " + this.Ciudadanos + " ciudadanos para recibir tu recompensa!", npc.getId());
				}
				break;
			case OBJETIVO_ENCONTRAR_NPC:
				azar = Util.random(1, CANT_NPCS);
				if (this.Coordenadas[azar - 1] == null) {
					return;
				}
				user.sendTalk(COLOR_BLANCO,
						"Debes encontrar a mi amigo npc para recibir tu recompensa! Pistas: Se puede encontrar en lugares característicos del juego, pero apresúrate!" +
						" Si alguien que está haciendo este mismo tipo de quest, y lo encuentra antes, puedes perderlo. En tal caso deberás volver y hacerme clic, y poner /MERINDO",
						npc.getId());
				// Npc amigoNpc =
				Npc.spawnNpc(this.AmigoNpc, this.Coordenadas[azar - 1], true, false);
				break;
			case OBJETIVO_MATAR_UN_NPC:
				azar = Util.random(1, CANT_NPCS);
				if (this.Coordenadas[azar - 1] == null) {
					return;
				}
				user.sendTalk(COLOR_BLANCO, "Debes matar al npc que se encuentra en las coordenadas "
						+ this.Pista[azar - 1]
						+ " para recibir tu recompensa! PD: Si no te apresuras, y lo mata otro usuario, perderás esta quest. En tal caso deberás volver y hacerme clic, y poner /MERINDO",
						npc.getId());
				// Npc criaturaNpc =
				Npc.spawnNpc(this.CriaturaIndex, this.Coordenadas[azar - 1], true, false);
				break;
			}
		} catch (Exception e) {
			log.fatal(user.getUserName() + " Error en HacerQuest!", e);
		}
	}

	public void recibirRecompensaQuest(User user) {
		try {
			Npc npc = this.server.npcById(user.getFlags().TargetNpc);
			if (user.isNewbie()) {
				user.talk(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
				return;
			}
			if (user.quest().m_nroQuest <= 0) {
				user.talk(COLOR_BLANCO, "No has empezado ninguna Quest!", npc.getId());
				return;
			}
			if (user.quest().m_enQuest) {
				if (user.quest().m_recompensa == user.quest().m_nroQuest) {
					user.talk(COLOR_BLANCO, "Ya has recibido tu recompensa por esta Quest!", npc.getId());
					return;
				}
			}
			Quest quest = user.quest().getQuest();
			switch (quest.Objetivo) {
			case OBJETIVO_MATAR_NPCS:
				if (user.getStats().NPCsMuertos >= quest.NPCs) {
					if (quest.DaExp) {
						user.getStats().addExp(quest.Exp);
						user.checkUserLevel();
						user.talk(COLOR_BLANCO,
								"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!", npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
					if (quest.DaOro) {
						user.getStats().addGold(quest.Oro);
						user.talk(COLOR_BLANCO, "Como recompensa has recibido " + quest.Oro + " monedas de oro!",
								npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
					if (quest.DaObj) {
						if (user.getUserInv().agregarItem(quest.Obj, 1) < 1) {
							Map mapa = this.server.getMap(user.pos().map);
							mapa.dropItemOnFloor(user.pos().x, user.pos().y, new InventoryObject(quest.Obj, 1));
						}
						user.talk(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
				} else {
					user.talk(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
				}
				break;
			case OBJETIVO_MATAR_USUARIOS:
				if (!user.userFaction().ArmadaReal && !user.userFaction().FuerzasCaos) {
					if (user.userFaction().citizensKilled
							+ user.userFaction().criminalsKilled >= quest.Usuarios) {
						if (quest.DaExp) {
							user.getStats().addExp(quest.Exp);
							user.checkUserLevel();
							user.talk(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!",
									npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
						if (quest.DaOro) {
							user.getStats().addGold(quest.Oro);
							user.talk(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Oro + " monedas de oro!", npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
						if (quest.DaObj) {
							if (user.getUserInv().agregarItem(quest.Obj, 1) < 1) {
								Map mapa = this.server.getMap(user.pos().map);
								mapa.dropItemOnFloor(user.pos().x, user.pos().y,
										new InventoryObject(quest.Obj, 1));
							}
							user.talk(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
					} else {
						user.talk(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
					}
				} else if (user.userFaction().ArmadaReal) {
					if (user.userFaction().criminalsKilled >= quest.Criminales) {
						if (quest.DaExp) {
							user.getStats().addExp(quest.Exp);
							user.checkUserLevel();
							user.talk(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!",
									npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
						if (quest.DaOro) {
							user.getStats().addGold(quest.Oro);
							user.talk(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Oro + " monedas de oro!", npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
						if (quest.DaObj) {
							if (user.getUserInv().agregarItem(quest.Obj, 1) < 1) {
								Map mapa = this.server.getMap(user.pos().map);
								mapa.dropItemOnFloor(user.pos().x, user.pos().y,
										new InventoryObject(quest.Obj, 1));
							}
							user.talk(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
					} else {
						user.talk(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
					}
				} else if (user.userFaction().FuerzasCaos) {
					if (user.userFaction().citizensKilled >= quest.Ciudadanos) {
						if (quest.DaExp) {
							user.getStats().addExp(quest.Exp);
							user.checkUserLevel();
							user.talk(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!",
									npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
						if (quest.DaOro) {
							user.getStats().addGold(quest.Oro);
							user.talk(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Oro + " monedas de oro!", npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
						if (quest.DaObj) {
							if (user.getUserInv().agregarItem(quest.Obj, 1) < 1) {
								Map mapa = this.server.getMap(user.pos().map);
								mapa.dropItemOnFloor(user.pos().x, user.pos().y,
										new InventoryObject(quest.Obj, 1));
							}
							user.talk(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
							user.quest().m_realizoQuest = false;
							user.quest().m_recompensa = user.quest().m_nroQuest;
							user.quest().m_enQuest = false;
						}
					} else {
						user.talk(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
					}
				}
				break;
			case OBJETIVO_ENCONTRAR_NPC:
				if (user.quest().m_realizoQuest) {
					if (quest.DaExp) {
						user.getStats().addExp(quest.Exp);
						user.checkUserLevel();
						user.talk(COLOR_BLANCO,
								"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!", npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
					if (quest.DaOro) {
						user.getStats().addGold(quest.Oro);
						user.talk(COLOR_BLANCO, "Como recompensa has recibido " + quest.Oro + " monedas de oro!",
								npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
					if (quest.DaObj) {
						if (user.getUserInv().agregarItem(quest.Obj, 1) < 1) {
							Map mapa = this.server.getMap(user.pos().map);
							mapa.dropItemOnFloor(user.pos().x, user.pos().y, new InventoryObject(quest.Obj, 1));
						}
						user.talk(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
				} else {
					user.talk(COLOR_BLANCO, "Todavía no has logrado el objetivo!", npc.getId());
				}
				break;
			case OBJETIVO_MATAR_UN_NPC:
				if (user.quest().m_realizoQuest) {
					if (quest.DaExp) {
						user.getStats().addExp(quest.Exp);
						user.checkUserLevel();
						user.talk(COLOR_BLANCO,
								"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!", npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
					if (quest.DaOro) {
						user.getStats().addGold(quest.Oro);
						user.talk(COLOR_BLANCO, "Como recompensa has recibido " + quest.Oro + " monedas de oro!",
								npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
					if (quest.DaObj) {
						if (user.getUserInv().agregarItem(quest.Obj, 1) < 1) {
							Map mapa = this.server.getMap(user.pos().map);
							mapa.dropItemOnFloor(user.pos().x, user.pos().y, new InventoryObject(quest.Obj, 1));
						}
						user.talk(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
						user.quest().m_realizoQuest = false;
						user.quest().m_recompensa = user.quest().m_nroQuest;
						user.quest().m_enQuest = false;
					}
				} else {
					user.talk(COLOR_BLANCO, "Todavía no has logrado el objetivo!", npc.getId());
				}
				break;
			}
			user.sendUpdateUserStats();
		} catch (Exception e) {
			log.fatal(user.getUserName() + " Error en RecibirRecompensaQuest!", e);
		}
	}

	public void checkNpcAmigo(User user) {
		if (user.quest().m_enQuest) {
			Npc npc = this.server.npcById(user.getFlags().TargetNpc);
			if (npc.npcType() == NpcType.NPCTYPE_AMIGOQUEST) {
				Quest quest = user.quest().getQuest();
				if (quest.Objetivo == 3) {
					user.quest().m_realizoQuest = true;
					user.talk(COLOR_BLANCO,
							"Felicitaciones, me has encontrado, ahora debes volver con mi compañero por tu recompensa!",
							npc.getId());
				} else {
					user.talk(COLOR_BLANCO, "Yo correspondo a otra quest!", npc.getId());
				}
			} else {
				user.sendMessage("Este npc no es de la quest jajaja!", FontType.FONTTYPE_INFO);
			}
		} else {
			user.sendMessage("No has comenzado ninguna quest!", FontType.FONTTYPE_INFO);
		}
	}

	public void sendInfoQuest(User user) {
		Npc npc = this.server.npcById(user.getFlags().TargetNpc);
		if (user.isNewbie()) {
			user.talk(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
			return;
		}
		Quest quest = user.quest().getQuest();
		switch (quest.Objetivo) {
		case 1:
			user.talk(COLOR_BLANCO, "Debes matar " + quest.NPCs + " npcs para recibir tu recompensa!",
					npc.getId());
			break;
		case 2:
			if (!user.userFaction().ArmadaReal && !user.userFaction().FuerzasCaos) {
				user.talk(COLOR_BLANCO, "Debes matar " + quest.Usuarios + " usuarios para recibir tu recompensa!",
						npc.getId());
			} else if (user.userFaction().ArmadaReal) {
				user.talk(COLOR_BLANCO,
						"Debes matar " + quest.Criminales + " criminales para recibir tu recompensa!", npc.getId());
			} else if (user.userFaction().FuerzasCaos) {
				user.talk(COLOR_BLANCO,
						"Debes matar " + quest.Ciudadanos + " ciudadanos para recibir tu recompensa!", npc.getId());
			}
			break;
		case 3:
			user.talk(COLOR_BLANCO,
					"Debes encontrar a mi amigo npc para recibir tu recompensa! Pistas: Se pude encontrar en lugares característicos del juego," +
					" pero apresúrate porque si otro que está haciendo este tipo de quest lo encuentra primero puedes perderlo, en tal caso deberás clikearme y poner /MERINDO",
					npc.getId());
			break;
		case 4:
			int azar = Util.random(0, Pista.length);
			user.talk(COLOR_BLANCO, "Debes matar al npc que se encuentra en las coordenadas " +
					this.Pista[azar]
					+ " para recibir tu recompensa! PD: Si no te apresuras y lo mata otro usuario perderás esta quest, en tal caso deberás clikearme y poner /MERINDO",
					npc.getId());
			break;
		}
	}

	public void userSeRinde(User user) {
		Npc npc = this.server.npcById(user.getFlags().TargetNpc);
		try {
			if (user.isNewbie()) {
				user.talk(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
				return;
			}
			user.quest().m_realizoQuest = false;
			user.quest().m_recompensa = user.quest().m_nroQuest;
			user.quest().m_enQuest = false;
			user.talk(COLOR_BLANCO,
					"Te has rendido por lo tanto no has conseguido la recompensa, pero puedes continuar con la siguiente quest.",
					npc.getId());
		} catch (Exception e) {
			log.fatal(user.getUserName() + " Error en UserSeRinde!", e);
		}
	}

}
