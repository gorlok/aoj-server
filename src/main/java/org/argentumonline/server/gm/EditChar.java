package org.argentumonline.server.gm;

import java.util.EnumSet;

import org.argentumonline.server.Clazz;
import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.Skill;
import org.argentumonline.server.protocol.UpdateExpResponse;
import org.argentumonline.server.protocol.UpdateGoldResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserGender;
import org.argentumonline.server.user.UserRace;
import org.argentumonline.server.user.UserStorage;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Log;

public class EditChar {

	enum EditCharOptions {
		NONE,
		
	    Gold,
	    Experience,
	    Body,
	    Head,
	    CiticensKilled,
	    CriminalsKilled,
	    Level,
	    Class,
	    Skills,
	    SkillPointsLeft,
	    Nobleza,
	    Asesino,
	    Sex,
	    Raza,
	    AddGold;
		
		private static EditCharOptions[] VALUES = EditCharOptions.values();
		
		public static EditCharOptions value(int index) {
			return VALUES[index];
		}
	}
	
	public static void handleEditCharacter(GameServer server, User admin, String userName, byte action, String param1, String param2) {
		// MODIFICA CARACTER
		// Comando /MOD
		Log.logGM(admin.getUserName(), "/MOD " + userName + " " + action + " " + param1 + " " + param2);
		if ("".equals(userName)) {
			admin.sendMessage("Parámetros inválidos!", FontType.FONTTYPE_INFO);
			return;
		}
		userName = userName.replace("+", " ");
		
		User user = "YO".equalsIgnoreCase(userName) ? admin : server.userByName(userName);
		
		EditCharOptions option = EditCharOptions.value(action);
		
		boolean valid = false;
		if (admin.isRoleMaster()) {
			if (admin.isCounselor()) {
				// Los RMs consejeros sólo se pueden editar su head, body y level
				valid = (user == admin) && 
						EnumSet.of(EditCharOptions.Body, EditCharOptions.Head, EditCharOptions.Level).contains(option);
			} else if (admin.isDemiGod()) {
				// Los RMs sólo se pueden editar su level y el head y body de cualquiera
				valid = (option == EditCharOptions.Level && user == admin) ||
						EnumSet.of(EditCharOptions.Body, EditCharOptions.Head).contains(option);
			} else if (admin.isGod()) {
				// Los DRMs pueden aplicar los siguientes comandos sobre cualquiera
				// pero si quiere modificar el level sólo lo puede hacer sobre sí mismo
				valid = (option == EditCharOptions.Level && user == admin) ||
						EnumSet.of(EditCharOptions.Body, EditCharOptions.Head, EditCharOptions.CiticensKilled, 
								EditCharOptions.CriminalsKilled, EditCharOptions.Class, EditCharOptions.Skills, 
								EditCharOptions.AddGold).contains(option);
			}
		} else if (admin.isGod() || admin.isAdmin()) {
			valid = true;
		}
		
		if (!valid) {
			return;
		}

		if (!User.userExists(userName)) {
            admin.sendMessage("Esta intentando editar un usuario inexistente.", FontType.FONTTYPE_INFO);
            Log.logGM(admin.getUserName(), "Intento editar un usuario inexistente.");
            return;
		}
		
        // For making the Log
		StringBuilder commandString = new StringBuilder();
        commandString.append("/MOD ");
		
		switch (option) {
		case AddGold:
			int bankGoldToAdd = Integer.valueOf(param1);
			if (bankGoldToAdd > Constants.MAX_ORO_EDIT) {
				admin.sendMessage("No está permitido utilizar valores mayores a " + Constants.MAX_ORO_EDIT + ".", 
						FontType.FONTTYPE_INFO);
			} else {
				if (user == null) {
					UserStorage.addBankGold(userName, bankGoldToAdd);
					
					admin.sendMessage("Se le ha agregado " + bankGoldToAdd + " monedas de oro a " + userName + ".", 
							FontType.FONTTYPE_TALK);
				} else {
					user.getStats().addBankGold(bankGoldToAdd);
					user.sendMessage(Constants.STANDARD_BOUNTY_HUNTER_MESSAGE, FontType.FONTTYPE_TALK);
				}
			}
            // Log it
            commandString.append("AGREGAR_ORO_BANCO ");
			break;
			
		case Gold:
			int gold = Integer.valueOf(param1);
			if (gold > Constants.MAX_ORO_EDIT) {
				admin.sendMessage("No está permitido utilizar valores mayores a " + Constants.MAX_ORO_EDIT + ".", 
						FontType.FONTTYPE_INFO);
			} else {
				if (user == null) {
					// User offline
					UserStorage.writeGold(userName, gold);
					admin.sendMessage("Usuario desconectado. Se ha asignado " + gold + " de ORO a la billetera de " 
							+ userName, FontType.FONTTYPE_INFO);
				} else {
					// User online
					user.getStats().setGold(gold);
					user.sendPacket(new UpdateGoldResponse(gold));
					admin.sendMessage("Usuario conectado. Se ha asignado " + gold + " de ORO a la billetera de " 
							+ user.getUserName(), FontType.FONTTYPE_INFO);
				}
			}
            // Log it
            commandString.append("ASIGNAR_ORO ");
			break;
			
		case Experience:
			int exp = Integer.valueOf(param1);
            if (exp > 20_000_000) {
                exp = 20_000_000;
            }
            if (user == null) {
            	// user Offline
            	UserStorage.addExp(userName, exp);
				admin.sendMessage("Usuario desconectado. Se ha agregado " + exp + " de EXP a " + userName , 
						FontType.FONTTYPE_INFO);
            } else {
            	// User online
            	user.getStats().addExp(exp);
            	user.checkUserLevel();
            	user.sendPacket(new UpdateExpResponse(user.getStats().Exp));
            }
            // Log it
            commandString.append("EXP ");
			break;
			
		case Level:
			int level = Integer.valueOf(param1);
            if (level > Constants.STAT_MAXELV) {
                level = Constants.STAT_MAXELV;
                admin.sendMessage("No puedes tener un nivel superior a " + Constants.STAT_MAXELV + ".", FontType.FONTTYPE_INFO);
            }
            // Chequeamos si puede permanecer en el clan
            if (level >= 25) {
            	// FIXME
//                Dim GI As Integer
//                If tUser <= 0 Then
//                    GI = GetVar(UserCharPath, "GUILD", "GUILDINDEX")
//                Else
//                    GI = UserList(tUser).GuildIndex
//                End If
//                
//                If GI > 0 Then
//                    If modGuilds.GuildAlignment(GI) = "Legión oscura" Or modGuilds.GuildAlignment(GI) = "Armada Real" Then
//                        'We get here, so guild has factionary alignment, we have to expulse the user
//                        Call modGuilds.m_EcharMiembroDeClan(-1, UserName)
//                        
//                        Call SendData(SendTarget.ToGuildMembers, GI, PrepareMessageConsoleMsg(UserName & " deja el clan.", FontTypeNames.FONTTYPE_GUILD))
//                        ' Si esta online le avisamos
//                        If tUser > 0 Then _
//                            Call WriteConsoleMsg(tUser, "¡Ya tienes la madurez suficiente como para decidir bajo que estandarte pelearás! Por esta razón, hasta tanto no te enlistes en la Facción bajo la cual tu clan está alineado, estarás excluído del mismo.", FontTypeNames.FONTTYPE_GUILD)
//                    End If
//                End If
            }
            if (user == null) {
            	// User Offline
            	UserStorage.writeLevel(userName, level);
            	admin.sendMessage("Usuario desconectado. Ahora " + userName + " tiene el Nivel " + level,
            			FontType.FONTTYPE_INFO);
            } else {
            	// User Online
            	user.getStats().ELV = level;
            	user.sendUpdateUserStats();
            }
            // Log it
            commandString.append("LEVEL ");
			break;
			
		case CriminalsKilled:
			int criminalsKilled = Integer.valueOf(param1);
			if (criminalsKilled > Constants.MAX_USER_KILLED) {
				criminalsKilled = Constants.MAX_USER_KILLED;
			}
            if (user == null) {
            	// User Offline
            	UserStorage.writeCriminalsKilled(userName, criminalsKilled);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene " 
            			+ criminalsKilled + " ciminales matados", FontType.FONTTYPE_INFO);
            } else {
            	// User Online
            	user.userFaction().criminalsKilled = criminalsKilled;
            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene " 
            			+ criminalsKilled + " ciminales matados", FontType.FONTTYPE_INFO);
            }
            // Log it
    		commandString.append("CRIMINAL ");
			break;
			
		case CiticensKilled:
			int citizensKilled = Integer.valueOf(param1);
			if (citizensKilled > Constants.MAX_USER_KILLED) {
				citizensKilled = Constants.MAX_USER_KILLED;
			}
            if (user == null) {
            	// User Offline
            	UserStorage.writeCitizensKilled(userName, citizensKilled);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene " 
            			+ citizensKilled + " ciudadanos matados", FontType.FONTTYPE_INFO);
            } else {
            	// User Online
            	user.userFaction().citizensKilled = citizensKilled;
            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene " 
            			+ citizensKilled + " ciudadanos matados", FontType.FONTTYPE_INFO);
            }
            // Log it
    		commandString.append("CIUDADANO ");
			break;
			
		case Skills:
			String skillName = param1.replace("+", " ");
			int skillValue = Integer.valueOf(param2);
			Skill skill = Skill.byName(skillName);
			if (skill == null) {
				admin.sendMessage("Skill inexistente!", FontType.FONTTYPE_INFO);
			} else {
				if (user == null) {
					// User Offline
					UserStorage.writeSkillValue(userName, skill.value(), skillValue);
	            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene el skill " 
	            			+ skill.name() + " en " + skillValue, FontType.FONTTYPE_INFO);
				} else {
					// User Online
					user.skills().set(skill, skillValue);
					user.sendSkills();
	            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene el skill " 
	            			+ skill.name() + " en " + skillValue, FontType.FONTTYPE_INFO);
				}
			}
            // Log it
    		commandString.append("SKILLS ");
			break;
			
		case SkillPointsLeft:
			int freeSkillPoints = Integer.valueOf(param1);
            if (user == null) {
            	// User Offline
            	UserStorage.writeFreeSkillsPoints(userName, freeSkillPoints);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene " 
            			+ freeSkillPoints + " SkillPoints libres.", FontType.FONTTYPE_INFO);
            } else {
            	// User Online
            	user.skills().freeSkillPts = freeSkillPoints;
            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene " 
            			+ freeSkillPoints + " SkillPoints libres.", FontType.FONTTYPE_INFO);
            }
            // Log it
    		commandString.append("SKILLSLIBRES ");
			break;
			
		case Nobleza:
			int nobleRep = Integer.valueOf(param1);
			if (nobleRep > Constants.MAXREP) {
				nobleRep = Constants.MAXREP;
			}
            if (user == null) {
            	// User Offline
            	UserStorage.writeNobleReputation(userName, nobleRep);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene " 
            			+ nobleRep + " puntos de Nobleza.", FontType.FONTTYPE_INFO);
            } else {
            	// User Online
            	user.getReputation().setNobleRep(nobleRep);
            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene " 
            			+ nobleRep + " puntos de Nobleza.", FontType.FONTTYPE_INFO);
            }
            // Log it
    		commandString.append("NOBLE ");
			break;
			
		case Asesino:
			int assassinRep = Integer.valueOf(param1);
			if (assassinRep > Constants.MAXREP) {
				assassinRep = Constants.MAXREP;
			}
            if (user == null) {
            	// User Offline
            	UserStorage.writeAssassinReputation(userName, assassinRep);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene " 
            			+ assassinRep + " puntos de Asesino.", FontType.FONTTYPE_INFO);
            } else {
            	// User Online
            	user.getReputation().setAsesinoRep(assassinRep);
            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene " 
            			+ assassinRep + " puntos de Asesino.", FontType.FONTTYPE_INFO);
            }
            // Log it
    		commandString.append("ASESINO ");
			break;
			
		case Raza:
			UserRace race = UserRace.byName(param1);
			if (race == null) {
				admin.sendMessage("Raza desconocida.", FontType.FONTTYPE_INFO);
			} else {
				if (user == null) {
	            	UserStorage.writeRace(userName, race);
	            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene la raza " 
	            			+ race.name(), FontType.FONTTYPE_INFO);
				} else {
					user.setRace(race);
	            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene la raza " 
	            			+ race.name(), FontType.FONTTYPE_INFO);
				}
			}
            // Log it
    		commandString.append("RAZA ");
			break;
			
		case Class:
			Clazz userClass = Clazz.byName(param1);
			if (userClass == null) {
				admin.sendMessage("Clase desconocida.", FontType.FONTTYPE_INFO);
			} else {
				if (user == null) {
					UserStorage.writeClazz(userName, userClass);
	            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene la clase " 
	            			+ userClass.name(), FontType.FONTTYPE_INFO);
				} else {
					user.setClazz(userClass);
	            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene la clase " 
	            			+ userClass.name(), FontType.FONTTYPE_INFO);
				}
			}
            // Log it
            commandString.append("CLASE ");
			break;
			
		case Sex:
			UserGender gender = UserGender.fromString(param1);
			
			if (gender == null) {
				admin.sendMessage("Genero desconocido. Intente nuevamente.", FontType.FONTTYPE_INFO);
			} else {
				if (user == null) {
					UserStorage.writeGender(userName, gender);
	            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene el género " 
	            			+ gender.getName(), FontType.FONTTYPE_INFO);
				} else {
					user.setGender(gender);
	            	admin.sendMessage("Usuario conectado: " + user.getUserName() + " ahora tiene el género " 
	            			+ gender.getName(), FontType.FONTTYPE_INFO);
				}
			}
            // Log it
            commandString.append("GENERO ");
			break;
			
		case Body:
			short bodyIndex = Short.valueOf(param1);
			if (user == null) {
				UserStorage.writeBody(userName, bodyIndex);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene el cuerpo " + bodyIndex, FontType.FONTTYPE_INFO);
			} else {
				user.infoChar().body = bodyIndex;
				user.sendCharacterChange();
			}
            // Log it
            commandString.append("BODY ");
			break;
			
		case Head:
			short headIndex = Short.valueOf(param1);
			if (user == null) {
				UserStorage.writeHead(userName, headIndex);
            	admin.sendMessage("Usuario desconectado: " + userName + " ahora tiene la cabeza " + headIndex, FontType.FONTTYPE_INFO);
			} else {
				user.infoChar().head = headIndex;
				user.sendCharacterChange();
			}
            // Log it
            commandString.append("HEAD ");
			break;
			
		default:
			admin.sendMessage("Comando no permitido.", FontType.FONTTYPE_INFO);
			commandString.append("UNKOWN ");
			break;
		}
		
		commandString
			.append(param1)
			.append(" ").append(param2)
			.append(" ").append(userName);
		
		Log.logGM(admin.getUserName(), commandString.toString());
	}

}
