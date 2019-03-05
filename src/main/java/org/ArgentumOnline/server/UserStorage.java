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
package org.ArgentumOnline.server;

import static org.ArgentumOnline.server.Constants.IntervaloParalizado;
import static org.ArgentumOnline.server.Constants.NingunArma;
import static org.ArgentumOnline.server.Constants.NingunCasco;
import static org.ArgentumOnline.server.Constants.NingunEscudo;
import static org.ArgentumOnline.server.Constants.OBJ_INDEX_CABEZA_MUERTO;
import static org.ArgentumOnline.server.Constants.OBJ_INDEX_CUERPO_MUERTO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.MapPos.Heading;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.protocol.CharacterInfoResponse;
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
	void loadUserFromStorage() 
	throws IOException {
		IniFile ini = new IniFile(Player.getPjFile(user.userName));
		
		loadUserInit(ini);
		loadUserStats(ini);
		if (!validateChr()) {
			user.sendError("Error en el personaje.");
			throw new RuntimeException("Error en el personaje." + user.getNick());
		}
		loadUserReputacion(ini, user.reputation());
	}
	
	private boolean validateChr() {
		return user.infoChar().validateChr() && user.skills().validateSkills();
	}
	
	public String loadPasswordFromStorage(String nick) 
	throws FileNotFoundException, IOException {
		IniFile ini = new IniFile(Player.getPjFile(user.getNick()));
		
		return ini.getString("INIT", "Password");
	}

	private void loadUserInit(IniFile ini) 
	throws java.io.IOException {
		user.userFaction().ArmadaReal = ini.getShort("FACCIONES", "EjercitoReal") == 1;
		user.userFaction().FuerzasCaos = ini.getShort("FACCIONES", "EjercitoCaos") == 1;
		user.userFaction().CiudadanosMatados = ini.getLong("FACCIONES", "CiudMatados");
		user.userFaction().CriminalesMatados = ini.getLong("FACCIONES", "CrimMatados");
		user.userFaction().RecibioArmaduraCaos = ini.getShort("FACCIONES", "rArCaos") == 1;
		user.userFaction().RecibioArmaduraReal = ini.getShort("FACCIONES", "rArReal") == 1;
		user.userFaction().RecibioExpInicialCaos = ini.getShort("FACCIONES", "rExCaos") == 1;
		user.userFaction().RecibioExpInicialReal = ini.getShort("FACCIONES", "rExReal") == 1;
		user.userFaction().RecompensasCaos = ini.getShort("FACCIONES", "recCaos");
		user.userFaction().RecompensasReal = ini.getShort("FACCIONES", "recReal");

		user.flags().Muerto = ini.getShort("FLAGS", "Muerto") == 1;
		user.flags().Escondido = ini.getShort("FLAGS", "Escondido") == 1;
		user.flags().Hambre = ini.getShort("FLAGS", "Hambre") == 1;
		user.flags().Sed = ini.getShort("FLAGS", "Sed") == 1;
		user.flags().Desnudo = ini.getShort("FLAGS", "Desnudo") == 1;
		user.flags().Envenenado = ini.getShort("FLAGS", "Envenenado") == 1;
		user.flags().Paralizado = ini.getShort("FLAGS", "Paralizado") == 1;
		user.flags().Ban = ini.getShort("FLAGS", "Ban") == 1;
		if (user.flags().Paralizado) {
			user.counters().Paralisis = IntervaloParalizado;
		}
		user.flags().Navegando = ini.getShort("FLAGS", "Navegando") == 1;
		user.counters().Pena = ini.getLong("COUNTERS", "Pena");
		user.email = ini.getString("CONTACTO", "Email");
		user.gender = UserGender.value(ini.getShort("INIT", "Genero"));
		user.clazz = Clazz.value(ini.getInt("INIT", "Clase"));
		if (user.clazz == null) {
			throw new java.io.IOException("Clase desconocida: " + ini.getString("INIT", "Clase").toUpperCase());
		}
		user.race = UserRace.value(ini.getShort("INIT", "Raza"));
		user.homeland = (byte) ini.getShort("INIT", "Hogar");
		user.infoChar().heading = (byte)ini.getShort("INIT", "Heading");

		user.origChar().head = ini.getShort("INIT", "Head");
		user.origChar().body = ini.getShort("INIT", "Body");
		user.origChar().weapon = ini.getShort("INIT", "Arma");
		user.origChar().shield = ini.getShort("INIT", "Escudo");
		user.origChar().helmet = ini.getShort("INIT", "Casco");
		user.origChar().heading = (byte)(short)Heading.SOUTH.ordinal();

		user.bannedBy = ini.getString("BAN", "BannedBy");
		user.bannedReason = ini.getString("BAN", "Reason");

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
		user.flags().Desnudo = (user.userInv().getArmaduraSlot() == 0);
		user.userInv().setEscudoSlot(ini.getShort("Inventory", "EscudoEqpSlot"));
		user.userInv().setCascoSlot(ini.getShort("Inventory", "CascoEqpSlot"));
		user.userInv().setBarcoSlot(ini.getShort("Inventory", "BarcoSlot"));
		user.userInv().setMunicionSlot(ini.getShort("Inventory", "MunicionSlot"));

		int cantMascotas = ini.getShort("Mascotas", "NroMascotas");
		// Lista de mascotas.
		for (int i = 1; i <= cantMascotas; i++) {
			short npcNumber = ini.getShort("Mascotas", "Mas" + i);
			Npc pet = server.createNpc(npcNumber);
			user.getUserPets().addPet(pet);
		}

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
	
	public static CharacterInfoResponse createCharacterInfoResponse(String userName) {
		// ¿Existe el personaje?
		if (!Util.existeArchivo(Player.getPjFile(userName))) {
			return null;
		}
		try {
			IniFile ini = new IniFile(Player.getPjFile(userName));
			
			Reputation tmpReputation = new Reputation();
			loadUserReputacion(ini, tmpReputation);
			
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

	private void loadUserStats(IniFile ini) {
		
		int i = 1;
		for (Attribute attr : Attribute.values()) {
			user.stats().attr().set(attr, ini.getShort("ATRIBUTOS", "AT" + (i++)));
		}
		user.stats().attr().backupAttributes();

		i = 1;
		for (Skill skill : Skill.values()) {
			user.skills().set(skill, ini.getShort("SKILLS", "SK" + (i++)));
		}
		user.skills().SkillPts = ini.getInt("STATS", "SkillPtsLibres");

		user.stats().Exp = ini.getInt("STATS", "EXP");
		user.stats().ELU = ini.getInt("STATS", "ELU");
		user.stats().ELV = ini.getInt("STATS", "ELV");

		for (int slot = 1; slot <= user.spells().getCount(); slot++) {
			user.spells().setSpell(slot, ini.getShort("HECHIZOS", "H" + slot));
		}

		user.stats().setGold(ini.getInt("STATS", "GLD"));
		user.stats().setBankGold(ini.getInt("STATS", "BANCO"));

		user.stats().MaxHP = ini.getInt("STATS", "MaxHP");
		user.stats().MinHP = ini.getInt("STATS", "MinHP");

		user.stats().stamina = ini.getInt("STATS", "MinSTA");
		user.stats().maxStamina = ini.getInt("STATS", "MaxSTA");

		user.stats().maxMana = ini.getInt("STATS", "MaxMAN");
		user.stats().mana = ini.getInt("STATS", "MinMAN");

		user.stats().MaxHIT = ini.getInt("STATS", "MaxHIT");
		user.stats().MinHIT = ini.getInt("STATS", "MinHIT");

		user.stats().maxDrinked = ini.getInt("STATS", "MaxAGU");
		user.stats().drinked = ini.getInt("STATS", "MinAGU");

		user.stats().maxEaten = ini.getInt("STATS", "MaxHAM");
		user.stats().eaten = ini.getInt("STATS", "MinHAM");

		user.stats().usuariosMatados = ini.getInt("MUERTES", "UserMuertes");
		user.stats().NPCsMuertos = ini.getInt("MUERTES", "NpcsMuertes");
	}

	private static void loadUserReputacion(IniFile ini, Reputation reputation) {
		reputation.asesinoRep = ini.getDouble("REP", "Asesino");
		reputation.bandidoRep = ini.getDouble("REP", "Bandido");
		reputation.burguesRep = ini.getDouble("REP", "Burguesia");
		reputation.ladronRep = ini.getDouble("REP", "Ladrones");
		reputation.nobleRep = ini.getDouble("REP", "Nobles");
		reputation.plebeRep = ini.getDouble("REP", "Plebe");
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
			ini.setValue("FLAGS", "Ban", user.flags().Ban);
			ini.setValue("FLAGS", "Navegando", user.flags().Navegando);
			ini.setValue("FLAGS", "Envenenado", user.flags().Envenenado);
			ini.setValue("FLAGS", "Paralizado", user.flags().Paralizado);

			ini.setValue("COUNTERS", "Pena", user.counters().Pena);

			ini.setValue("FACCIONES", "EjercitoReal", user.userFaction().ArmadaReal);
			ini.setValue("FACCIONES", "EjercitoCaos", user.userFaction().FuerzasCaos);
			ini.setValue("FACCIONES", "CiudMatados", user.userFaction().CiudadanosMatados);
			ini.setValue("FACCIONES", "CrimMatados", user.userFaction().CriminalesMatados);
			ini.setValue("FACCIONES", "rArCaos", user.userFaction().RecibioArmaduraCaos);
			ini.setValue("FACCIONES", "rArReal", user.userFaction().RecibioArmaduraReal);
			ini.setValue("FACCIONES", "rExCaos", user.userFaction().RecibioExpInicialCaos);
			ini.setValue("FACCIONES", "rExReal", user.userFaction().RecibioExpInicialReal);
			ini.setValue("FACCIONES", "recCaos", user.userFaction().RecompensasCaos);
			ini.setValue("FACCIONES", "recReal", user.userFaction().RecompensasReal);

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
			ini.setValue("INIT", "Hogar", user.homeland);
			ini.setValue("INIT", "Clase", user.clazz().id());
			ini.setValue("INIT", "Password", user.password);
			ini.setValue("INIT", "Desc", user.description);
			ini.setValue("INIT", "Heading", user.infoChar().heading);

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

			ini.setValue("STATS", "SkillPtsLibres", user.skills().SkillPts);

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
					ini.setValue("MASCOTAS", "MAS" + (++savedCount), pet.getNumero());
				}
			}
			ini.setValue("MASCOTAS", "NroMascotas", savedCount);

			// Devuelve el head de muerto
			if (!user.isAlive()) {
				user.infoChar().head = OBJ_INDEX_CABEZA_MUERTO;
			}

			// Guardar todo
			ini.store(Player.getPjFile(user.userName));
		} catch (Exception e) {
			log.fatal(user.getNick() + ": ERROR EN SAVEUSER()", e);
		}
	}
	
}
