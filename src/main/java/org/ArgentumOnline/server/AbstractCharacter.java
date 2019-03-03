/**
 * AbstractCharacter.java
 *
 * Created on 14 de septiembre de 2003, 21:15
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
