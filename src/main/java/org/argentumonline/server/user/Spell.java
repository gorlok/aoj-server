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

import org.argentumonline.server.Constants;
import org.argentumonline.server.util.*;

/**
 * @author gorlok
 */
public class Spell implements Constants {
    
    public int Numero = 0;    
    public String Nombre = "";
    public String Desc = "";
    public String PalabrasMagicas = "";
    
    public String HechiceroMsg = "";
    public String TargetMsg = "";
    public String PropioMsg = "";
    
    boolean Invisibilidad  = false;
    boolean inmoviliza     = false;
    boolean paraliza       = false;
    boolean RemoverParalisis = false;
    boolean CuraVeneno     = false;
    boolean Envenena       = false;
    boolean Maldicion      = false;
    boolean RemoverMaldicion = false;
    boolean Bendicion      = false;
    boolean estupidez      = false;
    boolean Ceguera        = false;
    boolean Revivir        = false;
    boolean Morph          = false;
    boolean RemoverEstupidez = false;
    boolean Mimetiza 		= false;
    boolean RemueveInvisibilidadParcial = false;    
    
    /* Tipo */
    SpellAction spellAction;
    
    public byte  WAV   = 0;
    public short FXgrh = 0;
    public char  loops = 0;
    
    public char  SubeHP = 0;
    public short MinHP  = 0;
    public short MaxHP  = 0;
    
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
    
    
    char  Invoca        = 0;
    int   NumNpc        = 0;
    public short Cant   = 0;
    
    public short MinSkill      = 0;    
    public short ManaRequerido = 0;
    public short StaRequerida  = 0;
    
    SpellTarget  Target;
    
    short NeedStaff; // FIXME
    boolean StaffAffected; // FIXME 
    
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
    
    public boolean isParaliza() {
		return paraliza;
	}
    
    public boolean isInmoviliza() {
		return inmoviliza;
	}
    
    public boolean isEstupidez() {
    	return estupidez;
    }
    
    public void load(IniFile ini) {
        String section = "HECHIZO" + this.Numero;
        this.Nombre          = ini.getString(section, "Nombre");
        this.Desc            = ini.getString(section, "Desc");
        this.PalabrasMagicas = ini.getString(section, "PalabrasMagicas");
        this.HechiceroMsg    = ini.getString(section, "HechiceroMsg");
        this.TargetMsg       = ini.getString(section, "TargetMsg");
        this.PropioMsg       = ini.getString(section, "PropioMsg");
        
        this.spellAction    = SpellAction.value(ini.getShort(section, "Tipo"));
        
        this.WAV     = (byte) ini.getShort(section, "WAV");
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
        
        this.Invisibilidad   = ini.getShort(section, "Invisibilidad") == 1;
        this.RemueveInvisibilidadParcial   = ini.getShort(section, "RemueveInvisibilidadParcial") == 1;
		this.inmoviliza      = ini.getShort(section, "Inmoviliza") == 1;
        this.paraliza        = ini.getShort(section, "Paraliza") == 1;
        this.RemoverParalisis= ini.getShort(section, "RemoverParalisis") == 1;
        this.Envenena        = ini.getShort(section, "Envenena") == 1;
        this.CuraVeneno      = ini.getShort(section, "CuraVeneno") == 1;
        this.Maldicion       = ini.getShort(section, "Maldicion") == 1;
        this.RemoverMaldicion= ini.getShort(section, "RemoverMaldicion") == 1;
        this.Bendicion       = ini.getShort(section, "Bendicion") == 1;
        this.estupidez       = ini.getShort(section, "Estupidez") == 1;
        this.RemoverEstupidez = ini.getShort(section, "RemoverEstupidez") == 1;
        this.Ceguera         = ini.getShort(section, "Ceguera") == 1;
        this.Revivir         = ini.getShort(section, "Revivir") == 1;
        this.Morph           = ini.getShort(section, "Morph") == 1;
        this.Mimetiza        = ini.getShort(section, "Mimetiza") == 1;
        		
        this.Invoca  = (char) ini.getShort(section, "Invoca");
        this.NumNpc  = ini.getShort(section, "NumNpc");
        this.Cant    = ini.getShort(section, "Cant");
        
        this.MinSkill       = ini.getShort(section, "MinSkill");
        this.ManaRequerido  = ini.getShort(section, "ManaRequerido");
        this.StaRequerida   = ini.getShort(section, "StaRequerido");
        
        this.Target  = SpellTarget.value(ini.getShort(section, "Target"));
        
        this.NeedStaff = ini.getShort(section, "NeedStaff");
	    this.StaffAffected = ini.getShort(section, "StaffAffected") == 1;        
    }
}
