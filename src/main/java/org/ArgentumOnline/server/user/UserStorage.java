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

import static org.ArgentumOnline.server.Constants.IntervaloParalizado;
import static org.ArgentumOnline.server.Constants.NingunArma;
import static org.ArgentumOnline.server.Constants.NingunCasco;
import static org.ArgentumOnline.server.Constants.NingunEscudo;
import static org.ArgentumOnline.server.Constants.OBJ_INDEX_CABEZA_MUERTO;
import static org.ArgentumOnline.server.Constants.OBJ_INDEX_CUERPO_MUERTO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import org.ArgentumOnline.server.Ciudad;
import org.ArgentumOnline.server.Clazz;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Heading;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.CharacterInfoResponse;
import org.ArgentumOnline.server.user.UserAttributes.Attribute;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserStorage {
	private static Logger log = LogManager.getLogger();

	private GameServer server;
	private Player user;
	
	public UserStorage(GameServer server, Player user) {
		this.server = server;
		this.user = user;
	}
	
	/**
	 * Load user data from storage
	 * @throws IOException
	 */
	public void loadUserFromStorage()
	throws IOException {
		IniFile ini = new IniFile(Player.getPjFile(user.userName));
		
		loadUserInit(ini);
		user.stats().loadUserStats(ini);
		user.skills().loadUserSkills(ini);
		user.spells().loadSpells(ini);
		user.reputation.loadUserReputacion(ini);
		loadPets(ini);
		if (!validateChr()) {
			user.sendError("Error en el personaje.");
			throw new RuntimeException("Error en el personaje." + user.getNick());
		}
	}

	/**
	 * Load offline user from storage (partial load)
	 * @throws IOException
	 */
	public void loadUserFromStorageOffline(String userName)
	throws IOException {
		user.userName = userName;
		IniFile ini = new IniFile(Player.getPjFile(user.userName));
		
		loadUserInit(ini);
		user.stats().loadUserStats(ini);
		user.skills().loadUserSkills(ini);
		user.reputation.loadUserReputacion(ini);
	}
	
	private boolean validateChr() {
		return user.infoChar().validateChr() && user.skills().validateSkills();
	}
	
	public String passwordHashFromStorage(String nick) 
	throws FileNotFoundException, IOException {
		IniFile ini = new IniFile(Player.getPjFile(user.getNick()));
		
		return ini.getString("INIT", "PasswordHash");
	}

	private void loadUserInit(IniFile ini) 
	throws java.io.IOException {
		user.userFaction().loadUserFaction(ini);

		user.banned = ini.getShort("BAN", "Banned") == 1;
		user.bannedBy = ini.getString("BAN", "BannedBy");
		user.bannedReason = ini.getString("BAN", "Reason");
		
		user.flags().Muerto = ini.getShort("FLAGS", "Muerto") == 1;
		user.flags().Escondido = ini.getShort("FLAGS", "Escondido") == 1;
		user.flags().Hambre = ini.getShort("FLAGS", "Hambre") == 1;
		user.flags().Sed = ini.getShort("FLAGS", "Sed") == 1;
		user.flags().Desnudo = ini.getShort("FLAGS", "Desnudo") == 1;
		user.flags().Envenenado = ini.getShort("FLAGS", "Envenenado") == 1;
		user.flags().Paralizado = ini.getShort("FLAGS", "Paralizado") == 1;
		user.flags().Desnudo = (ini.getShort("Inventory", "ArmourEqpSlot") == 0);
		user.flags().Navegando = ini.getShort("FLAGS", "Navegando") == 1;
		if (user.flags().Paralizado) {
			user.counters().Paralisis = IntervaloParalizado;
		}
		user.counters().Pena = ini.getLong("COUNTERS", "Pena");
		user.email = ini.getString("CONTACTO", "Email");
		user.gender = UserGender.value(ini.getShort("INIT", "Genero"));
		user.clazz = Clazz.value(ini.getInt("INIT", "Clase"));
		if (user.clazz == null) {
			throw new java.io.IOException("Clase desconocida: " + ini.getString("INIT", "Clase").toUpperCase());
		}
		user.race = UserRace.value(ini.getShort("INIT", "Raza"));
		user.homeland = Ciudad.value(ini.getShort("INIT", "Hogar"));

		user.origChar().head = ini.getShort("INIT", "Head");
		user.origChar().body = ini.getShort("INIT", "Body");
		user.origChar().weapon = ini.getShort("INIT", "Arma");
		user.origChar().shield = ini.getShort("INIT", "Escudo");
		user.origChar().helmet = ini.getShort("INIT", "Casco");
		user.origChar().heading = Heading.SOUTH;

		user.infoChar().heading = Heading.value(ini.getShort("INIT", "Heading"));
		if (user.isAlive()) {
			user.infoChar().head = user.origChar().head;
			user.infoChar().body = user.origChar().body;
			user.infoChar().weapon = user.origChar().weapon;
			user.infoChar().shield = user.origChar().shield;
			user.infoChar().helmet = user.origChar().helmet;
			user.infoChar().heading = user.origChar().heading;
		} else {
			user.infoChar().head = OBJ_INDEX_CABEZA_MUERTO;
			user.infoChar().body = OBJ_INDEX_CUERPO_MUERTO;
			user.infoChar().weapon = NingunArma;
			user.infoChar().shield = NingunEscudo;
			user.infoChar().helmet = NingunCasco;
		}
		
		user.description = ini.getString("INIT", "Desc");
		{
			StringTokenizer st = new StringTokenizer(ini.getString("INIT", "Position"), "-");
			user.pos().set(
					Short.parseShort(st.nextToken()),  // map
					Short.parseShort(st.nextToken()),  // x
					Short.parseShort(st.nextToken())); // y
		}

		// Lista de objetos en el banco.
		for (int i = 0; i < user.getBankInventory().size(); i++) {
			String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			user.getBankInventory().setObjeto(i + 1,
					new InventoryObject(Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken())));
		}

		// Lista de objetos del inventario del usuario.
		for (int i = 0; i < user.userInv().size(); i++) {
			String tmp = ini.getString("Inventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			user.userInv().setObjeto(i + 1, new InventoryObject(Short.parseShort(st.nextToken()),
					Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken()) == 1));
		}
		user.userInv().setArmaSlot(ini.getShort("Inventory", "WeaponEqpSlot"));
		user.userInv().setArmaduraSlot(ini.getShort("Inventory", "ArmourEqpSlot"));
		user.userInv().setEscudoSlot(ini.getShort("Inventory", "EscudoEqpSlot"));
		user.userInv().setCascoSlot(ini.getShort("Inventory", "CascoEqpSlot"));
		user.userInv().setBarcoSlot(ini.getShort("Inventory", "BarcoSlot"));
		user.userInv().setMunicionSlot(ini.getShort("Inventory", "MunicionSlot"));

		user.guildUser.m_fundoClan = ini.getShort("Guild", "FundoClan") == 1;
		user.guildUser.m_esGuildLeader = ini.getShort("Guild", "EsGuildLeader") == 1;
		user.guildUser.m_echadas = ini.getLong("Guild", "Echadas");
		user.guildUser.m_solicitudes = ini.getLong("Guild", "Solicitudes");
		user.guildUser.m_solicitudesRechazadas = ini.getLong("Guild", "SolicitudesRechazadas");
		user.guildUser.m_vecesFueGuildLeader = ini.getLong("Guild", "VecesFueGuildLeader");
		user.guildUser.m_yaVoto = ini.getShort("Guild", "YaVoto") == 1;
		user.guildUser.m_clanesParticipo = ini.getLong("Guild", "ClanesParticipo");
		user.guildUser.m_guildPoints = ini.getLong("Guild", "GuildPts");
		user.guildUser.m_clanFundado = ini.getString("Guild", "ClanFundado");
		user.guildUser.m_guildName = ini.getString("Guild", "GuildName");

		user.quest().m_nroQuest = ini.getShort("QUEST", "NroQuest");
		user.quest().m_recompensa = ini.getShort("QUEST", "Recompensa");
		user.quest().m_enQuest = (ini.getShort("QUEST", "EnQuest") == 1);
		user.quest().m_realizoQuest = (ini.getShort("QUEST", "RealizoQuest") == 1);
	}

	private void loadPets(IniFile ini) {
		int cantMascotas = ini.getShort("Mascotas", "NroMascotas");
		// Lista de mascotas.
		for (int i = 1; i <= cantMascotas; i++) {
			short npcNumber = ini.getShort("Mascotas", "Mas" + i);
			Npc pet = server.createNpc(npcNumber);
			user.addTamedPet(pet);
			pet.activate();
		}
	}
	
	public static CharacterInfoResponse createCharacterInfoResponse(String userName) {
		// ¿Existe el personaje?
		if (!Util.existeArchivo(Player.getPjFile(userName))) {
			return null;
		}
		try {
			IniFile ini = new IniFile(Player.getPjFile(userName));
			
			Reputation tmpReputation = new Reputation();
			tmpReputation.loadUserReputacion(ini);
			
			/*
        'Get previous guilds
        Miembro = .GetValue("GUILD", "Miembro")
        If Len(Miembro) > 400 Then
            Miembro = ".." & Right$(Miembro, 400)
        End If
			 */
			return new CharacterInfoResponse(
					userName, 
					(byte)(ini.getShort("INIT", "Raza")),
					(byte)(ini.getInt("INIT", "Clase")), 
					(byte)(ini.getShort("INIT", "Genero")), 
					(byte)ini.getInt("STATS", "ELV"), 
					ini.getInt("STATS", "GLD"), 
					ini.getInt("STATS", "BANCO"), 
					(int)tmpReputation.getPromedio(), 
					""/* FIXME previousPetitions GetValue("GUILD", "Pedidos") */, 
					ini.getString("Guild", "GuildName"), 
					"" /* FIXME previousGuilds */, 
					(byte) ini.getShort("FACCIONES", "EjercitoReal"), 
					(byte) ini.getShort("FACCIONES", "EjercitoCaos"), 
					(int)ini.getLong("FACCIONES", "CiudMatados"), 
					(int)ini.getLong("FACCIONES", "CrimMatados"));			
		} catch (java.io.IOException e) {
			log.fatal("ERROR getChrInfo", e);
		}
		return null;
	}

	public void saveUserToStorage() {
		// / guardar en archivo .chr
		try {
			IniFile ini = new IniFile();
			ini.setValue("FLAGS", "Muerto", user.flags().Muerto);
			ini.setValue("FLAGS", "Escondido", user.flags().Escondido);
			ini.setValue("FLAGS", "Hambre", user.flags().Hambre);
			ini.setValue("FLAGS", "Sed", user.flags().Sed);
			ini.setValue("FLAGS", "Desnudo", user.flags().Desnudo);
			ini.setValue("FLAGS", "Navegando", user.flags().Navegando);
			ini.setValue("FLAGS", "Envenenado", user.flags().Envenenado);
			ini.setValue("FLAGS", "Paralizado", user.flags().Paralizado);

			ini.setValue("COUNTERS", "Pena", user.counters().Pena);

			user.userFaction().saveUserFaction(ini);

			ini.setValue("GUILD", "EsGuildLeader", user.guildUser.m_esGuildLeader);
			ini.setValue("GUILD", "Echadas", user.guildUser.m_echadas);
			ini.setValue("GUILD", "Solicitudes", user.guildUser.m_solicitudes);
			ini.setValue("GUILD", "SolicitudesRechazadas", user.guildUser.m_solicitudesRechazadas);
			ini.setValue("GUILD", "VecesFueGuildLeader", user.guildUser.m_vecesFueGuildLeader);
			ini.setValue("GUILD", "YaVoto", user.guildUser.m_yaVoto);
			ini.setValue("GUILD", "FundoClan", user.guildUser.m_fundoClan);
			ini.setValue("GUILD", "GuildName", user.guildUser.m_guildName);
			ini.setValue("GUILD", "ClanFundado", user.guildUser.m_clanFundado);
			ini.setValue("GUILD", "ClanesParticipo", user.guildUser.m_clanesParticipo);
			ini.setValue("GUILD", "GuildPts", user.guildUser.m_guildPoints);

			ini.setValue("QUEST", "NroQuest", user.quest().m_nroQuest);
			ini.setValue("QUEST", "EnQuest", user.quest().m_enQuest);
			ini.setValue("QUEST", "RealizoQuest", user.quest().m_realizoQuest);
			ini.setValue("QUEST", "Recompensa", user.quest().m_recompensa);

			ini.setValue("BAN", "Banned", user.banned);
			ini.setValue("BAN", "BannedBy", user.bannedBy);
			ini.setValue("BAN", "Reason", user.bannedReason);

			// ¿Fueron modificados los atributos del usuario?
			int i = 1;
			for (Attribute attr : Attribute.values()) {
				if (!user.flags().TomoPocion) {
					ini.setValue("ATRIBUTOS", "AT" + (i++), user.stats().attr().get(attr));
				} else {
					ini.setValue("ATRIBUTOS", "AT" + (i++), user.stats().attr().getBackup(attr));
				}
			}

			i = 1;
			for (Skill skill : Skill.values()) {
				ini.setValue("SKILLS", "SK" + (i++), user.skills().get(skill));
			}

			ini.setValue("CONTACTO", "Email", user.email);

			ini.setValue("INIT", "Genero", user.gender.value());
			ini.setValue("INIT", "Raza", user.race.value());
			ini.setValue("INIT", "Hogar", user.homeland.id());
			ini.setValue("INIT", "Clase", user.clazz().id());
			ini.setValue("INIT", "PasswordHash", user.passwordHash);
			ini.setValue("INIT", "Desc", user.description);
			ini.setValue("INIT", "Heading", user.infoChar().heading.value());

			if (user.flags().Muerto || user.flags().Invisible || user.flags().Navegando) {
				ini.setValue("INIT", "Head", user.origChar().head);
				ini.setValue("INIT", "Body", user.origChar().body);
			} else {
				ini.setValue("INIT", "Head", user.infoChar().head);
				ini.setValue("INIT", "Body", user.infoChar().body);
			}

			ini.setValue("INIT", "Arma", user.infoChar().weapon);
			ini.setValue("INIT", "Escudo", user.infoChar().shield);
			ini.setValue("INIT", "Casco", user.infoChar().helmet);
			ini.setValue("INIT", "LastIP", user.getIP());
			ini.setValue("INIT", "Position", user.pos().map + "-" + user.pos().x + "-" + user.pos().y);

			ini.setValue("STATS", "GLD", user.stats().getGold());
			ini.setValue("STATS", "BANCO", user.stats().getBankGold());

			// ini.setValue("STATS", "MET", user.getEstads().MET);
			ini.setValue("STATS", "MaxHP", user.stats().MaxHP);
			ini.setValue("STATS", "MinHP", user.stats().MinHP);

			// ini.setValue("STATS", "FIT", user.getEstads().FIT);
			ini.setValue("STATS", "MaxSTA", user.stats().maxStamina);
			ini.setValue("STATS", "MinSTA", user.stats().stamina);

			ini.setValue("STATS", "MaxMAN", user.stats().maxMana);
			ini.setValue("STATS", "MinMAN", user.stats().mana);

			ini.setValue("STATS", "MaxHIT", user.stats().MaxHIT);
			ini.setValue("STATS", "MinHIT", user.stats().MinHIT);

			ini.setValue("STATS", "MaxAGU", user.stats().maxDrinked);
			ini.setValue("STATS", "MinAGU", user.stats().drinked);

			ini.setValue("STATS", "MaxHAM", user.stats().maxEaten);
			ini.setValue("STATS", "MinHAM", user.stats().eaten);

			ini.setValue("STATS", "SkillPtsLibres", user.skills().freeSkillPts);

			ini.setValue("STATS", "EXP", user.stats().Exp);
			ini.setValue("STATS", "ELV", user.stats().ELV);
			ini.setValue("STATS", "ELU", user.stats().ELU);

			ini.setValue("MUERTES", "UserMuertes", user.stats().usuariosMatados);
			ini.setValue("MUERTES", "NpcsMuertes", user.stats().NPCsMuertos);

			i = 1;
			for (InventoryObject invObj : user.getBankInventory()) {
				if (invObj.estaVacio()) {
					ini.setValue("BancoInventory", "Obj" + (i++), "0-0");
				} else {
					ini.setValue("BancoInventory", "Obj" + (i++), invObj.objid + "-" + invObj.cant);
				}
			}
			ini.setValue("BancoInventory", "CantidadItems", user.getBankInventory().size());

			i = 1;
			for (InventoryObject invObj : user.userInv()) {
				if (invObj.estaVacio()) {
					ini.setValue("Inventory", "Obj" + (i++), "0-0-0");
				} else {
					ini.setValue("Inventory", "Obj" + (i++), 
						invObj.objid + "-" + invObj.cant + "-" + (invObj.equipado ? 1 : 0));
				}
			}
			ini.setValue("Inventory", "CantidadItems", user.userInv().size());
			
			ini.setValue("Inventory", "WeaponEqpSlot", user.userInv().getArmaSlot());
			ini.setValue("Inventory", "ArmourEqpSlot", user.userInv().getArmaduraSlot());
			ini.setValue("Inventory", "CascoEqpSlot", user.userInv().getCascoSlot());
			ini.setValue("Inventory", "EscudoEqpSlot", user.userInv().getEscudoSlot());
			ini.setValue("Inventory", "BarcoSlot", user.userInv().getBarcoSlot());
			ini.setValue("Inventory", "MunicionSlot", user.userInv().getMunicionSlot());
			ini.setValue("Inventory", "EspadaMataDragonesSlot", user.userInv().getEspadaMataDragonesSlot());

			// Reputacion
			ini.setValue("REP", "Asesino", user.reputation().asesinoRep);
			ini.setValue("REP", "Bandido", user.reputation().bandidoRep);
			ini.setValue("REP", "Burguesia", user.reputation().burguesRep);
			ini.setValue("REP", "Ladrones", user.reputation().ladronRep);
			ini.setValue("REP", "Nobles", user.reputation().nobleRep);
			ini.setValue("REP", "Plebe", user.reputation().plebeRep);

			ini.setValue("REP", "Promedio", user.reputation().getPromedio());

			for (int slot = 1; slot <= user.spells().getCount(); slot++) {
				ini.setValue("HECHIZOS", "H" + slot, user.spells().getSpell(slot));
			}

			var pets = user.getUserPets().getPets();
			int savedCount = 0;
			for (Npc pet: pets) {
				// Se guardan las mascotas que no fueron invocadas
				if ( ! pet.isSpellSpawnedPet() ) {
					ini.setValue("MASCOTAS", "MAS" + (++savedCount), pet.getNumber());
				}
			}
			ini.setValue("MASCOTAS", "NroMascotas", savedCount);

			// Devuelve el head de muerto
			if (!user.isAlive()) {
				user.infoChar().head = OBJ_INDEX_CABEZA_MUERTO;
			}
			
			updateLastIp(ini);

			// Guardar todo
			ini.store(Player.getPjFile(user.userName));
		} catch (Exception e) {
			log.fatal(user.getNick() + ": ERROR EN SAVEUSER()", e);
		}
	}

	private void updateLastIp(IniFile ini) {
		try {
			List<String> lastIP = new ArrayList<>();
			IntStream.range(1, 6).forEach(i -> {
				String ip = ini.getString("INIT", "LastIP" + i);
				if (!ip.isEmpty()) {
					lastIP.add(ip);
				}
			});
			
			if (lastIP.isEmpty() || lastIP.get(0).isEmpty() || !lastIP.get(0).equals(user.getIP())) {
				lastIP.add(0, user.getIP());
				if (lastIP.size() > 5) {
					lastIP.remove(lastIP.size()-1);
				}
			}
			
			IntStream.range(1, 6).forEach(i -> {
				if (i <= lastIP.size()) {
					ini.setValue("INIT", "LastIP" + i, lastIP.get(i-1));
				} else {
					ini.setValue("INIT", "LastIP" + i, "");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> loadLastIPs() {
		List<String> lastIP = new ArrayList<>();
		IniFile ini;
		try {
			ini = new IniFile(Player.getPjFile(user.getNick()));
			IntStream.range(1, 6).forEach(i -> {
				String ip = ini.getString("INIT", "LastIP" + i);
				if (!ip.isEmpty()) {
					lastIP.add(ip);
				}
			});
		} catch (IOException ignored) {
		}
		return lastIP;
	}

	public static String emailFromStorage(String userName) 
	throws FileNotFoundException, IOException {
		IniFile ini = new IniFile(Player.getPjFile(userName));
		
		return ini.getString("CONTACTO", "Email");
	}

	public static List<String> punishments(String userName) {
		List<String> punishments = new ArrayList<>();
		IniFile ini;
		try {
			ini = new IniFile(Player.getPjFile(userName));
			int count = ini.getInt("PENAS", "Cant");
			IntStream.range(1, count + 1).forEach(i -> {
				punishments.add(ini.getString("PENAS", "P" + i));
			});
		} catch (IOException ignored) {
		}
		return punishments;
	}

	public static void addPunishment(String userName, String text) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			int count = ini.getInt("PENAS", "Cant");
			count++;
			
			ini.setValue("PENAS", "Cant", count);
			ini.setValue("PENAS", "P" + count, text);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}

	public static void updatePunishment(String userName, byte index, String newText) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			int count = ini.getInt("PENAS", "Cant");
			if (index < 1 || index > count) {
				return;
			}
			ini.setValue("PENAS", "P" + index, newText);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}
	
    public static boolean isUserBanned(String userName) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			return ini.getShort("FLAGS", "Ban") == 1;
		} catch (IOException ignored) {
		}
    	return false;
    }
    
    public static void banUser(String userName, String admin, String reason) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("BAN", "Banned", 1);
			ini.setValue("BAN", "BannedBy", admin);
			ini.setValue("BAN", "Reason", reason);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }

    public static void unBanUser(String userName) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("BAN", "Banned", 0);
			ini.setValue("BAN", "BannedBy", "");
			ini.setValue("BAN", "Reason", "");
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }

    public static void updateEmail(String userName, String newEmail) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("CONTACTO", "Email", newEmail);    
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void updatePassword(String userName, String newPasswordHash) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "PasswordHash", newPasswordHash);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static String getGuildName(String userName) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			return ini.getString("Guild", "GuildName");
		} catch (IOException ignored) {
		}
    	return null;
    }

	public static void changeName(String userName, String newName) throws IOException {
		Path original = Paths.get(Player.getPjFile(userName));
	    Path copied = Paths.get(Player.getPjFile(newName));
	    Files.copy(original, copied, StandardCopyOption.COPY_ATTRIBUTES);		
	}
    
}
