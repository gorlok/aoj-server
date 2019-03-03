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
package org.ArgentumOnline.server;

public class UserArea {

	public int areaID = 0;

	public int areaPerteneceX = 0;
	public int areaPerteneceY = 0;

	public int areaRecibeX = 0;
	public int areaRecibeY = 0;

	public int minX = 0;
	public int minY = 0;

	/**
	 * JAO: with this, we set the user area ID
	 */
	public void setArea(int id) {
		if (Constants.DEBUG)
			System.out.println("AREAID:" + id);
		this.areaID = id;
	}

	/**
	 * JAO: setter in X area
	 */
	public void setAreaPerteneceX(int value) {
		this.areaPerteneceX = value;
	}

	/**
	 * JAO: setter in Y area
	 */

	public void setAreaPerteneceY(int value) {
		this.areaPerteneceY = value;
	}

	/**
	 * JAO: adyacent X user area
	 */
	public void setAreaRecibeX(int value) {
		this.areaRecibeX = value;
	}

	/**
	 * JAO: adyacent Y user area
	 */
	public void setAreaRecibeY(int value) {
		this.areaRecibeY = value;
	}

	/**
	 * JAO: min x pos area
	 */
	public void setMinX(int value) {
		this.minX = value;
	}

	/**
	 * JAO: min y pos area
	 */
	public void setMinY(int value) {
		this.minY = value;
	}

	/**
	 * JAO: return the area id
	 */
	public int getArea() {
		return this.areaID;
	}

	/**
	 * JAO: give the area in X
	 */
	public int getAreaPerteneceX() {
		return this.areaPerteneceX;
	}

	/**
	 * JAO: give the area in Y
	 */
	public int getAreaPerteneceY() {
		return this.areaPerteneceY;
	}

	/**
	 * JAO: give the adyacent area X
	 */
	public int getAreaRecibeX() {
		return this.areaRecibeX;
	}

	/**
	 * JAO: give the adyacent area Y
	 */
	public int getAreaRecibeY() {
		return this.areaRecibeY;
	}

	/**
	 * JAO: return the lowest value in X
	 */
	public int getMinX() {
		return this.minX;
	}

	/**
	 * JAO: return the lowest value in Y
	 */
	public int getMinY() {
		return this.minY;
	}

}
