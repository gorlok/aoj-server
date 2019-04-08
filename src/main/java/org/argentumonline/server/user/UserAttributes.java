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

public class UserAttributes {
	
	final static int MIN_ATTRIBUTE_VALUE = 6;
    final static int MAX_ATTRIBUTE_VALUE = 35;
    
    public enum Attribute {
    	
	    STRENGTH,
	    AGILITY,
	    INTELIGENCE,
	    CHARISMA,
	    CONSTITUTION;
    	
    	private static final Attribute[] VALUES = Attribute.values();
    	
    	public static Attribute value(int value) {
    		return VALUES[value];
    	}
    	
    	public byte value() {
    		return (byte) (ordinal());
    	}
    	
    }

	private int userAttributes[] = new int[Attribute.VALUES.length];
	
	private int userAttributesBackup[] = new int[Attribute.VALUES.length];
	
	public byte get(Attribute attr) {
		return (byte) this.userAttributes[attr.value()];
	}
	
	public byte getBackup(Attribute attr) {
		return (byte) this.userAttributesBackup[attr.value()];
	}
	
	public void set(Attribute attr, int value) {
		this.userAttributes[attr.value()] = (byte) value;
	}
	
	/**
	 * Increment or decrement an attribute.
	 * If amount is positive, attribute is incremented. Otherwise, is decremented.
	 * Attribute value is capped at MIN_ATTRIBUTE_VALUE and MAX_ATTRIBUTE_VALUE.
	 * @param attr is the attribute to modify
	 * @param amount to increment o decrement from the attribute
	 */
	public void modifyByEffect(Attribute attr, int amount) {
		this.userAttributes[attr.value()] += amount;
		
		// el valor del atributo no puede ser menor que MIN_ATTRIBUTE_VALUE
		if (this.userAttributes[attr.value()] < MIN_ATTRIBUTE_VALUE) {
			this.userAttributes[attr.value()] = MIN_ATTRIBUTE_VALUE;
		}
		
		// el valor del atributo no puede ser mayor que MAX_ATTRIBUTE_VALUE
		if (this.userAttributes[attr.value()] > MAX_ATTRIBUTE_VALUE) {
			this.userAttributes[attr.value()] = MAX_ATTRIBUTE_VALUE;
		}
		
		// el valor del atributo no puede ser mayor que el doble de su valor original
		if (this.userAttributes[attr.value()] > 2 * this.userAttributesBackup[attr.value()]) {
			this.userAttributes[attr.value()] = 2 * this.userAttributesBackup[attr.value()];
		}
	}
	
	public boolean hasValidAttributes() {
		for (int value : this.userAttributes) {
			if (value < 1 || value > 18) {
				return false;
			}
		}
		return true;
	}

	public void backupAttributes() {
		for (int i = 0; i < this.userAttributes.length; i++) {
			this.userAttributesBackup[i] = this.userAttributes[i];
		}
	}

	public void restoreAttributes() {
		for (int i = 0; i < this.userAttributes.length; i++) {
			this.userAttributes[i] = this.userAttributesBackup[i];
		}
	}

}
