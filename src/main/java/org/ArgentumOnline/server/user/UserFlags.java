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

/**
 * @author gorlok
 */
// Flags del usuario
public class UserFlags {
	
	public int areaPerteneceX;
	public int areaPerteneceY;
	
    public boolean Muerto = false; // �Esta muerto?
    public boolean Escondido = false; // �Esta escondido?
    public boolean Comerciando = false; // �Esta comerciando?
    public boolean UserLogged = false; // �Esta online?
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
    
    public byte Silenciado; // FIXME
    
    public boolean TomoPocion = false;
    public short TipoPocion = 0;
    
    public boolean Seguro = true;
    public boolean SeguroResu = true; // FIXME    
    
    public boolean Ban = false;
    public boolean AdministrativeBan = false;
    
    public boolean StatsChanged = false;
    
    public boolean AdminInvisible = false;
    public boolean AdminPerseguible = false; // User can be followed by NPCs
    
    public short Hechizo = 0;
    public double Descuento = 0.0;
    
    public long DuracionEfecto = 0;
    public short TargetNpc = 0; // Npc se�alado por el usuario
    public short TargetNpcTipo = 0; // Tipo del npc se�alado
    public short NpcInv = 0;
    
    public short TargetUser = 0; // Usuario se�alado
    
    public short TargetObj = 0;// Obj se�alado
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
    
	public enum PlayerType {
		
	    User		(0x01),
	    Consejero	(0x02),
	    SemiDios	(0x04),
	    Dios		(0x08),
	    Admin		(0x10),
	    RoleMaster	(0x20),
	    ChaosCouncil(0x40),
	    RoyalCouncil(0x80);
	    
	    private int value;
	    private PlayerType(int value) {
	    	this.value = value;
		}
	    
	    public int value() {
			return this.value;
		}
	}
	
	/**
	 * This is a binary OR of PlayerType flags.
	 * Ordinary user has privileges = 1.
	 * GM has privileges > 1
	 */
    public int privileges = 0;
    
    public short ValCoDe = 0;
    
    public String LastCrimMatado = "";
    public String LastCiudMatado = "";
    
    public short OldBody = 0;
    public short OldHead = 0;
    
    long TimesWalk = 0;
    long StartWalk = 0;
    long CountSH = 0;
    boolean Trabajando = false;
    
    byte UltimoMensaje = 0; // FIXME hacer enum de mensajes y IDs... y para i18n tambi�n 
    
    short indexDuel = 0;
    
	public boolean isGod() {
		return (privileges & PlayerType.Dios.value()) > 0;
	}

	public boolean isAdmin() {
		return (privileges & PlayerType.Admin.value()) > 0;
	}

	public boolean isDemiGod() {
		return (privileges & PlayerType.SemiDios.value()) > 0;
	}

	public boolean isCounselor() {
		return (privileges & PlayerType.Consejero.value()) > 0;
	}

	public boolean isGM() {
		return privileges > PlayerType.User.value();
	}
	
	public boolean isRoleMaster() {
		return (privileges & PlayerType.RoleMaster.value()) > 0; 
	}

	public boolean isChaosCouncil() {
		return (privileges & PlayerType.ChaosCouncil.value()) > 0; 
	}

	public boolean isRoyalCouncil() {
		return (privileges & PlayerType.RoyalCouncil.value()) > 0; 
	}
	
	public void setGod() {
		this.privileges = PlayerType.Dios.value();
	}

	public void setDemiGod() {
		this.privileges = PlayerType.SemiDios.value();
	}

	public void setCounselor() {
		this.privileges = PlayerType.Consejero.value();
	}
	
	public void setOrdinaryUser() {
		this.privileges = PlayerType.User.value();
	}

	public void setChaosCouncil() {
		this.privileges = PlayerType.ChaosCouncil.value();
	}

	public void setRoyalCouncil() {
		this.privileges = PlayerType.RoyalCouncil.value();
	}

}
