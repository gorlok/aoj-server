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

import static org.argentumonline.server.util.Color.COLOR_BLANCO;

import java.io.IOException;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.ObjectInfo;
import org.argentumonline.server.inventory.InventoryObject;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.npc.Npc;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Log;

/**
 * @author gorlok
 */
public class UserFaction implements Constants {
	
    public static final String NO_INGRESO_A_NINGUNA_FACCION = "No ingresó a ninguna Facción";
    
    public static final int EXP_AL_UNIRSE = 100000;
    
    public static final int EXP_X_100 = 5000;
    
    public boolean ArmadaReal  = false;
    public boolean FuerzasCaos = false;
    
    public long criminalsKilled = 0;
    public long citizensKilled = 0;
    
    public short RecompensasReal   = 0;
    public short RecompensasCaos   = 0;
    
    public boolean RecibioExpInicialReal = false;
    public boolean RecibioExpInicialCaos = false;
    
    public boolean RecibioArmaduraReal   = false;
    public boolean RecibioArmaduraCaos   = false;
    
    public String lastCriminalKilled = "";
    public String lastCitizenKilled = "";
    
    // FIXME THIS IS NEW
    public int Reenlistadas = 0;
    public int NivelIngreso = 0;
    public String FechaIngreso = NO_INGRESO_A_NINGUNA_FACCION;
    public int MatadosIngreso = 0;
    public int NextRecompensa = 0;
    
    
    private Player player;
    private GameServer server;
    
    public UserFaction(GameServer server, Player player) {
    	this.server = server;
        this.player = player;
    }
    
    public void reset() {
    	// ResetFacciones
    	// Resetea todos los valores generales y las stats
        ArmadaReal = false;
        citizensKilled = 0;
        criminalsKilled = 0;
        FuerzasCaos = false;
        FechaIngreso = NO_INGRESO_A_NINGUNA_FACCION;
        RecibioArmaduraCaos = false;
        RecibioArmaduraReal = false;
        RecibioExpInicialCaos = false;
        RecibioExpInicialReal = false;
        RecompensasCaos = 0;
        RecompensasReal = 0;
        Reenlistadas = 0;
        NivelIngreso = 0;
        MatadosIngreso = 0;
        NextRecompensa = 0;
    }

    public void countKill(Player killedUser) {
    	if (killedUser.isCriminal()) {
    		if (!lastCriminalKilled.equalsIgnoreCase(killedUser.userName)) {
    			lastCriminalKilled = killedUser.userName;
    			if (criminalsKilled < MAX_USER_KILLED) {
    				criminalsKilled++;
    			}
    		}
    	} else {
    		if (!lastCitizenKilled.equalsIgnoreCase(killedUser.userName)) {
    			lastCitizenKilled = killedUser.userName;
    			if (citizensKilled < MAX_USER_KILLED) {
    				citizensKilled++;
    			}
    		}
    	}
    }
    
	public void loadUserFaction(IniFile ini) {
		this.ArmadaReal = ini.getShort("FACCIONES", "EjercitoReal") == 1;
		this.FuerzasCaos = ini.getShort("FACCIONES", "EjercitoCaos") == 1;
		this.citizensKilled = ini.getLong("FACCIONES", "CiudMatados");
		this.criminalsKilled = ini.getLong("FACCIONES", "CrimMatados");
		this.RecibioArmaduraCaos = ini.getShort("FACCIONES", "rArCaos") == 1;
		this.RecibioArmaduraReal = ini.getShort("FACCIONES", "rArReal") == 1;
		this.RecibioExpInicialCaos = ini.getShort("FACCIONES", "rExCaos") == 1;
		this.RecibioExpInicialReal = ini.getShort("FACCIONES", "rExReal") == 1;
		this.RecompensasCaos = ini.getShort("FACCIONES", "recCaos");
		this.RecompensasReal = ini.getShort("FACCIONES", "recReal");
	    this.Reenlistadas = ini.getInt("FACCIONES", "Reenlistadas");
	    this.NivelIngreso = ini.getInt("FACCIONES", "NivelIngreso");
	    this.FechaIngreso = ini.getString("FACCIONES", "FechaIngreso");
	    this.MatadosIngreso = ini.getInt("FACCIONES", "MatadosIngreso");
	    this.NextRecompensa = ini.getInt("FACCIONES", "NextRecompensa");
	}
    
	public void saveUserFaction(IniFile ini) {
		ini.setValue("FACCIONES", "EjercitoReal", this.ArmadaReal);
		ini.setValue("FACCIONES", "EjercitoCaos", this.FuerzasCaos);
		ini.setValue("FACCIONES", "CiudMatados", this.citizensKilled);
		ini.setValue("FACCIONES", "CrimMatados", this.criminalsKilled);
		ini.setValue("FACCIONES", "rArCaos", this.RecibioArmaduraCaos);
		ini.setValue("FACCIONES", "rArReal", this.RecibioArmaduraReal);
		ini.setValue("FACCIONES", "rExCaos", this.RecibioExpInicialCaos);
		ini.setValue("FACCIONES", "rExReal", this.RecibioExpInicialReal);
		ini.setValue("FACCIONES", "recCaos", this.RecompensasCaos);
		ini.setValue("FACCIONES", "recReal", this.RecompensasReal);
	    ini.setValue("FACCIONES", "Reenlistadas", this.Reenlistadas);
	    ini.setValue("FACCIONES", "NivelIngreso", this.NivelIngreso);
	    ini.setValue("FACCIONES", "FechaIngreso", this.FechaIngreso);
	    ini.setValue("FACCIONES", "MatadosIngreso", this.MatadosIngreso);
	    ini.setValue("FACCIONES", "NextRecompensa", this.NextRecompensa);
	}
	
    public static void royalArmyKick(Player admin, String userName) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("FACCIONES", "EjercitoReal", 0);
			ini.setValue("FACCIONES", "Reenlistadas", 200);
			ini.setValue("FACCIONES", "Extra", "Expulsado por " + admin.getNick());
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public static void chaosLegionKick(Player admin, String userName) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("FACCIONES", "EjercitoCaos", 0);
			ini.setValue("FACCIONES", "Reenlistadas", 200);
			ini.setValue("FACCIONES", "Extra", "Expulsado por " + admin.getNick());
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }

    public static void resetFactions(String userName) {
		final String fileName = Player.getPjFile(userName);
		IniFile ini;
		try {
			ini = new IniFile(fileName);
			ini.setValue("FACCIONES", "EjercitoReal", 0);
			ini.setValue("FACCIONES", "CiudMatados", 0);
			ini.setValue("FACCIONES", "CrimMatados", 0);
			ini.setValue("FACCIONES", "EjercitoCaos", 0);
			ini.setValue("FACCIONES", "FechaIngreso", UserFaction.NO_INGRESO_A_NINGUNA_FACCION);
			ini.setValue("FACCIONES", "rArCaos", 0);
			ini.setValue("FACCIONES", "rArReal", 0);
			ini.setValue("FACCIONES", "rExCaos", 0);
			ini.setValue("FACCIONES", "rExReal", 0);
			ini.setValue("FACCIONES", "recCaos", 0);
			ini.setValue("FACCIONES", "recReal", 0);
			ini.setValue("FACCIONES", "Reenlistadas", 0);
			ini.setValue("FACCIONES", "NivelIngreso", 0);
			ini.setValue("FACCIONES", "MatadosIngreso", 0);
			ini.setValue("FACCIONES", "NextRecompensa", 0);
			
			ini.store(fileName);
		} catch (IOException ignored) {
		}
    }
    
    public boolean faccionPuedeUsarItem(short objid) {
        ObjectInfo infoObj = this.server.getObjectInfoStorage().getInfoObjeto(objid);
        if (infoObj.esReal()) {
            if (!player.isCriminal()) {
                return this.ArmadaReal;
            } 
            return false;
        } else if (infoObj.esCaos()) {
            if (player.isCriminal()) {
                return this.FuerzasCaos;
            }
            return false;
        } else {
            return true;
        }
    }
    
    public void royalArmyEnlist(Npc npc) {
        if (this.ArmadaReal) {
            this.player.talk(COLOR_BLANCO, "¡Ya perteneces a las tropas reales! Ve a combatir criminales!!!", npc.getId());
            return;
        }
        if (this.FuerzasCaos) {
            this.player.talk(COLOR_BLANCO, "¡Maldito insolente! Vete de aqui seguidor de las sombras!!!", npc.getId());
            return;
        }
        if (this.player.isCriminal()) {
            this.player.talk(COLOR_BLANCO, "No se permiten criminales en el ejército imperial!!!", npc.getId());
            return;
        }
        if (this.criminalsKilled < 10) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes matar al menos 10 criminales, y solo has matado " + this.criminalsKilled, npc.getId());
            return;
        }
        if (this.player.stats().ELV < 18) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes ser al menos nivel 18!!!", npc.getId());
            return;
        }
        if (this.citizensKilled > 0) {
            this.player.talk(COLOR_BLANCO, "Has asesinado gente inocente, no aceptamos asesinos en las tropas reales!", npc.getId());
            return;
        }
        this.ArmadaReal = true;
        this.RecompensasReal = (short) (this.criminalsKilled / 100);
        this.player.talk(COLOR_BLANCO, "Bienvenido a al Ejercito Imperial!!!. Aquí tienes tu armadura. Por cada centena de criminales que acabes te daré un recompensa, buena suerte soldado!", npc.getId());
        if (!this.RecibioArmaduraReal) {
            short armadura = this.player.clazz().getRoyalArmyArmor(this.player);
            if (this.player.userInv().agregarItem(armadura, 1) < 1) {
                Map mapa = this.server.getMap(this.player.pos().map);
                mapa.dropItemOnFloor(this.player.pos().x, this.player.pos().y, new InventoryObject(armadura, 1));
            }
            this.RecibioArmaduraReal = true;
        }
        if (!this.RecibioExpInicialReal) {
            this.player.stats().addExp(EXP_AL_UNIRSE);
            this.player.sendMessage("Has ganado " + EXP_AL_UNIRSE + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            this.RecibioExpInicialReal = true;
            this.player.checkUserLevel();
        }
        Log.logEjercitoReal(this.player.getNick());
    }

    public void darkLegionEnlist(Npc npc) {
        if (!this.player.isCriminal()) {
            this.player.talk(COLOR_BLANCO, "Lárgate de aqui, bufón!!!! No eres bienvenido!", npc.getId());
            return;
        }
        if (this.FuerzasCaos) {
            this.player.talk(COLOR_BLANCO, "Ya perteneces a las tropas del Caos!!!", npc.getId());
            return;
        }
        if (this.ArmadaReal) {
            this.player.talk(COLOR_BLANCO, "Las sombras reinarán en Argentum, lárgate de aqui estúpido ciudadano.!!!", npc.getId());
            return;
        }
        // Si era miembro de la Armada Real no se puede enlistar
        if (this.RecibioExpInicialReal) { 
            // Tomamos el valor de ahí: ¿Recibio la experiencia para entrar?
            this.player.talk(COLOR_BLANCO, "No permitiré que ningún insecto real ingrese ¡Traidor del Rey!", npc.getId());
            return;
        }
        if (this.citizensKilled < 150) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes matar al menos 150 ciudadanos, y solo has matado " + this.citizensKilled + ". No pierdas tiempo y haz rápido tu trabajo!", npc.getId());
            return;
        }
        if (this.player.stats().ELV < 25) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes ser al menos nivel 25!!!", npc.getId());
            return;
        }
        this.FuerzasCaos = true;
        this.RecompensasCaos = (short) (this.citizensKilled / 100);
        this.player.talk(COLOR_BLANCO, "Bienvenido al lado oscuro!!!. Aqui tienes tu armadura. Por cada centena de ciudadanos que acabes te daré un recompensa, buena suerte soldado!", npc.getId());
        if (!this.RecibioArmaduraCaos) {
            short armadura = this.player.clazz().getDarkLegionArmor(this.player);
            if (this.player.userInv().agregarItem(armadura, 1) < 1) {
                Map mapa = this.server.getMap(this.player.pos().map);
                mapa.dropItemOnFloor(this.player.pos().x, this.player.pos().y, new InventoryObject(armadura, 1));
            }
            this.RecibioArmaduraCaos = true;
        }
        if (!this.RecibioExpInicialCaos) {
            this.player.stats().addExp(EXP_AL_UNIRSE);
            this.player.sendMessage("Has ganado " + EXP_AL_UNIRSE + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            this.RecibioExpInicialCaos = true;
            this.player.checkUserLevel();
        }
        Log.logEjercitoCaos(this.player.getNick());
    }

    public void royalArmyReward(Npc npc) {
        if (this.criminalsKilled / 100 == this.RecompensasReal) {
            this.player.talk(COLOR_BLANCO, "Ya has recibido tu recompensa, mata 100 criminales mas para recibir la proxima!!!", npc.getId());
        } else {
            this.player.talk(COLOR_BLANCO, "Aqui tienes tu recompensa noble guerrero!!!", npc.getId());
            this.player.stats().addExp(EXP_X_100);
            this.player.sendMessage("Has ganado " + EXP_X_100 + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            this.RecompensasReal++;
            this.player.checkUserLevel();
        }
    }

    public void darkLegionReward(Npc npc) {
        if (this.citizensKilled / 100 == this.RecompensasCaos) {
            this.player.talk(COLOR_BLANCO, "Ya has recibido tu recompensa, mata 100 ciudadanos mas para recibir la proxima!!!", npc.getId());
        } else {
            this.player.talk(COLOR_BLANCO, "Aqui tienes tu recompensa noble guerrero!!!", npc.getId());
            this.player.stats().addExp(EXP_X_100);
            this.player.sendMessage("Has ganado " + EXP_X_100 + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            this.RecompensasCaos++;
            this.player.checkUserLevel();
        }
    }

    public void royalArmyKick() {
        this.ArmadaReal = false;
        this.player.sendMessage("Has sido expulsado de las tropas reales!!!.", FontType.FONTTYPE_FIGHT);
    }

    public void darkLegionKick() {
        this.FuerzasCaos = false;
        this.player.sendMessage("Has sido expulsado de las fuerzas del caos!!!.", FontType.FONTTYPE_FIGHT);
    }
    
    public void royalArmyKickForEver(String byWho) { 
		ArmadaReal = false;
		Reenlistadas = 200;
		player.sendMessage(byWho + " te ha expulsado de forma definitiva de la Armada Real.", FontType.FONTTYPE_FIGHT);
    }
    
    public void darkLegionKickForEver(String byWho) { 
    	FuerzasCaos = false;
		Reenlistadas = 200;
		player.sendMessage(byWho + " te ha expulsado de forma definitiva de la Legión Oscura.", FontType.FONTTYPE_FIGHT);
    }

    private final static String[] ROYAL_ARMY_TITLES = {
        "Aprendiz real",        // 0
        "Soldado real",         // 1
        "Teniente real",        // 2
        "Comandante real",      // 3
        "General real",         // 4
        "Elite real",           // 5
        "Guardian del bien",    // 6
        "Caballero Imperial",   // 7
        "Guardian del bien",    // 8
        "Protector de Newbies"  // 9
    };
    
    public String royalArmyTitle() {
        return this.RecompensasReal < ROYAL_ARMY_TITLES.length ? 
            ROYAL_ARMY_TITLES[this.RecompensasReal] : 
            ROYAL_ARMY_TITLES[ROYAL_ARMY_TITLES.length - 1];
    }

    private final static String[] DARK_LEGION_TITLES = {
        "Adorador del demonio",     // 0
        "Esclavo de las sombras",   // 1
        "Guerrero del caos",        // 2
        "Teniente del caos",        // 3
        "Comandante del caos",      // 4
        "General del caos",         // 5
        "Elite caos",               // 6
        "Asolador de las sombras",  // 7
        "Caballero Oscuro",         // 8
        "Asesino del caos"          // 9
    };

    public String darkLegionTitle() {
        return this.RecompensasCaos < DARK_LEGION_TITLES.length ? 
            DARK_LEGION_TITLES[this.RecompensasCaos] : 
            DARK_LEGION_TITLES[DARK_LEGION_TITLES.length - 1];
    }
    
}
