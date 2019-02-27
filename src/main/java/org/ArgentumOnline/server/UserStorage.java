package org.ArgentumOnline.server;

import static org.ArgentumOnline.server.Constants.IntervaloParalizado;
import static org.ArgentumOnline.server.Constants.NUMATRIBUTOS;
import static org.ArgentumOnline.server.Constants.NingunArma;
import static org.ArgentumOnline.server.Constants.NingunCasco;
import static org.ArgentumOnline.server.Constants.NingunEscudo;
import static org.ArgentumOnline.server.Constants.iCabezaMuerto;
import static org.ArgentumOnline.server.Constants.iCuerpoMuerto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.classes.Clazz;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.MapPos.Heading;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.util.IniFile;
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
		IniFile ini = new IniFile(Player.getPjFile(user.m_nick));
		
		loadUserInit(ini);
		loadUserStats(ini);
		if (!validateChr()) {
			user.enviarError("Error en el personaje.");
			throw new RuntimeException("Error en el personaje." + user.getNick());
		}
		loadUserReputacion(ini);
	}
	
	private boolean validateChr() {
		return user.getInfoChar().validateChr() && user.getEstads().validateSkills();
	}
	
	public String loadPasswordFromStorage(String nick) 
	throws FileNotFoundException, IOException {
		IniFile ini = new IniFile(Player.getPjFile(user.getNick()));
		
		return ini.getString("INIT", "Password");
	}

	private void loadUserInit(IniFile ini) 
	throws java.io.IOException {
		user.getFaccion().ArmadaReal = ini.getShort("FACCIONES", "EjercitoReal") == 1;
		user.getFaccion().FuerzasCaos = ini.getShort("FACCIONES", "EjercitoCaos") == 1;
		user.getFaccion().CiudadanosMatados = ini.getLong("FACCIONES", "CiudMatados");
		user.getFaccion().CriminalesMatados = ini.getLong("FACCIONES", "CrimMatados");
		user.getFaccion().RecibioArmaduraCaos = ini.getShort("FACCIONES", "rArCaos") == 1;
		user.getFaccion().RecibioArmaduraReal = ini.getShort("FACCIONES", "rArReal") == 1;
		user.getFaccion().RecibioExpInicialCaos = ini.getShort("FACCIONES", "rExCaos") == 1;
		user.getFaccion().RecibioExpInicialReal = ini.getShort("FACCIONES", "rExReal") == 1;
		user.getFaccion().RecompensasCaos = ini.getShort("FACCIONES", "recCaos");
		user.getFaccion().RecompensasReal = ini.getShort("FACCIONES", "recReal");

		user.getFlags().Muerto = ini.getShort("FLAGS", "Muerto") == 1;
		user.getFlags().Escondido = ini.getShort("FLAGS", "Escondido") == 1;
		user.getFlags().Hambre = ini.getShort("FLAGS", "Hambre") == 1;
		user.getFlags().Sed = ini.getShort("FLAGS", "Sed") == 1;
		user.getFlags().Desnudo = ini.getShort("FLAGS", "Desnudo") == 1;
		user.getFlags().Envenenado = ini.getShort("FLAGS", "Envenenado") == 1;
		user.getFlags().Paralizado = ini.getShort("FLAGS", "Paralizado") == 1;
		user.getFlags().Ban = ini.getShort("FLAGS", "Ban") == 1;
		if (user.getFlags().Paralizado) {
			user.getCounters().Paralisis = IntervaloParalizado;
		}
		user.getFlags().Navegando = ini.getShort("FLAGS", "Navegando") == 1;
		user.getCounters().Pena = ini.getLong("COUNTERS", "Pena");
		user.m_email = ini.getString("CONTACTO", "Email");
		user.m_genero = (byte) ini.getShort("INIT", "Genero");
		user.clazz = Clazz.findByName(ini.getString("INIT", "Clase").toUpperCase());
		if (user.clazz == null) {
			throw new java.io.IOException("Clase desconocida: " + ini.getString("INIT", "Clase").toUpperCase());
		}
		user.m_raza = (byte) ini.getShort("INIT", "Raza");
		user.m_hogar = (byte) ini.getShort("INIT", "Hogar");
		user.getInfoChar().m_dir = (byte)ini.getShort("INIT", "Heading");

		user.getOrigChar().m_cabeza = ini.getShort("INIT", "Head");
		user.getOrigChar().m_cuerpo = ini.getShort("INIT", "Body");
		user.getOrigChar().m_arma = ini.getShort("INIT", "Arma");
		user.getOrigChar().m_escudo = ini.getShort("INIT", "Escudo");
		user.getOrigChar().m_casco = ini.getShort("INIT", "Casco");
		user.getOrigChar().m_dir = (byte)(short)Heading.SOUTH.ordinal();

		user.m_banned_by = ini.getString("BAN", "BannedBy");
		user.m_banned_reason = ini.getString("BAN", "Reason");

		if (user.isAlive()) {
			user.getInfoChar().m_cabeza = user.getOrigChar().m_cabeza;
			user.getInfoChar().m_cuerpo = user.getOrigChar().m_cuerpo;
			user.getInfoChar().m_arma = user.getOrigChar().m_arma;
			user.getInfoChar().m_escudo = user.getOrigChar().m_escudo;
			user.getInfoChar().m_casco = user.getOrigChar().m_casco;
			user.getInfoChar().m_dir = user.getOrigChar().m_dir;
		} else {
			user.getInfoChar().m_cabeza = iCabezaMuerto;
			user.getInfoChar().m_cuerpo = iCuerpoMuerto;
			user.getInfoChar().m_arma = NingunArma;
			user.getInfoChar().m_escudo = NingunEscudo;
			user.getInfoChar().m_casco = NingunCasco;
		}

		user.m_desc = ini.getString("INIT", "Desc");
		{
			StringTokenizer st = new StringTokenizer(ini.getString("INIT", "Position"), "-");
			user.pos().set(
					Short.parseShort(st.nextToken()),  // map
					Short.parseShort(st.nextToken()),  // x
					Short.parseShort(st.nextToken())); // y
		}

		// int banco_cant = ini.getInt("BancoInventory", "CantidadItems");
		// Lista de objetos en el banco.
		for (int i = 0; i < user.getBankInventory().size(); i++) {
			String tmp = ini.getString("BancoInventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			user.getBankInventory().setObjeto(i + 1,
					new InventoryObject(Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken())));
		}

		// int cant = ini.getInt("Inventory", "CantidadItems");
		// Lista de objetos del inventario del usuario.
		for (int i = 0; i < user.getInv().size(); i++) {
			String tmp = ini.getString("Inventory", "Obj" + (i + 1));
			StringTokenizer st = new StringTokenizer(tmp, "-");
			user.getInv().setObjeto(i + 1, new InventoryObject(Short.parseShort(st.nextToken()),
					Short.parseShort(st.nextToken()), Short.parseShort(st.nextToken()) == 1));
		}
		user.getInv().setArmaSlot(ini.getShort("Inventory", "WeaponEqpSlot"));
		user.getInv().setArmaduraSlot(ini.getShort("Inventory", "ArmourEqpSlot"));
		user.getFlags().Desnudo = (user.getInv().getArmaduraSlot() == 0);
		user.getInv().setEscudoSlot(ini.getShort("Inventory", "EscudoEqpSlot"));
		user.getInv().setCascoSlot(ini.getShort("Inventory", "CascoEqpSlot"));
		user.getInv().setBarcoSlot(ini.getShort("Inventory", "BarcoSlot"));
		user.getInv().setMunicionSlot(ini.getShort("Inventory", "MunicionSlot"));
		user.getInv().setHerramientaSlot(ini.getShort("Inventory", "HerramientaSlot"));

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

		user.m_quest.m_nroQuest = ini.getShort("QUEST", "NroQuest");
		user.m_quest.m_recompensa = ini.getShort("QUEST", "Recompensa");
		user.m_quest.m_enQuest = (ini.getShort("QUEST", "EnQuest") == 1);
		user.m_quest.m_realizoQuest = (ini.getShort("QUEST", "RealizoQuest") == 1);
	}

	private void loadUserStats(IniFile ini) {
		for (int i = 0; i < NUMATRIBUTOS; i++) {
			user.getEstads().userAttributes[i] = (byte) ini.getShort("ATRIBUTOS", "AT" + (i + 1));
			user.getEstads().userAttributesBackup[i] = user.getEstads().userAttributes[i];
		}

		for (int i = 1; i < user.getEstads().userSkills.length; i++) {
			user.getEstads().userSkills[i] = (byte) ini.getShort("SKILLS", "SK" + i);
		}

		for (int slot = 1; slot <= user.m_spells.getCount(); slot++) {
			user.m_spells.setSpell(slot, ini.getShort("HECHIZOS", "H" + slot));
		}

		user.getEstads().setGold(ini.getInt("STATS", "GLD"));
		user.getEstads().setBankGold(ini.getInt("STATS", "BANCO"));

		user.getEstads().MaxHP = ini.getInt("STATS", "MaxHP");
		user.getEstads().MinHP = ini.getInt("STATS", "MinHP");

		user.getEstads().stamina = ini.getInt("STATS", "MinSTA");
		user.getEstads().maxStamina = ini.getInt("STATS", "MaxSTA");

		user.getEstads().maxMana = ini.getInt("STATS", "MaxMAN");
		user.getEstads().mana = ini.getInt("STATS", "MinMAN");

		user.getEstads().MaxHIT = ini.getInt("STATS", "MaxHIT");
		user.getEstads().MinHIT = ini.getInt("STATS", "MinHIT");

		user.getEstads().maxDrinked = ini.getInt("STATS", "MaxAGU");
		user.getEstads().drinked = ini.getInt("STATS", "MinAGU");

		user.getEstads().maxEaten = ini.getInt("STATS", "MaxHAM");
		user.getEstads().eaten = ini.getInt("STATS", "MinHAM");

		user.getEstads().SkillPts = ini.getInt("STATS", "SkillPtsLibres");

		user.getEstads().Exp = ini.getInt("STATS", "EXP");
		user.getEstads().ELU = ini.getInt("STATS", "ELU");
		user.getEstads().ELV = ini.getInt("STATS", "ELV");

		user.getEstads().usuariosMatados = ini.getInt("MUERTES", "UserMuertes");
		// user.getEstads().criminalesMatados = ini.getInt("MUERTES",
		// "CrimMuertes");
		user.getEstads().NPCsMuertos = ini.getInt("MUERTES", "NpcsMuertes");
	}

	private void loadUserReputacion(IniFile ini) {
		user.getReputacion().asesinoRep = ini.getDouble("REP", "Asesino");
		user.getReputacion().bandidoRep = ini.getDouble("REP", "Bandido");
		user.getReputacion().burguesRep = ini.getDouble("REP", "Burguesia");
		user.getReputacion().ladronRep = ini.getDouble("REP", "Ladrones");
		user.getReputacion().nobleRep = ini.getDouble("REP", "Nobles");
		user.getReputacion().plebeRep = ini.getDouble("REP", "Plebe");
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
			ini.setValue("FLAGS", "Ban", user.getFlags().Ban);
			ini.setValue("FLAGS", "Navegando", user.getFlags().Navegando);
			ini.setValue("FLAGS", "Envenenado", user.getFlags().Envenenado);
			ini.setValue("FLAGS", "Paralizado", user.getFlags().Paralizado);

			ini.setValue("COUNTERS", "Pena", user.getCounters().Pena);

			ini.setValue("FACCIONES", "EjercitoReal", user.getFaccion().ArmadaReal);
			ini.setValue("FACCIONES", "EjercitoCaos", user.getFaccion().FuerzasCaos);
			ini.setValue("FACCIONES", "CiudMatados", user.getFaccion().CiudadanosMatados);
			ini.setValue("FACCIONES", "CrimMatados", user.getFaccion().CriminalesMatados);
			ini.setValue("FACCIONES", "rArCaos", user.getFaccion().RecibioArmaduraCaos);
			ini.setValue("FACCIONES", "rArReal", user.getFaccion().RecibioArmaduraReal);
			ini.setValue("FACCIONES", "rExCaos", user.getFaccion().RecibioExpInicialCaos);
			ini.setValue("FACCIONES", "rExReal", user.getFaccion().RecibioExpInicialReal);
			ini.setValue("FACCIONES", "recCaos", user.getFaccion().RecompensasCaos);
			ini.setValue("FACCIONES", "recReal", user.getFaccion().RecompensasReal);

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

			ini.setValue("QUEST", "NroQuest", user.m_quest.m_nroQuest);
			ini.setValue("QUEST", "EnQuest", user.m_quest.m_enQuest);
			ini.setValue("QUEST", "RealizoQuest", user.m_quest.m_realizoQuest);
			ini.setValue("QUEST", "Recompensa", user.m_quest.m_recompensa);

			ini.setValue("BAN", "BannedBy", user.m_banned_by);
			ini.setValue("BAN", "Reason", user.m_banned_reason);

			// ¿Fueron modificados los atributos del usuario?
			if (!user.getFlags().TomoPocion) {
				for (int i = 0; i < user.getEstads().userAttributes.length; i++) {
					ini.setValue("ATRIBUTOS", "AT" + (i + 1), user.getEstads().userAttributes[i]);
				}
			} else {
				for (int i = 0; i < user.getEstads().userAttributes.length; i++) {
					ini.setValue("ATRIBUTOS", "AT" + (i + 1), user.getEstads().userAttributesBackup[i]);
				}
			}

			for (int i = 1; i < user.getEstads().userSkills.length; i++) {
				ini.setValue("SKILLS", "SK" + i, user.getEstads().userSkills[i]);
			}

			ini.setValue("CONTACTO", "Email", user.m_email);

			ini.setValue("INIT", "Genero", user.m_genero);
			ini.setValue("INIT", "Raza", user.m_raza);
			ini.setValue("INIT", "Hogar", user.m_hogar);
			ini.setValue("INIT", "Clase", user.getClazz().clazz().getName());
			ini.setValue("INIT", "Password", user.m_password);
			ini.setValue("INIT", "Desc", user.m_desc);
			ini.setValue("INIT", "Heading", user.getInfoChar().m_dir);

			if (user.getFlags().Muerto || user.getFlags().Invisible || user.getFlags().Navegando) {
				ini.setValue("INIT", "Head", user.getOrigChar().m_cabeza);
				ini.setValue("INIT", "Body", user.getOrigChar().m_cuerpo);
			} else {
				ini.setValue("INIT", "Head", user.getInfoChar().m_cabeza);
				ini.setValue("INIT", "Body", user.getInfoChar().m_cuerpo);
			}

			ini.setValue("INIT", "Arma", user.getInfoChar().m_arma);
			ini.setValue("INIT", "Escudo", user.getInfoChar().m_escudo);
			ini.setValue("INIT", "Casco", user.getInfoChar().m_casco);
			ini.setValue("INIT", "LastIP", user.getIP());
			ini.setValue("INIT", "Position", user.pos().map + "-" + user.pos().x + "-" + user.pos().y);

			ini.setValue("STATS", "GLD", user.getEstads().getGold());
			ini.setValue("STATS", "BANCO", user.getEstads().getBankGold());

			// ini.setValue("STATS", "MET", user.getEstads().MET);
			ini.setValue("STATS", "MaxHP", user.getEstads().MaxHP);
			ini.setValue("STATS", "MinHP", user.getEstads().MinHP);

			// ini.setValue("STATS", "FIT", user.getEstads().FIT);
			ini.setValue("STATS", "MaxSTA", user.getEstads().maxStamina);
			ini.setValue("STATS", "MinSTA", user.getEstads().stamina);

			ini.setValue("STATS", "MaxMAN", user.getEstads().maxMana);
			ini.setValue("STATS", "MinMAN", user.getEstads().mana);

			ini.setValue("STATS", "MaxHIT", user.getEstads().MaxHIT);
			ini.setValue("STATS", "MinHIT", user.getEstads().MinHIT);

			ini.setValue("STATS", "MaxAGU", user.getEstads().maxDrinked);
			ini.setValue("STATS", "MinAGU", user.getEstads().drinked);

			ini.setValue("STATS", "MaxHAM", user.getEstads().maxEaten);
			ini.setValue("STATS", "MinHAM", user.getEstads().eaten);

			ini.setValue("STATS", "SkillPtsLibres", user.getEstads().SkillPts);

			ini.setValue("STATS", "EXP", user.getEstads().Exp);
			ini.setValue("STATS", "ELV", user.getEstads().ELV);
			ini.setValue("STATS", "ELU", user.getEstads().ELU);

			ini.setValue("MUERTES", "UserMuertes", user.getEstads().usuariosMatados);
			// ini.setValue("MUERTES", "CrimMuertes",
			// user.getEstads().criminalesMatados);
			ini.setValue("MUERTES", "NpcsMuertes", user.getEstads().NPCsMuertos);

			int cant = user.getBankInventory().size();
			for (int i = 0; i < user.getBankInventory().size(); i++) {
				if (user.getBankInventory().getObjeto(i + 1) == null) {
					ini.setValue("BancoInventory", "Obj" + (i + 1), "0-0");
					cant--;
				} else {
					ini.setValue("BancoInventory", "Obj" + (i + 1),
							user.getBankInventory().getObjeto(i + 1).objid + "-" + user.getBankInventory().getObjeto(i + 1).cant);
				}
			}
			ini.setValue("BancoInventory", "CantidadItems", cant);

			cant = user.getInv().size();
			for (int i = 0; i < user.getInv().size(); i++) {
				if (user.getInv().getObjeto(i + 1) == null) {
					ini.setValue("Inventory", "Obj" + (i + 1), "0-0-0");
					cant--;
				} else {
					ini.setValue("Inventory", "Obj" + (i + 1), user.getInv().getObjeto(i + 1).objid + "-"
							+ user.getInv().getObjeto(i + 1).cant + "-" + (user.getInv().getObjeto(i + 1).equipado ? 1 : 0));
				}
			}
			ini.setValue("Inventory", "CantidadItems", cant);
			ini.setValue("Inventory", "WeaponEqpSlot", user.getInv().getArmaSlot());
			ini.setValue("Inventory", "ArmourEqpSlot", user.getInv().getArmaduraSlot());
			ini.setValue("Inventory", "CascoEqpSlot", user.getInv().getCascoSlot());
			ini.setValue("Inventory", "EscudoEqpSlot", user.getInv().getEscudoSlot());
			ini.setValue("Inventory", "BarcoSlot", user.getInv().getBarcoSlot());
			ini.setValue("Inventory", "MunicionSlot", user.getInv().getMunicionSlot());
			ini.setValue("Inventory", "HerramientaSlot", user.getInv().getHerramientaSlot());
			ini.setValue("Inventory", "EspadaMataDragonesSlot", user.getInv().getEspadaMataDragonesSlot());

			// Reputacion
			ini.setValue("REP", "Asesino", user.getReputacion().asesinoRep);
			ini.setValue("REP", "Bandido", user.getReputacion().bandidoRep);
			ini.setValue("REP", "Burguesia", user.getReputacion().burguesRep);
			ini.setValue("REP", "Ladrones", user.getReputacion().ladronRep);
			ini.setValue("REP", "Nobles", user.getReputacion().nobleRep);
			ini.setValue("REP", "Plebe", user.getReputacion().plebeRep);

			ini.setValue("REP", "Promedio", user.getReputacion().getPromedio());

			for (int slot = 1; slot <= user.m_spells.getCount(); slot++) {
				ini.setValue("HECHIZOS", "H" + slot, user.m_spells.getSpell(slot));
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
				user.getInfoChar().m_cabeza = iCabezaMuerto;
			}

			// Guardar todo
			ini.store(Player.getPjFile(user.m_nick));
		} catch (Exception e) {
			log.fatal(user.getNick() + ": ERROR EN SAVEUSER()", e);
		}
	}
	
}
