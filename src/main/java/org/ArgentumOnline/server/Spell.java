/**
 * Spell.java
 *
 * Created on 17 de septiembre de 2003, 22:48
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
 * @author Pablo F. Lillia
 */
public class Spell implements Constants {
    
    public int Numero = 0;    
    public String Nombre = "";
    public String Desc = "";
    public String PalabrasMagicas = "";
    
    public String HechiceroMsg = "";
    public String TargetMsg = "";
    public String PropioMsg = "";
    
    char  Resis = 0;
    
    public char  Tipo  = 0;
    short WAV   = 0;
    short FXgrh = 0;
    char  loops = 0;
    
    char  SubeHP = 0;
    short MinHP  = 0;
    short MaxHP  = 0;
    
    char  SubeMana = 0;
    short MinMana   = 0;
    short MaxMana   = 0;
    
    char  SubeSta = 0;
    short MinSta  = 0;
    short MaxSta  = 0;
    
    char  SubeHam = 0;
    short MinHam  = 0;
    short MaxHam  = 0;
    
    char  SubeSed = 0;
    short MinSed  = 0;
    short MaxSed  = 0;
    
    char  SubeAgilidad = 0;
    short MinAgilidad  = 0;
    short MaxAgilidad  = 0;
    
    char  SubeFuerza = 0;
    short MinFuerza  = 0;
    short MaxFuerza  = 0;
    
    char  SubeCarisma = 0;
    short MinCarisma  = 0;
    short MaxCarisma  = 0;
    
    public char Invisibilidad  = 0;
    char Paraliza       = 0;
    char RemoverParalisis = 0;
    char CuraVeneno     = 0;
    char Envenena       = 0;
    char Maldicion      = 0;
    char RemoverMaldicion = 0;
    char Bendicion      = 0;
    char Estupidez      = 0;
    char Ceguera        = 0;
    char Revivir        = 0;
    char Morph          = 0;    
    
    char  Invoca        = 0;
    int   NumNpc        = 0;
    public short Cant   = 0;    
    char  Materializa   = 0;
    char  ItemIndex     = 0;    
    short MinSkill      = 0;    
    public short ManaRequerido = 0;    
    char  Target        = 0;
    public short StaRequerida  = 0;
    
    /** Creates a new instance of Hechizo */
    public Spell(int numero) {
        this.Numero = numero;
    }
    
    public int getId() {
    	return this.Numero;
    }
    
    public String getName() {
    	return this.Nombre;
    }
    
    public void load(IniFile ini) {
        String section = "HECHIZO" + this.Numero;
        this.Nombre          = ini.getString(section, "Nombre");
        this.Desc            = ini.getString(section, "Desc");
        this.PalabrasMagicas = ini.getString(section, "PalabrasMagicas");
        this.HechiceroMsg    = ini.getString(section, "HechiceroMsg");
        this.TargetMsg       = ini.getString(section, "TargetMsg");
        this.PropioMsg       = ini.getString(section, "PropioMsg");
        
        this.Resis   = (char) ini.getShort(section, "Resis");
        this.Tipo    = (char) ini.getShort(section, "Tipo");
        this.WAV     = ini.getShort(section, "WAV");
        this.FXgrh   = ini.getShort(section, "FXgrh");
        this.loops   = (char) ini.getShort(section, "loops");
        
        this.SubeHP  = (char) ini.getShort(section, "SubeHP");
        this.MinHP   = ini.getShort(section, "MinHP");
        this.MaxHP   = ini.getShort(section, "MaxHP");
        
        this.SubeMana = (char) ini.getShort(section, "SubeMana");
        this.MinMana   = ini.getShort(section, "MinMana");
        this.MaxMana   = ini.getShort(section, "MaxMana");
        
        this.SubeSta  = (char) ini.getShort(section, "SubeSta");
        this.MinSta   = ini.getShort(section, "MinSta");
        
        this.MaxSta   = ini.getShort(section, "MaxSta");
        
        this.SubeHam  = (char) ini.getShort(section, "SubeHam");
        this.MinHam   = ini.getShort(section, "MinHam");
        this.MaxHam   = ini.getShort(section, "MaxHam");
        
        this.SubeSed  = (char) ini.getShort(section, "SubeSed");
        this.MinSed   = ini.getShort(section, "MinSed");
        this.MaxSed   = ini.getShort(section, "MaxSed");
        
        this.SubeAgilidad = (char) ini.getShort(section, "SubeAgilidad");
        this.MinAgilidad  = ini.getShort(section, "MinAgilidad");
        this.MaxAgilidad  = ini.getShort(section, "MaxAgilidad");
        
        this.SubeFuerza 	= (char) ini.getShort(section, "SubeFuerza");
        this.MinFuerza  	= ini.getShort(section, "MinFuerza");
        this.MaxFuerza  	= ini.getShort(section, "MaxFuerza");
        
        this.SubeCarisma = (char) ini.getShort(section, "SubeCarisma");
        this.MinCarisma  = ini.getShort(section, "MinCarisma");
        this.MaxCarisma  = ini.getShort(section, "MaxCarisma");
        
        this.Invisibilidad   = (char) ini.getShort(section, "Invisibilidad");
        this.Paraliza        = (char) ini.getShort(section, "Paraliza");
        this.RemoverParalisis = (char) ini.getShort(section, "RemoverParalisis");
        this.CuraVeneno      = (char) ini.getShort(section, "CuraVeneno");
        this.Envenena        = (char) ini.getShort(section, "Envenena");
        this.Maldicion       = (char) ini.getShort(section, "Maldicion");
        this.RemoverMaldicion = (char) ini.getShort(section, "RemoverMaldicion");
        this.Bendicion       = (char) ini.getShort(section, "Bendicion");
        this.Estupidez       = (char) ini.getShort(section, "Estupidez");
        this.Ceguera         = (char) ini.getShort(section, "Ceguera");
        this.Revivir         = (char) ini.getShort(section, "Revivir");
        this.Morph           = (char) ini.getShort(section, "Morph");
        
        this.Invoca  = (char) ini.getShort(section, "Invoca");
        this.NumNpc  = ini.getShort(section, "NumNpc");
        this.Cant    = ini.getShort(section, "Cant");
        
        this.Materializa = (char) ini.getShort(section, "Materializa");
        this.ItemIndex   = (char) ini.getShort(section, "ItemIndex");
        
        this.MinSkill       = ini.getShort(section, "MinSkill");
        this.ManaRequerido  = ini.getShort(section, "ManaRequerido");
        this.StaRequerida   = ini.getShort(section, "StaRequerido");
        
        this.Target  = (char) ini.getShort(section, "Target");
    }
}
