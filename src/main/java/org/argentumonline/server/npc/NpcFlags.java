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
package org.argentumonline.server.npc;

public class NpcFlags {

	public final static int FLAG_INV_RESPAWN = 0;
	public final static int FLAG_COMERCIA = 1;
	public final static int FLAG_ENVENENA = 2;
	public final static int FLAG_ATACABLE = 3;
	public final static int FLAG_HOSTIL = 4;
	public final static int FLAG_PUEDE_ATACAR = 5;
	public final static int FLAG_AFECTA_PARALISIS = 6;
	public final static int FLAG_GOLPE_EXACTO = 7;
	public final static int FLAG_NPC_ACTIVE = 8; // ¿Esta vivo?
	public final static int FLAG_FOLLOW = 9;
	public final static int FLAG_FACCION = 10;
	public final static int FLAG_OLD_HOSTILE = 11;
	public final static int FLAG_AGUA_VALIDA = 12;
	public final static int FLAG_TIERRA_INVALIDA = 13;
	public final static int FLAG_USE_AI_NOW = 14;
	public final static int FLAG_ATTACKING = 15;
	public final static int FLAG_BACKUP = 16;
	public final static int FLAG_RESPAWN_ORIG_POS = 17; // POS_ORIG
	public final static int FLAG_ENVENENADO = 18;
	public final static int FLAG_PARALIZADO = 19;
	public final static int FLAG_INVISIBLE = 20;
	public final static int FLAG_MALDICION = 21;
	public final static int FLAG_BENDICION = 22;
	public final static int FLAG_RESPAWN = 23;
	public final static int FLAG_LANZA_SPELLS = 24;
	public final static int FLAG_INMOVILIZADO = 25;
	
	public final static int MAX_FLAGS = 26;
	
	private boolean[] flags = new boolean[MAX_FLAGS];
	
	public void set(int flag, boolean value) {
		this.flags[flag] = value;
	}
	
	public boolean get(int flag) {
		return this.flags[flag];
	}
	
}
