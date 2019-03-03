package org.ArgentumOnline.server;

public class UserAttributes {
	
	final static int MIN_ATTRIBUTE_VALUE = 6;
    final static int MAX_ATTRIBUTE_VALUE = 35;
    
    public enum Attribute {
    	
	    FUERZA,
	    AGILIDAD,
	    INTELIGENCIA,
	    CARISMA,
	    CONSTITUCION;
    	
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
	public void modify(Attribute attr, int amount) {
		this.userAttributes[attr.value()] += amount;
		if (this.userAttributes[attr.value()] < MIN_ATTRIBUTE_VALUE) {
			this.userAttributes[attr.value()] = MIN_ATTRIBUTE_VALUE;
		}
		if (this.userAttributes[attr.value()] > MAX_ATTRIBUTE_VALUE) {
			this.userAttributes[attr.value()] = MAX_ATTRIBUTE_VALUE;
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
