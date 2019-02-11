/**
 * Factions.java
 *
 * Created on 23 de febrero de 2004, 21:35
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
package org.ArgentumOnline.server;

import org.ArgentumOnline.server.util.*;

/**
 * @author gorlok
 */
public class Factions implements Constants {
    ///////////////// Constantes públicas de la clase:
    public static final int EXP_AL_UNIRSE = 100000;
    public static final int EXP_X_100 = 5000;
    
    ///////////////// Miembros de la CLASE ("globales"):
    // FACCION IMPERIAL:
    public static short ArmaduraImperial1; // Primer jerarquia
    public static short ArmaduraImperial2; // Segunda jerarquía
    public static short ArmaduraImperial3; // Enanos (?)
    public static short TunicaMagoImperial; // Magos
    public static short TunicaMagoImperialEnanos; // Magos Enanos
    // FACCION CAOS:
    public static short ArmaduraCaos1; // Primer jerarquia
    public static short ArmaduraCaos2; // Segunda jerarquia
    public static short ArmaduraCaos3; // Enanos (?)
    public static short TunicaMagoCaos; // Magos
    public static short TunicaMagoCaosEnanos; // Magos Enanos

    ///////////////// Miembros de instancia:
    Client cliente;
    AojServer server;
    
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
    
    public Factions(AojServer server, Client cliente) {
    	this.server = server;
        this.cliente = cliente;
    }
    
    public boolean faccionPuedeUsarItem(Client cliente, short objid) {
        ObjectInfo infoObj = this.server.getInfoObjeto(objid);
        if (infoObj.Real == 1) {
            if (!cliente.esCriminal()) {
                return this.ArmadaReal;
            } 
            return false;
        } else if (infoObj.Caos == 1) {
            if (cliente.esCriminal()) {
                return this.FuerzasCaos;
            }
            return false;
        } else {
            return true;
        }
    }
    
    public void enlistarArmadaReal(Npc npc) {
        if (this.ArmadaReal) {
            this.cliente.hablar(COLOR_BLANCO, "¡Ya perteneces a las tropas reales! Ve a combatir criminales!!!", npc.getId());
            return;
        }
        if (this.FuerzasCaos) {
            this.cliente.hablar(COLOR_BLANCO, "¡Maldito insolente! Vete de aqui seguidor de las sombras!!!", npc.getId());
            return;
        }
        if (this.cliente.esCriminal()) {
            this.cliente.hablar(COLOR_BLANCO, "No se permiten criminales en el ejército imperial!!!", npc.getId());
            return;
        }
        if (this.CriminalesMatados < 10) {
            this.cliente.hablar(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes matar al menos 10 criminales, y solo has matado " + this.CriminalesMatados, npc.getId());
            return;
        }
        if (this.cliente.getEstads().ELV < 18) {
            this.cliente.hablar(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes ser al menos nivel 18!!!", npc.getId());
            return;
        }
        if (this.CiudadanosMatados > 0) {
            this.cliente.hablar(COLOR_BLANCO, "Has asesinado gente inocente, no aceptamos asesinos en las tropas reales!", npc.getId());
            return;
        }
        this.ArmadaReal = true;
        this.RecompensasReal = (short) (this.CriminalesMatados / 100);
        this.cliente.hablar(COLOR_BLANCO, "Bienvenido a al Ejercito Imperial!!!. Aquí tienes tu armadura. Por cada centena de criminales que acabes te daré un recompensa, buena suerte soldado!", npc.getId());
        if (!this.RecibioArmaduraReal) {
            short armadura = this.cliente.getClase().getArmaduraImperial(this.cliente);
            if (this.cliente.getInv().agregarItem(armadura, 1) < 1) {
                Map mapa = this.server.getMapa(this.cliente.getPos().mapa);
                mapa.tirarItemAlPiso(this.cliente.getPos().x, this.cliente.getPos().y, new InventoryObject(armadura, 1));
            }
            this.RecibioArmaduraReal = true;
        }
        if (!this.RecibioExpInicialReal) {
            this.cliente.getEstads().addExp(EXP_AL_UNIRSE);
            this.cliente.enviarMensaje("Has ganado " + EXP_AL_UNIRSE + " puntos de experiencia.", FontType.FIGHT);
            this.RecibioExpInicialReal = true;
            this.cliente.checkUserLevel();
        }
        Log.logEjercitoReal(this.cliente.getNick());
    }

    public void enlistarCaos(Npc npc) {
        if (!this.cliente.esCriminal()) {
            this.cliente.hablar(COLOR_BLANCO, "Lárgate de aqui, bufón!!!! No eres bienvenido!", npc.getId());
            return;
        }
        if (this.FuerzasCaos) {
            this.cliente.hablar(COLOR_BLANCO, "Ya perteneces a las tropas del Caos!!!", npc.getId());
            return;
        }
        if (this.ArmadaReal) {
            this.cliente.hablar(COLOR_BLANCO, "Las sombras reinarán en Argentum, lárgate de aqui estúpido ciudadano.!!!", npc.getId());
            return;
        }
        // Si era miembro de la Armada Real no se puede enlistar
        if (this.RecibioExpInicialReal) { 
            // Tomamos el valor de ahí: ¿Recibio la experiencia para entrar?
            this.cliente.hablar(COLOR_BLANCO, "No permitiré que ningún insecto real ingrese ¡Traidor del Rey!", npc.getId());
            return;
        }
        if (this.CiudadanosMatados < 150) {
            this.cliente.hablar(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes matar al menos 150 ciudadanos, y solo has matado " + this.CiudadanosMatados + ". No pierdas tiempo y haz rápido tu trabajo!", npc.getId());
            return;
        }
        if (this.cliente.getEstads().ELV < 25) {
            this.cliente.hablar(COLOR_BLANCO, "Para unirte a nuestras fuerzas debes ser al menos nivel 25!!!", npc.getId());
            return;
        }
        this.FuerzasCaos = true;
        this.RecompensasCaos = (short) (this.CiudadanosMatados / 100);
        this.cliente.hablar(COLOR_BLANCO, "Bienvenido al lado oscuro!!!. Aqui tienes tu armadura. Por cada centena de ciudadanos que acabes te daré un recompensa, buena suerte soldado!", npc.getId());
        if (!this.RecibioArmaduraCaos) {
            short armadura = this.cliente.getClase().getArmaduraCaos(this.cliente);
            if (this.cliente.getInv().agregarItem(armadura, 1) < 1) {
                Map mapa = this.server.getMapa(this.cliente.getPos().mapa);
                mapa.tirarItemAlPiso(this.cliente.getPos().x, this.cliente.getPos().y, new InventoryObject(armadura, 1));
            }
            this.RecibioArmaduraCaos = true;
        }
        if (!this.RecibioExpInicialCaos) {
            this.cliente.getEstads().addExp(EXP_AL_UNIRSE);
            this.cliente.enviarMensaje("Has ganado " + EXP_AL_UNIRSE + " puntos de experiencia.", FontType.FIGHT);
            this.RecibioExpInicialCaos = true;
            this.cliente.checkUserLevel();
        }
        Log.logEjercitoCaos(this.cliente.getNick());
    }

    public void recompensaArmadaReal(Npc npc) {
        if (this.CriminalesMatados / 100 == this.RecompensasReal) {
            this.cliente.hablar(COLOR_BLANCO, "Ya has recibido tu recompensa, mata 100 criminales mas para recibir la proxima!!!", npc.getId());
        } else {
            this.cliente.hablar(COLOR_BLANCO, "Aqui tienes tu recompensa noble guerrero!!!", npc.getId());
            this.cliente.getEstads().addExp(EXP_X_100);
            this.cliente.enviarMensaje("Has ganado " + EXP_X_100 + " puntos de experiencia.", FontType.FIGHT);
            this.RecompensasReal++;
            this.cliente.checkUserLevel();
        }
    }

    public void recompensaCaos(Npc npc) {
        if (this.CiudadanosMatados / 100 == this.RecompensasCaos) {
            this.cliente.hablar(COLOR_BLANCO, "Ya has recibido tu recompensa, mata 100 ciudadanos mas para recibir la proxima!!!", npc.getId());
        } else {
            this.cliente.hablar(COLOR_BLANCO, "Aqui tienes tu recompensa noble guerrero!!!", npc.getId());
            this.cliente.getEstads().addExp(EXP_X_100);
            this.cliente.enviarMensaje("Has ganado " + EXP_X_100 + " puntos de experiencia.", FontType.FIGHT);
            this.RecompensasCaos++;
            this.cliente.checkUserLevel();
        }
    }

    public void expulsarFaccionReal() {
        this.ArmadaReal = false;
        this.cliente.enviarMensaje("Has sido expulsado de las tropas reales!!!.", FontType.FIGHT);
    }

    public void expulsarFaccionCaos() {
        this.FuerzasCaos = false;
        this.cliente.enviarMensaje("Has sido expulsado de las fuerzas del caos!!!.", FontType.FIGHT);
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

