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
package org.ArgentumOnline.server.classes;

import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.util.Util;

/**
 * Class of the fisherman character.
 * @author  gorlok
 */
public class FisherClass extends AbstractClazz {
    
    FisherClass() {
        this.name = "PESCADOR";
    }
    
    @Override
	public double modificadorPoderAtaqueArmas() {
        return 0.6;
    }
    
    @Override
	public double modificadorPoderAtaqueProyectiles() {
        return 0.65;
    }
    
    @Override
	public double modicadorDañoClaseArmas() {
        return 0.6;
    }
     
    @Override
	public double modicadorDañoClaseProyectiles() {
        return 0.6;
    }
    
    @Override
	public double modEvasionDeEscudoClase() {
        return 0.7;
    }
    
    @Override
	public double modNavegacion() {
        return 1.2;
    }
    
    @Override
	public short getEsfuerzoPescar() {
        return 1;
    }
    
    /** Incremento de salud al subir de nivel */
    @Override
	protected int getMejoraSalud(UserStats estads) {
        return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2);
    }
    
    /** Incremento de stamina al subir de nivel */
    @Override
	protected int getMejoraStamina() {
        return 15 + AdicionalSTPescador;
    }
    
    /** Incremento de golpe al subir de nivel */
    @Override
	protected int getMejoraGolpe() {
        return 1;
    }
    
}
