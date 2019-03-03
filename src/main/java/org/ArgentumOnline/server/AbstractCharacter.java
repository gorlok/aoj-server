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
package org.ArgentumOnline.server;

import org.ArgentumOnline.server.map.MapPos;

/**
 * Character base class
 * @author gorlok
 */
public abstract class AbstractCharacter implements Constants {

	/** Character universal id. */
	private short id = 0;

    protected MapPos pos  = MapPos.empty();

    protected CharInfo infoChar = new CharInfo();
    protected CharInfo origChar = new CharInfo();

	public short getId() {
		return this.id;
	}

	protected void setId(short id) {
		this.id = id;
	}

    public CharInfo infoChar() {
        return this.infoChar;
    }

    public CharInfo origChar() {
    	return this.origChar;
    }

    public MapPos pos() {
        return this.pos;
    }

    protected void setPos(MapPos newPos) {
    	pos().set(newPos.map, newPos.x, newPos.y);
    }

}
