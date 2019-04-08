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
    public boolean PuedeAtacar = true;
    //public boolean PuedeLanzarSpell = false;
    //public boolean PuedeTrabajar = false;

    //public long TimerPuedeLanzarSpell = 0;
    
    public boolean Envenenado = false;
    public boolean Paralizado = false;
    public boolean Inmovilizado = false;
    public boolean Estupidez = false;
    public boolean Ceguera = false;
    public boolean Invisible = false;
    public boolean Maldicion = false;
    public boolean Bendicion = false;
    public boolean Oculto = false;
    public boolean Desnudo = false;
    public boolean Descansar = false;
    public boolean Vuela = false;
    public boolean Navegando = false;
    public boolean Mimetizado = false; // FIXME
    
    public boolean NoActualizado; // FIXME
    
    public boolean Silenciado;
    
    public boolean TomoPocion = false;
    public short TipoPocion = 0;
    
    public boolean Seguro = true;
    public boolean SeguroResu = true; // FIXME    
    
    public boolean StatsChanged = false;
    
    public boolean AdminInvisible = false;
    public boolean AdminPerseguible = false; // User can be followed by NPCs
    
    public short Hechizo = 0;
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
    
    public boolean workWatcherRepliedOK = false;
    
	public enum UserType {
		
	    User		(0x01),
	    Consejero	(0x02),
	    SemiDios	(0x04),
	    Dios		(0x08),
	    Admin		(0x10),
	    RoleMaster	(0x20),
	    ChaosCouncil(0x40),
	    RoyalCouncil(0x80);
	    
	    private int value;
	    private UserType(int value) {
	    	this.value = value;
		}
	    
	    public int value() {
			return this.value;
		}
	}
	
	/**
	 * This is a binary OR of UserType flags.
	 * Ordinary user has privileges = 1.
	 * GM has privileges > 1
	 */
    public int privileges = 0;
    
    public short ValCoDe = 0;
    
    public short OldBody = 0;
    public short OldHead = 0;
    
    long TimesWalk = 0;
    long StartWalk = 0;
    long CountSH = 0;
    boolean Trabajando = false;
    
    byte UltimoMensaje = 0; // FIXME hacer enum de mensajes y IDs... y para i18n también 
    
    short indexDuel = 0;
    
	public boolean isGod() {
		return (privileges & UserType.Dios.value()) > 0;
	}

	public boolean isAdmin() {
		return (privileges & UserType.Admin.value()) > 0;
	}

	public boolean isDemiGod() {
		return (privileges & UserType.SemiDios.value()) > 0;
	}

	public boolean isCounselor() {
		return (privileges & UserType.Consejero.value()) > 0;
	}

	public boolean isGM() {
		return privileges > UserType.User.value();
	}
	
	public boolean isRoleMaster() {
		return (privileges & UserType.RoleMaster.value()) > 0; 
	}

	public boolean isChaosCouncil() {
		return (privileges & UserType.ChaosCouncil.value()) > 0; 
	}

	public boolean isRoyalCouncil() {
		return (privileges & UserType.RoyalCouncil.value()) > 0; 
	}
	
	public void removeChaosCouncil() {
		if (isChaosCouncil()) {
			privileges =- UserType.ChaosCouncil.value();
		}
	}
	
	public void removeRoyalCouncil() {
		if (isRoyalCouncil()) {
			privileges =- UserType.RoyalCouncil.value(); 
		}
	}
	
	public void addChaosCouncil() {
		if (!isChaosCouncil()) {
			privileges =+ UserType.ChaosCouncil.value();
		}
	}
	
	public void addRoyalCouncil() {
		if (!isRoyalCouncil()) {
			privileges =+ UserType.RoyalCouncil.value(); 
		}
	}
	
	public void setGod() {
		this.privileges = UserType.Dios.value();
	}

	public void setDemiGod() {
		this.privileges = UserType.SemiDios.value();
	}

	public void setCounselor() {
		this.privileges = UserType.Consejero.value();
	}
	
	public void setOrdinaryUser() {
		this.privileges = UserType.User.value();
	}

	public void setChaosCouncil() {
		this.privileges = UserType.ChaosCouncil.value();
	}

	public void setRoyalCouncil() {
		this.privileges = UserType.RoyalCouncil.value();
	}

}
