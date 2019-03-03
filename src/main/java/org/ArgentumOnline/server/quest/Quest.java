/**
 * Quest.java
 *
 * Created on 21 de marzo de 2004, 12:19
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
package org.ArgentumOnline.server.quest;

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;

import java.util.StringTokenizer;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.map.MapPos;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.npc.NpcType;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	public void hacerQuest(Player player, Npc npc) {
		try {
			if (player.esNewbie()) {
				player.enviarHabla(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
				return;
			}
			if (player.getQuest().m_enQuest) {
				player.enviarHabla(COLOR_BLANCO,
						"Debes terminar primero la quest que estás realizando para empezar otra!", npc.getId());
				return;
			}
			if (player.getQuest().m_nroQuest >= this.server.questCount()) {
				player.enviarHabla(COLOR_BLANCO, "Ya has realizados todas las quest!", npc.getId());
				return;
			}
			player.nextQuest();
			Quest quest = player.getQuest().getQuest();
			int azar = 0;
			switch (quest.Objetivo) {
			case OBJETIVO_MATAR_NPCS:
				player.enviarHabla(COLOR_BLANCO, "Debes matar " + this.NPCs + " npcs para recibir tu recompensa!",
						npc.getId());
				break;
			case OBJETIVO_MATAR_USUARIOS:
				if (!player.userFaction().ArmadaReal && !player.userFaction().FuerzasCaos) {
					player.enviarHabla(COLOR_BLANCO,
							"Debes matar " + this.Usuarios + " usuarios para recibir tu recompensa!", npc.getId());
				} else if (player.userFaction().ArmadaReal) {
					player.enviarHabla(COLOR_BLANCO,
							"Debes matar " + this.Criminales + " criminales para recibir tu recompensa!", npc.getId());
				} else if (player.userFaction().FuerzasCaos) {
					player.enviarHabla(COLOR_BLANCO,
							"Debes matar " + this.Ciudadanos + " ciudadanos para recibir tu recompensa!", npc.getId());
				}
				break;
			case OBJETIVO_ENCONTRAR_NPC:
				azar = Util.Azar(1, CANT_NPCS);
				if (this.Coordenadas[azar - 1] == null) {
					return;
				}
				player.enviarHabla(COLOR_BLANCO,
						"Debes encontrar a mi amigo npc para recibir tu recompensa! Pistas: Se puede encontrar en lugares característicos del juego, pero apresúrate!" +
						" Si alguien que está haciendo este mismo tipo de quest, y lo encuentra antes, puedes perderlo. En tal caso deberás volver y hacerme clic, y poner /MERINDO",
						npc.getId());
				// Npc amigoNpc =
				Npc.spawnNpc(this.AmigoNpc, this.Coordenadas[azar - 1], true, false);
				break;
			case OBJETIVO_MATAR_UN_NPC:
				azar = Util.Azar(1, CANT_NPCS);
				if (this.Coordenadas[azar - 1] == null) {
					return;
				}
				player.enviarHabla(COLOR_BLANCO, "Debes matar al npc que se encuentra en las coordenadas "
						+ this.Pista[azar - 1]
						+ " para recibir tu recompensa! PD: Si no te apresuras, y lo mata otro usuario, perderás esta quest. En tal caso deberás volver y hacerme clic, y poner /MERINDO",
						npc.getId());
				// Npc criaturaNpc =
				Npc.spawnNpc(this.CriaturaIndex, this.Coordenadas[azar - 1], true, false);
				break;
			}
		} catch (Exception e) {
			log.fatal(player.getNick() + " Error en HacerQuest!", e);
		}
	}

	public void recibirRecompensaQuest(Player player) {
		try {
			Npc npc = this.server.npcById(player.flags().TargetNpc);
			if (player.esNewbie()) {
				player.hablar(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
				return;
			}
			if (player.getQuest().m_nroQuest <= 0) {
				player.hablar(COLOR_BLANCO, "No has empezado ninguna Quest!", npc.getId());
				return;
			}
			if (player.getQuest().m_enQuest) {
				if (player.getQuest().m_recompensa == player.getQuest().m_nroQuest) {
					player.hablar(COLOR_BLANCO, "Ya has recibido tu recompensa por esta Quest!", npc.getId());
					return;
				}
			}
			Quest quest = player.getQuest().getQuest();
			switch (quest.Objetivo) {
			case OBJETIVO_MATAR_NPCS:
				if (player.stats().NPCsMuertos >= quest.NPCs) {
					if (quest.DaExp) {
						player.stats().addExp(quest.Exp);
						player.checkUserLevel();
						player.hablar(COLOR_BLANCO,
								"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!", npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
					if (quest.DaOro) {
						player.stats().addGold(quest.Oro);
						player.hablar(COLOR_BLANCO, "Como recompensa has recibido " + quest.Oro + " monedas de oro!",
								npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
					if (quest.DaObj) {
						if (player.userInv().agregarItem(quest.Obj, 1) < 1) {
							Map mapa = this.server.getMap(player.pos().map);
							mapa.tirarItemAlPiso(player.pos().x, player.pos().y, new InventoryObject(quest.Obj, 1));
						}
						player.hablar(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
				} else {
					player.hablar(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
				}
				break;
			case OBJETIVO_MATAR_USUARIOS:
				if (!player.userFaction().ArmadaReal && !player.userFaction().FuerzasCaos) {
					if (player.userFaction().CiudadanosMatados
							+ player.userFaction().CriminalesMatados >= quest.Usuarios) {
						if (quest.DaExp) {
							player.stats().addExp(quest.Exp);
							player.checkUserLevel();
							player.hablar(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!",
									npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
						if (quest.DaOro) {
							player.stats().addGold(quest.Oro);
							player.hablar(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Oro + " monedas de oro!", npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
						if (quest.DaObj) {
							if (player.userInv().agregarItem(quest.Obj, 1) < 1) {
								Map mapa = this.server.getMap(player.pos().map);
								mapa.tirarItemAlPiso(player.pos().x, player.pos().y,
										new InventoryObject(quest.Obj, 1));
							}
							player.hablar(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
					} else {
						player.hablar(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
					}
				} else if (player.userFaction().ArmadaReal) {
					if (player.userFaction().CriminalesMatados >= quest.Criminales) {
						if (quest.DaExp) {
							player.stats().addExp(quest.Exp);
							player.checkUserLevel();
							player.hablar(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!",
									npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
						if (quest.DaOro) {
							player.stats().addGold(quest.Oro);
							player.hablar(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Oro + " monedas de oro!", npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
						if (quest.DaObj) {
							if (player.userInv().agregarItem(quest.Obj, 1) < 1) {
								Map mapa = this.server.getMap(player.pos().map);
								mapa.tirarItemAlPiso(player.pos().x, player.pos().y,
										new InventoryObject(quest.Obj, 1));
							}
							player.hablar(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
					} else {
						player.hablar(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
					}
				} else if (player.userFaction().FuerzasCaos) {
					if (player.userFaction().CiudadanosMatados >= quest.Ciudadanos) {
						if (quest.DaExp) {
							player.stats().addExp(quest.Exp);
							player.checkUserLevel();
							player.hablar(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!",
									npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
						if (quest.DaOro) {
							player.stats().addGold(quest.Oro);
							player.hablar(COLOR_BLANCO,
									"Como recompensa has recibido " + quest.Oro + " monedas de oro!", npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
						if (quest.DaObj) {
							if (player.userInv().agregarItem(quest.Obj, 1) < 1) {
								Map mapa = this.server.getMap(player.pos().map);
								mapa.tirarItemAlPiso(player.pos().x, player.pos().y,
										new InventoryObject(quest.Obj, 1));
							}
							player.hablar(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
							player.getQuest().m_realizoQuest = false;
							player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
							player.getQuest().m_enQuest = false;
						}
					} else {
						player.hablar(COLOR_BLANCO, "Todavía no has completado el objetivo!", npc.getId());
					}
				}
				break;
			case OBJETIVO_ENCONTRAR_NPC:
				if (player.getQuest().m_realizoQuest) {
					if (quest.DaExp) {
						player.stats().addExp(quest.Exp);
						player.checkUserLevel();
						player.hablar(COLOR_BLANCO,
								"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!", npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
					if (quest.DaOro) {
						player.stats().addGold(quest.Oro);
						player.hablar(COLOR_BLANCO, "Como recompensa has recibido " + quest.Oro + " monedas de oro!",
								npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
					if (quest.DaObj) {
						if (player.userInv().agregarItem(quest.Obj, 1) < 1) {
							Map mapa = this.server.getMap(player.pos().map);
							mapa.tirarItemAlPiso(player.pos().x, player.pos().y, new InventoryObject(quest.Obj, 1));
						}
						player.hablar(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
				} else {
					player.hablar(COLOR_BLANCO, "Todavía no has logrado el objetivo!", npc.getId());
				}
				break;
			case OBJETIVO_MATAR_UN_NPC:
				if (player.getQuest().m_realizoQuest) {
					if (quest.DaExp) {
						player.stats().addExp(quest.Exp);
						player.checkUserLevel();
						player.hablar(COLOR_BLANCO,
								"Como recompensa has recibido " + quest.Exp + " puntos de experiencia!", npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
					if (quest.DaOro) {
						player.stats().addGold(quest.Oro);
						player.hablar(COLOR_BLANCO, "Como recompensa has recibido " + quest.Oro + " monedas de oro!",
								npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
					if (quest.DaObj) {
						if (player.userInv().agregarItem(quest.Obj, 1) < 1) {
							Map mapa = this.server.getMap(player.pos().map);
							mapa.tirarItemAlPiso(player.pos().x, player.pos().y, new InventoryObject(quest.Obj, 1));
						}
						player.hablar(COLOR_BLANCO, "Como recompensa has recibido un objeto!", npc.getId());
						player.getQuest().m_realizoQuest = false;
						player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
						player.getQuest().m_enQuest = false;
					}
				} else {
					player.hablar(COLOR_BLANCO, "Todavía no has logrado el objetivo!", npc.getId());
				}
				break;
			}
			player.sendUpdateUserStats();
		} catch (Exception e) {
			log.fatal(player.getNick() + " Error en RecibirRecompensaQuest!", e);
		}
	}

	public void checkNpcAmigo(Player player) {
		if (player.getQuest().m_enQuest) {
			Npc npc = this.server.npcById(player.flags().TargetNpc);
			if (npc.npcType() == NpcType.NPCTYPE_AMIGOQUEST) {
				Quest quest = player.getQuest().getQuest();
				if (quest.Objetivo == 3) {
					player.getQuest().m_realizoQuest = true;
					player.hablar(COLOR_BLANCO,
							"Felicitaciones, me has encontrado, ahora debes volver con mi compañero por tu recompensa!",
							npc.getId());
				} else {
					player.hablar(COLOR_BLANCO, "Yo correspondo a otra quest!", npc.getId());
				}
			} else {
				player.enviarMensaje("Este npc no es de la quest jajaja!", FontType.FONTTYPE_INFO);
			}
		} else {
			player.enviarMensaje("No has comenzado ninguna quest!", FontType.FONTTYPE_INFO);
		}
	}

	public void sendInfoQuest(Player player) {
		Npc npc = this.server.npcById(player.flags().TargetNpc);
		if (player.esNewbie()) {
			player.hablar(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
			return;
		}
		Quest quest = player.getQuest().getQuest();
		switch (quest.Objetivo) {
		case 1:
			player.hablar(COLOR_BLANCO, "Debes matar " + quest.NPCs + " npcs para recibir tu recompensa!",
					npc.getId());
			break;
		case 2:
			if (!player.userFaction().ArmadaReal && !player.userFaction().FuerzasCaos) {
				player.hablar(COLOR_BLANCO, "Debes matar " + quest.Usuarios + " usuarios para recibir tu recompensa!",
						npc.getId());
			} else if (player.userFaction().ArmadaReal) {
				player.hablar(COLOR_BLANCO,
						"Debes matar " + quest.Criminales + " criminales para recibir tu recompensa!", npc.getId());
			} else if (player.userFaction().FuerzasCaos) {
				player.hablar(COLOR_BLANCO,
						"Debes matar " + quest.Ciudadanos + " ciudadanos para recibir tu recompensa!", npc.getId());
			}
			break;
		case 3:
			player.hablar(COLOR_BLANCO,
					"Debes encontrar a mi amigo npc para recibir tu recompensa! Pistas: Se pude encontrar en lugares característicos del juego," +
					" pero apresúrate porque si otro que está haciendo este tipo de quest lo encuentra primero puedes perderlo, en tal caso deberás clikearme y poner /MERINDO",
					npc.getId());
			break;
		case 4:
			int azar = Util.Azar(0, Pista.length);
			player.hablar(COLOR_BLANCO, "Debes matar al npc que se encuentra en las coordenadas " +
					this.Pista[azar]
					+ " para recibir tu recompensa! PD: Si no te apresuras y lo mata otro usuario perderás esta quest, en tal caso deberás clikearme y poner /MERINDO",
					npc.getId());
			break;
		}
	}

	public void userSeRinde(Player player) {
		Npc npc = this.server.npcById(player.flags().TargetNpc);
		try {
			if (player.esNewbie()) {
				player.hablar(COLOR_BLANCO, "Los newbies no pueden realizar estas quests!", npc.getId());
				return;
			}
			player.getQuest().m_realizoQuest = false;
			player.getQuest().m_recompensa = player.getQuest().m_nroQuest;
			player.getQuest().m_enQuest = false;
			player.hablar(COLOR_BLANCO,
					"Te has rendido por lo tanto no has conseguido la recompensa, pero puedes continuar con la siguiente quest.",
					npc.getId());
		} catch (Exception e) {
			log.fatal(player.getNick() + " Error en UserSeRinde!", e);
		}
	}

}
