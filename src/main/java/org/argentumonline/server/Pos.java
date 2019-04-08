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
package org.argentumonline.server;

/**
 * Position in a 2D map
 * @author gorlok
 */
public class Pos implements Constants {

	public byte x;
	public byte y;
	
	public static Pos xy(int x, int y) {
		return new Pos((byte)x, (byte)y);
	}

	public Pos(int x, int y) {
		this.x = (byte)x;
		this.y = (byte)y;
	}

	public boolean isValid() {
		return (this.x > 0) && (this.y > 0) && (this.x <= MAP_WIDTH) && (this.y <= MAP_HEIGHT);
	}

	public static boolean isValid(int x, int y) {
		return (x > 0) && (y > 0) && (x <= MAP_WIDTH) && (y <= MAP_HEIGHT);
	}

	public boolean inRangoVision(Pos pos) {
		return inRangoVision(pos.x, pos.y);
	}

	public boolean inRangoVision(int xx, int yy) {
		return Math.abs(this.x - xx) < MinXBorder &&
				Math.abs(this.y - yy) < MinYBorder;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("(x=")
				.append(this.x)
				.append(",y=")
				.append(this.y)
				.append(")")
				.toString();
	}

	public String toStringShort() {
		return new StringBuilder()
				.append("(")
				.append(this.x)
				.append(",")
				.append(this.y)
				.append(")")
				.toString();
	}
}
