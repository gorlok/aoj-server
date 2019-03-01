/**
 * UserFlags.java
 *
 * Created on 23 de febrero de 2004, 21:34
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

/**
 * @author gorlok
 */
// Flags del usuario
public class UserFlags {
	
	public int areaPerteneceX;
	public int areaPerteneceY;
	
    public boolean Muerto = false; // ¿Esta muerto?
    public boolean Escondido = false; // ¿Esta escondido?
    public boolean Comerciando = false; // ¿Esta comerciando?
    public boolean UserLogged = false; // ¿Esta online?
    public boolean Meditando = false;
    public boolean ModoCombate = false;
    public boolean Hambre = false;
    public boolean Sed = false;
    public boolean PuedeMoverse = false;
    //public boolean PuedeLanzarSpell = false;
    //public long TimerPuedeLanzarSpell = 0;
    //public boolean PuedeTrabajar = false;
    public boolean Envenenado = false;
    public boolean Paralizado = false;
    public boolean Estupidez = false;
    public boolean Ceguera = false;
    public boolean Invisible = false;
    public boolean Maldicion = false;
    public boolean Bendicion = false;
    public boolean Oculto = false;
    public boolean Desnudo = false;
    public boolean Descansar = false;
    public boolean TomoPocion = false;
    public boolean Vuela = false;
    public boolean Navegando = false;
    
    public boolean Seguro = true;
    public boolean PuedeAtacar = true;
    
    public boolean Ban = false;
    public boolean AdministrativeBan = false;
    
    public boolean StatsChanged = false;
    
    public boolean AdminInvisible = false;
    
    public short Hechizo = 0;
    public short TipoPocion = 0;
    public double Descuento = 0.0;
    
    public long DuracionEfecto = 0;
    public short TargetNpc = 0; // Npc señalado por el usuario
    public short TargetNpcTipo = 0; // Tipo del npc señalado
    public short NpcInv = 0;
    
    public short TargetUser = 0; // Usuario señalado
    
    public short TargetObj = 0;// Obj señalado
    public short TargetObjMap = 0;
    public byte TargetObjX = 0;
    public byte TargetObjY = 0;
    
    public short TargetMap = 0;
    public byte TargetX = 0;
    public byte TargetY = 0;
    
    public short TargetObjInvIndex = 0;
    public short TargetObjInvSlot = 0;
    
    public short AtacadoPorNpc = 0;
    public short AtacadoPorUser = 0;
    
    public short Privilegios = 0;
    
    public short ValCoDe = 0;
    
    public String LastCrimMatado = "";
    public String LastCiudMatado = "";
    
    public short OldBody = 0;
    public short OldHead = 0;
    
    long TimesWalk = 0;
    long StartWalk = 0;
    long CountSH = 0;
    boolean Trabajando = false;
    
    byte UltimoMensaje = 0;
    
    short indexDuel = 0;
    
    public boolean Mimetizado = false; // FIXME
    
}


