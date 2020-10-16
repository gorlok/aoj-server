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

import static org.argentumonline.server.Constants.IntervaloParalizado;
import static org.argentumonline.server.Constants.NingunArma;
import static org.argentumonline.server.Constants.NingunCasco;
import static org.argentumonline.server.Constants.NingunEscudo;
import static org.argentumonline.server.Constants.OBJ_INDEX_CABEZA_MUERTO;
import static org.argentumonline.server.Constants.OBJ_INDEX_CUERPO_MUERTO;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.City;
import org.argentumonline.server.Clazz;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.Skill;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.map.Heading;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.protocol.CharacterInfoResponse;
import org.argentumonline.server.user.UserAttributes.Attribute;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Util;

public class UserStorage {
	private static Logger log = LogManager.getLogger();

	private GameServer server;
	private User user;
	
	public UserStorage(GameServer server, User user) {
		this.server = server;
		this.user = user;
	}
	
	/**
	 * Load user data from storage
	 * @throws IOException
	 */
	public void loadUserFromStorage()
	throws IOException {
		IniFile ini = new IniFile(User.getPjFile(user.userName));
		
		loadUserInit(ini);
		user.getStats().loadUserStats(ini);
		user.skills().loadUserSkills(ini);
		user.spells().loadSpells(ini);
		user.reputation.loadUserReputacion(ini);
		loadPets(ini);
		if (!validateChr()) {
			user.sendError("Error en el personaje.");
			throw new RuntimeException("Error en el personaje." + user.getUserName());
		}
	}

	/**
	 * Load offline user from storage (partial load)
	 * @throws IOException
	 */
	public void loadUserFromStorageOffline(String userName)
	throws IOException {
		user.userName = userName;
		IniFile ini = new IniFile(User.getPjFile(user.getUserName()));
		
		loadUserInit(ini);
		user.getStats().loadUserStats(ini);
		user.skills().loadUserSkills(ini);
		user.reputation.loadUserReputacion(ini);
	}
	
	private boolean validateChr() {
		return user.infoChar().validateChr() && user.skills().validateSkills();
	}
	
	public String passwordHashFromStorage() 
	throws FileNotFoundException, IOException {
		return passwordHashFromStorage(this.user.getUserName());
	}
	
	public static String passwordHashFromStorage(String userName) 
	throws FileNotFoundException, IOException {
		if (User.userExists(userName)) {
			IniFile ini = new IniFile(User.getPjFile(userName));
			return ini.getString("INIT", "PasswordHash");
		} else {
			return null;
		}
	}

	private void loadUserInit(IniFile ini) 
	throws java.io.IOException {
		user.userFaction().loadUserFaction(ini);

		user.banned = ini.getShort("BAN", "Banned") == 1;
		user.bannedBy = ini.getString("BAN", "BannedBy");
		user.bannedReason = ini.getString("BAN", "Reason");
		
		user.getFlags().Muerto = ini.getShort("FLAGS", "Muerto") == 1;
		user.getFlags().Escondido = ini.getShort("FLAGS", "Escondido") == 1;
		user.getFlags().Hambre = ini.getShort("FLAGS", "Hambre") == 1;
		user.getFlags().Sed = ini.getShort("FLAGS", "Sed") == 1;
		user.getFlags().Desnudo = ini.getShort("FLAGS", "Desnudo") == 1;
		user.getFlags().Envenenado = ini.getShort("FLAGS", "Envenenado") == 1;
		user.getFlags().Paralizado = ini.getShort("FLAGS", "Paralizado") == 1;
		user.getFlags().Desnudo = (ini.getShort("Inventory", "ArmourEqpSlot") == 0);
		user.getFlags().Navegando = ini.getShort("FLAGS", "Navegando") == 1;
		if (user.getFlags().Paralizado) {
			user.getCounters().Paralisis = IntervaloParalizado;
		}
		user.getCounters().Pena = ini.getLong("COUNTERS", "Pena");
		user.email = ini.getString("CONTACTO", "Email");
		user.gender = UserGender.value(ini.getShort("INIT", "Genero"));
		user.clazz = Clazz.value(ini.getInt("INIT", "Clase"));
		if (user.clazz == null) {
			throw new java.io.IOException("Clase desconocida: " + ini.getString("INIT", "Clase").toUpperCase());
		}
		user.race = UserRace.value(ini.getShort("INIT", "Raza"));
		user.homeland = City.value(ini.getShort("INIT", "Hogar"));

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
		for (int i = 0; i < user.getBankInventory().getSize(); i++) {
			String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			user.getBankInventory().setObject(i + 1,
					new InventoryObject(Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken())));
		}

		// Lista de objetos del inventario del usuario.
		for (int i = 0; i < user.getUserInv().getSize(); i++) {
			String tmp = ini.getString("Inventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			user.getUserInv().setObject(i + 1, new InventoryObject(Short.parseShort(st.nextToken()),
					Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken()) == 1));
		}
		user.getUserInv().setArmaSlot(ini.getShort("Inventory", "WeaponEqpSlot"));
		user.getUserInv().setArmaduraSlot(ini.getShort("Inventory", "ArmourEqpSlot"));
		user.getUserInv().setEscudoSlot(ini.getShort("Inventory", "EscudoEqpSlot"));
		user.getUserInv().setCascoSlot(ini.getShort("Inventory", "CascoEqpSlot"));
		user.getUserInv().setBarcoSlot(ini.getShort("Inventory", "BarcoSlot"));
		user.getUserInv().setMunicionSlot(ini.getShort("Inventory", "MunicionSlot"));

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
		if (!Util.fileExists(User.getPjFile(userName))) {
			return null;
		}
		try {
			IniFile ini = new IniFile(User.getPjFile(userName));
			
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
			ini.setValue("FLAGS", "Muerto", user.getFlags().Muerto);
			ini.setValue("FLAGS", "Escondido", user.getFlags().Escondido);
			ini.setValue("FLAGS", "Hambre", user.getFlags().Hambre);
			ini.setValue("FLAGS", "Sed", user.getFlags().Sed);
			ini.setValue("FLAGS", "Desnudo", user.getFlags().Desnudo);
			ini.setValue("FLAGS", "Navegando", user.getFlags().Navegando);
			ini.setValue("FLAGS", "Envenenado", user.getFlags().Envenenado);
			ini.setValue("FLAGS", "Paralizado", user.getFlags().Paralizado);

			ini.setValue("COUNTERS", "Pena", user.getCounters().Pena);

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
				if (!user.getFlags().TomoPocion) {
					ini.setValue("ATRIBUTOS", "AT" + (i++), user.getStats().attr().get(attr));
				} else {
					ini.setValue("ATRIBUTOS", "AT" + (i++), user.getStats().attr().getBackup(attr));
				}
			}

			i = 1;
			for (Skill skill : Skill.values()) {
				ini.setValue("SKILLS", "SK" + (i++), user.skills().get(skill));
			}

			ini.setValue("CONTACTO", "Email", user.email);

			ini.setValue("INIT", "Genero", user.gender.id());
			ini.setValue("INIT", "Raza", user.race.id());
			ini.setValue("INIT", "Hogar", user.homeland.id());
			ini.setValue("INIT", "Clase", user.clazz().id());
			ini.setValue("INIT", "PasswordHash", user.passwordHash);
			ini.setValue("INIT", "Desc", user.description);
			ini.setValue("INIT", "Heading", user.infoChar().heading.value());

			if (user.getFlags().Muerto || user.getFlags().Invisible || user.getFlags().Navegando) {
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

			ini.setValue("STATS", "GLD", user.getStats().getGold());
			ini.setValue("STATS", "BANCO", user.getStats().getBankGold());

			// ini.setValue("STATS", "MET", user.getEstads().MET);
			ini.setValue("STATS", "MaxHP", user.getStats().MaxHP);
			ini.setValue("STATS", "MinHP", user.getStats().MinHP);

			// ini.setValue("STATS", "FIT", user.getEstads().FIT);
			ini.setValue("STATS", "MaxSTA", user.getStats().maxStamina);
			ini.setValue("STATS", "MinSTA", user.getStats().stamina);

			ini.setValue("STATS", "MaxMAN", user.getStats().maxMana);
			ini.setValue("STATS", "MinMAN", user.getStats().mana);

			ini.setValue("STATS", "MaxHIT", user.getStats().MaxHIT);
			ini.setValue("STATS", "MinHIT", user.getStats().MinHIT);

			ini.setValue("STATS", "MaxAGU", user.getStats().maxDrinked);
			ini.setValue("STATS", "MinAGU", user.getStats().drinked);

			ini.setValue("STATS", "MaxHAM", user.getStats().maxEaten);
			ini.setValue("STATS", "MinHAM", user.getStats().eaten);

			ini.setValue("STATS", "SkillPtsLibres", user.skills().freeSkillPts);

			ini.setValue("STATS", "EXP", user.getStats().Exp);
			ini.setValue("STATS", "ELV", user.getStats().ELV);
			ini.setValue("STATS", "ELU", user.getStats().ELU);

			ini.setValue("MUERTES", "UserMuertes", user.getStats().usuariosMatados);
			ini.setValue("MUERTES", "NpcsMuertes", user.getStats().NPCsMuertos);

			i = 1;
			for (InventoryObject invObj : user.getBankInventory()) {
				if (invObj.estaVacio()) {
					ini.setValue("BancoInventory", "Obj" + (i++), "0-0");
				} else {
					ini.setValue("BancoInventory", "Obj" + (i++), invObj.objid + "-" + invObj.cant);
				}
			}
			ini.setValue("BancoInventory", "CantidadItems", user.getBankInventory().getSize());

			i = 1;
			for (InventoryObject invObj : user.getUserInv()) {
				if (invObj.estaVacio()) {
					ini.setValue("Inventory", "Obj" + (i++), "0-0-0");
				} else {
					ini.setValue("Inventory", "Obj" + (i++), 
						invObj.objid + "-" + invObj.cant + "-" + (invObj.equipado ? 1 : 0));
				}
			}
			ini.setValue("Inventory", "CantidadItems", user.getUserInv().getSize());
			
			ini.setValue("Inventory", "WeaponEqpSlot", user.getUserInv().getArmaSlot());
			ini.setValue("Inventory", "ArmourEqpSlot", user.getUserInv().getArmaduraSlot());
			ini.setValue("Inventory", "CascoEqpSlot", user.getUserInv().getCascoSlot());
			ini.setValue("Inventory", "EscudoEqpSlot", user.getUserInv().getEscudoSlot());
			ini.setValue("Inventory", "BarcoSlot", user.getUserInv().getBarcoSlot());
			ini.setValue("Inventory", "MunicionSlot", user.getUserInv().getMunicionSlot());
			ini.setValue("Inventory", "EspadaMataDragonesSlot", user.getUserInv().getEspadaMataDragonesSlot());

			// Reputacion
			ini.setValue("REP", "Asesino", user.getReputation().asesinoRep);
			ini.setValue("REP", "Bandido", user.getReputation().bandidoRep);
			ini.setValue("REP", "Burguesia", user.getReputation().burguesRep);
			ini.setValue("REP", "Ladrones", user.getReputation().ladronRep);
			ini.setValue("REP", "Nobles", user.getReputation().nobleRep);
			ini.setValue("REP", "Plebe", user.getReputation().plebeRep);

			ini.setValue("REP", "Promedio", user.getReputation().getPromedio());

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
			ini.store(User.getPjFile(user.getUserName()));
		} catch (Exception e) {
			log.fatal(user.getUserName() + ": ERROR EN SAVEUSER()", e);
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
			ini = new IniFile(User.getPjFile(user.getUserName()));
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
		IniFile ini = new IniFile(User.getPjFile(userName));
		
		return ini.getString("CONTACTO", "Email");
	}

	public static List<String> punishments(String userName) {
		List<String> punishments = new ArrayList<>();
		IniFile ini;
		try {
			ini = new IniFile(User.getPjFile(userName));
			int count = ini.getInt("PENAS", "Cant");
			IntStream.range(1, count + 1).forEach(i -> {
				punishments.add(ini.getString("PENAS", "P" + i));
			});
		} catch (IOException ignored) {
		}
		return punishments;
	}

	public static void addPunishment(String userName, String text) {
		final String fileName = User.getPjFile(userName);
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
		final String fileName = User.getPjFile(userName);
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
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			return ini.getShort("FLAGS", "Ban") == 1;
		} catch (IOException ignored) {
		}
    	return false;
    }
    
    public static void banUser(String userName, String admin, String reason) {
		final String fileName = User.getPjFile(userName);
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
		final String fileName = User.getPjFile(userName);
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
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("CONTACTO", "Email", newEmail);    
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void updatePassword(String userName, String newPasswordHash) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "PasswordHash", newPasswordHash);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static String getGuildName(String userName) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			return ini.getString("Guild", "GuildName");
		} catch (IOException ignored) {
		}
    	return null;
    }

	public static void changeName(String userName, String newName) throws IOException {
		Path original = Paths.get(User.getPjFile(userName));
	    Path copied = Paths.get(User.getPjFile(newName));
	    Files.copy(original, copied, StandardCopyOption.COPY_ATTRIBUTES);		
	}

    public static void councilKick(String userName) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("CONSEJO", "PERTENECE", 0);
			ini.setValue("CONSEJO", "PERTENECECAOS", 0);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void addBankGold(String userName, int goldToAdd) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			int currentGold = ini.getInt("STATS", "BANCO");
			currentGold += goldToAdd;
			if (currentGold < 0) {
				currentGold = 0;
			}
			ini.setValue("STATS", "BANCO", currentGold);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void writeGold(String userName, int gold) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("STATS", "GLD", gold);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void addExp(String userName, int addedExp) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			int currentExp = ini.getInt("STATS", "EXP");
			currentExp += addedExp;
			ini.setValue("STATS", "EXP", currentExp);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }

    public static void writeLevel(String userName, int level) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("STATS", "ELV", level);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void writeCriminalsKilled(String userName, int count) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("FACCIONES", "CrimMatados", count);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void writeCitizensKilled(String userName, int count) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("FACCIONES", "CiudMatados", count);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
        
    public static void writeSkillValue(String userName, int skill, int value) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("Skills", "SK" + skill, value);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void writeFreeSkillsPoints(String userName, int value) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("STATS", "SkillPtsLibres", value);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void writeNobleReputation(String userName, int value) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("REP", "Nobles", value);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    
    public static void writeAssassinReputation(String userName, int value) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("REP", "Asesino", value);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }

	public static void writeRace(String userName, UserRace race) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "Raza", race.id());
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}
	
	public static void writeClazz(String userName, Clazz clazz) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "Clase", clazz.id());
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}
	
	public static void writeGender(String userName, UserGender gender) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "Genero", gender.id());
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}
	
	public static void writeBody(String userName, short bodyIndex) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "Body", bodyIndex);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}
	
	public static void writeHead(String userName, short headIndex) {
		final String fileName = User.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("INIT", "Head", headIndex);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
	}
	
}
