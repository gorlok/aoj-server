/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia �gorlok� 
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

import static org.ArgentumOnline.server.util.Color.COLOR_BLANCO;

import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.ObjectInfo;
import org.ArgentumOnline.server.inventory.InventoryObject;
import org.ArgentumOnline.server.map.Map;
import org.ArgentumOnline.server.npc.Npc;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;

/**
 * @author gorlok
 */
public class UserFaction implements Constants {
	
    ///////////////// Constantes p�blicas de la clase:
    public static final int EXP_AL_UNIRSE = 100000;
    public static final int EXP_X_100 = 5000;
    
    ///////////////// Miembros de la CLASE ("globales"):
    public static enum FactionArmors {
        // FACCION IMPERIAL:
    	ARMADURA_IMPERIAL_1("Armadura Imperial 1"),
    	ARMADURA_IMPERIAL_2("Armadura Imperial 2"),
    	ARMADURA_IMPERIAL_3("Armadura Imperial 3"), // ENANO / GNOMO
    	TUNICA_MAGO_IMPERIAL("T�nica Mago Imperial"),
    	TUNICA_MAGO_IMPERIAL_ENANOS("T�nica Mago Imperial Enanos"),
    	
        // FACCION CAOS:
    	ARMADURA_CAOS_1("Armadura Caos 1"),
    	ARMADURA_CAOS_2("Armadura Caos 2"),
    	ARMADURA_CAOS_3("Armadura Caos 3"), // ENANO / GNOMO
    	TUNICA_MAGO_CAOS("T�nica Mago Caos"),
    	TUNICA_MAGO_CAOS_ENANOS("T�nica Mago Caos Enanos");
    	
    	private String name;
    	FactionArmors(String name) {
    		this.name = name;
    	}
    	
    	public String getName() {
			return name;
		}
    }
    static short[] factionArmors = new short[FactionArmors.values().length];

    ///////////////// Miembros de instancia:
    Player player;
    GameServer server;
    
    public boolean ArmadaReal  = false;
    public boolean FuerzasCaos = false;
    public long CriminalesMatados = 0;
    public long CiudadanosMatados = 0;
    public short RecompensasReal   = 0;
    public short RecompensasCaos   = 0;
    public boolean RecibioExpInicialReal = false;
    public boolean RecibioExpInicialCaos = false;
    public boolean RecibioArmaduraReal   = false;
    public boolean RecibioArmaduraCaos   = false;
    
    // FIXME THIS IS NEW
    public int Reenlistadas = 0;
    public int NivelIngreso = 0;
    public String FechaIngreso = "No ingres� a ninguna Facci�n";
    public int MatadosIngreso = 0;
    public int NextRecompensa = 0;
    
    
    public UserFaction(GameServer server, Player player) {
    	this.server = server;
        this.player = player;
    }
    
	public void loadUserFaction(IniFile ini) {
		this.ArmadaReal = ini.getShort("FACCIONES", "EjercitoReal") == 1;
		this.FuerzasCaos = ini.getShort("FACCIONES", "EjercitoCaos") == 1;
		this.CiudadanosMatados = ini.getLong("FACCIONES", "CiudMatados");
		this.CriminalesMatados = ini.getLong("FACCIONES", "CrimMatados");
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
		ini.setValue("FACCIONES", "CiudMatados", this.CiudadanosMatados);
		ini.setValue("FACCIONES", "CrimMatados", this.CriminalesMatados);
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
	
	
	public static void loadFactionArmors(IniFile ini) {
		factionArmors[FactionArmors.ARMADURA_IMPERIAL_1.ordinal()] = ini.getShort("INIT", "ArmaduraImperial1");
		factionArmors[FactionArmors.ARMADURA_IMPERIAL_2.ordinal()] = ini.getShort("INIT", "ArmaduraImperial2");
		factionArmors[FactionArmors.ARMADURA_IMPERIAL_3.ordinal()] = ini.getShort("INIT", "ArmaduraImperial3");
		factionArmors[FactionArmors.TUNICA_MAGO_IMPERIAL.ordinal()] = ini.getShort("INIT", "TunicaMagoImperial");
		factionArmors[FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS.ordinal()] = ini.getShort("INIT", "TunicaMagoImperialEnanos");
		factionArmors[FactionArmors.ARMADURA_CAOS_1.ordinal()] = ini.getShort("INIT", "ArmaduraCaos1");
		factionArmors[FactionArmors.ARMADURA_CAOS_2.ordinal()] = ini.getShort("INIT", "ArmaduraCaos2");
		factionArmors[FactionArmors.ARMADURA_CAOS_3.ordinal()] = ini.getShort("INIT", "ArmaduraCaos3");
		factionArmors[FactionArmors.TUNICA_MAGO_CAOS.ordinal()] = ini.getShort("INIT", "TunicaMagoCaos");
		factionArmors[FactionArmors.TUNICA_MAGO_CAOS_ENANOS.ordinal()] = ini.getShort("INIT", "TunicaMagoCaosEnanos");
	}
	
	public static short getFactionArmor(FactionArmors factiorArmor) {
		return factionArmors[factiorArmor.ordinal()];
	}
    
	public static void sendFactionArmor(Player admin, FactionArmors factiorArmor) {
		String msg = new StringBuilder()
				.append(factiorArmor.getName())
				.append(" es ")
				.append(factionArmors[factiorArmor.ordinal()])
				.toString();
		admin.sendMessage(msg, FontType.FONTTYPE_INFO);
	}
	
	public static void updateFactionArmor(Player admin, FactionArmors factiorArmor, short armorObjIdx) {
		factionArmors[factiorArmor.ordinal()] = armorObjIdx;
		
		String msg = new StringBuilder()
				.append(factiorArmor.getName())
				.append(" ha sido actualizada")
				.toString();
		admin.sendMessage(msg, FontType.FONTTYPE_INFO);
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
    
    public void enlistarArmadaReal(Npc npc) {
        if (this.ArmadaReal) {
            this.player.talk(COLOR_BLANCO, "�Ya perteneces a las tropas reales! Ve a combatir criminales!!!", npc.getId());
            return;
        }
        if (this.FuerzasCaos) {
            this.player.talk(COLOR_BLANCO, "�Maldito insolente! Vete de aqui seguidor de las sombras!!!", npc.getId());
            return;
        }
        if (this.player.isCriminal()) {
            this.player.talk(COLOR_BLANCO, "No se permiten criminales en el ej�rcito imperial!!!", npc.getId());
            return;
        }
        if (this.CriminalesMatados < 10) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes matar al menos 10 criminales, y solo has matado " + this.CriminalesMatados, npc.getId());
            return;
        }
        if (this.player.stats().ELV < 18) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes ser al menos nivel 18!!!", npc.getId());
            return;
        }
        if (this.CiudadanosMatados > 0) {
            this.player.talk(COLOR_BLANCO, "Has asesinado gente inocente, no aceptamos asesinos en las tropas reales!", npc.getId());
            return;
        }
        this.ArmadaReal = true;
        this.RecompensasReal = (short) (this.CriminalesMatados / 100);
        this.player.talk(COLOR_BLANCO, "Bienvenido a al Ejercito Imperial!!!. Aqu� tienes tu armadura. Por cada centena de criminales que acabes te dar� un recompensa, buena suerte soldado!", npc.getId());
        if (!this.RecibioArmaduraReal) {
            short armadura = this.player.clazz().getArmaduraImperial(this.player);
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

    public void enlistarCaos(Npc npc) {
        if (!this.player.isCriminal()) {
            this.player.talk(COLOR_BLANCO, "L�rgate de aqui, buf�n!!!! No eres bienvenido!", npc.getId());
            return;
        }
        if (this.FuerzasCaos) {
            this.player.talk(COLOR_BLANCO, "Ya perteneces a las tropas del Caos!!!", npc.getId());
            return;
        }
        if (this.ArmadaReal) {
            this.player.talk(COLOR_BLANCO, "Las sombras reinar�n en Argentum, l�rgate de aqui est�pido ciudadano.!!!", npc.getId());
            return;
        }
        // Si era miembro de la Armada Real no se puede enlistar
        if (this.RecibioExpInicialReal) { 
            // Tomamos el valor de ah�: �Recibio la experiencia para entrar?
            this.player.talk(COLOR_BLANCO, "No permitir� que ning�n insecto real ingrese �Traidor del Rey!", npc.getId());
            return;
        }
        if (this.CiudadanosMatados < 150) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes matar al menos 150 ciudadanos, y solo has matado " + this.CiudadanosMatados + ". No pierdas tiempo y haz r�pido tu trabajo!", npc.getId());
            return;
        }
        if (this.player.stats().ELV < 25) {
            this.player.talk(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes ser al menos nivel 25!!!", npc.getId());
            return;
        }
        this.FuerzasCaos = true;
        this.RecompensasCaos = (short) (this.CiudadanosMatados / 100);
        this.player.talk(COLOR_BLANCO, "Bienvenido al lado oscuro!!!. Aqui tienes tu armadura. Por cada centena de ciudadanos que acabes te dar� un recompensa, buena suerte soldado!", npc.getId());
        if (!this.RecibioArmaduraCaos) {
            short armadura = this.player.clazz().getArmaduraCaos(this.player);
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

    public void recompensaArmadaReal(Npc npc) {
        if (this.CriminalesMatados / 100 == this.RecompensasReal) {
            this.player.talk(COLOR_BLANCO, "Ya has recibido tu recompensa, mata 100 criminales mas para recibir la proxima!!!", npc.getId());
        } else {
            this.player.talk(COLOR_BLANCO, "Aqui tienes tu recompensa noble guerrero!!!", npc.getId());
            this.player.stats().addExp(EXP_X_100);
            this.player.sendMessage("Has ganado " + EXP_X_100 + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            this.RecompensasReal++;
            this.player.checkUserLevel();
        }
    }

    public void recompensaCaos(Npc npc) {
        if (this.CiudadanosMatados / 100 == this.RecompensasCaos) {
            this.player.talk(COLOR_BLANCO, "Ya has recibido tu recompensa, mata 100 ciudadanos mas para recibir la proxima!!!", npc.getId());
        } else {
            this.player.talk(COLOR_BLANCO, "Aqui tienes tu recompensa noble guerrero!!!", npc.getId());
            this.player.stats().addExp(EXP_X_100);
            this.player.sendMessage("Has ganado " + EXP_X_100 + " puntos de experiencia.", FontType.FONTTYPE_FIGHT);
            this.RecompensasCaos++;
            this.player.checkUserLevel();
        }
    }

    public void expulsarFaccionReal() {
        this.ArmadaReal = false;
        this.player.sendMessage("Has sido expulsado de las tropas reales!!!.", FontType.FONTTYPE_FIGHT);
    }

    public void expulsarFaccionCaos() {
        this.FuerzasCaos = false;
        this.player.sendMessage("Has sido expulsado de las fuerzas del caos!!!.", FontType.FONTTYPE_FIGHT);
    }

    private final static String[] titulosReales = {
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
    
    public String tituloReal() {
        return this.RecompensasReal < titulosReales.length ? 
            titulosReales[this.RecompensasReal] : 
            titulosReales[titulosReales.length - 1];
    }

    private final static String[] titulosCaos = {
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

    public String tituloCaos() {
        return this.RecompensasCaos < titulosCaos.length ? 
            titulosCaos[this.RecompensasCaos] : 
            titulosCaos[titulosCaos.length - 1];
    }
    
}
