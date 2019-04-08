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

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.protocol.TrainerCreatureListResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.IniFile;

/**
 * @author gorlok
 */
public class NpcTrainer extends Npc {

	// list of creatures for train
    private TrainerMascot[] creatureList;
    
    short petsCount    = 0;
    Npc pets[] = new Npc[MAX_TRAINER_PETS];
    
	protected NpcTrainer(int npc_number, GameServer server) {
		super(npc_number, server);
	}

    public short petsCount() {
        return this.petsCount;
    }
	
    public void removePet(Npc pet) {
        for (int i = 0; i < this.pets.length; i++) {
            if (this.pets[i] == pet) {
                this.pets[i] = null;
                this.petsCount--;
                return;
            }
        }
    }
    
    private void addPet(Npc pet) {
    	for (int i = 0; i < this.pets.length; i++) {
    		if (this.pets[i] == null) {
    			this.pets[i] = pet;
                pet.petNpcOwner(this);
                this.petsCount++;
                return;
    		}
    	}
    }
    
    private  int creaturesCount() {
        return this.creatureList.length;
    }

    private short creatureIndex(short slot) {
        if (slot > 0 && slot <= this.creatureList.length) {
			return this.creatureList[slot-1].npc_index;
		} 
		return 0;
    }
    
    public void sendTrainerCreatureList(User user) {
    	StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.creatureList.length; i++) {
            sb.append(this.creatureList[i].npc_name)
              .append(Constants.NULL_CHAR); // separator
        }
        if (sb.length() > 0) {
        	// remove last separator (extra)
        	sb.deleteCharAt(sb.length()-1);
        }
        
        user.sendPacket(new TrainerCreatureListResponse(sb.toString()));
    }
    
    @Override
    protected void loadNpc(IniFile ini, int npc_ind) {
    	super.loadNpc(ini, npc_ind);
        String section = "NPC" + npc_ind;
    	
        int count = (byte) ini.getShort(section, "NroCriaturas");
        if (count > MAX_TRAINER_CREATURES) {
        	count = MAX_TRAINER_CREATURES;
        }
        this.creatureList = new TrainerMascot[count];
        
        for (int i = 0; i < count; i++) {
        	short npcIndex = ini.getShort(section, "CI" + (i+1));
        	String npcName = ini.getString(section, "CN" + (i+1));
        	this.creatureList[i] = new TrainerMascot(npcIndex, npcName);
        }
    }
    
    @Override
    public void backupNpc(IniFile ini) {
    	super.backupNpc(ini);
        String section = "NPC" + this.npcNumber;
        
    	ini.setValue(section, "NroCriaturas", this.creatureList.length);
    	int size = 0;
    	for (TrainerMascot mascot : this.creatureList) {
    		size++;
            ini.setValue(section, "CI" + size, mascot.npc_index);
            ini.setValue(section, "CN" + size, mascot.npc_name);
    	};
    }
    
    public boolean isTrainerIsFull() {
    	return this.petsCount() >= MAX_TRAINER_PETS;	
    }

	public void spawnTrainerPet(short slot) {
		if (petsCount() < User.MAX_TRAINER_PETS) {
			if (slot > 0 && slot <= creaturesCount()) {
				Npc trainerPet = Npc.spawnNpc(creatureIndex(slot), 
						pos(), /*fx*/ true, /*respawn*/ false);
				if (trainerPet != null) {
					addPet(trainerPet);
				}
			}
		}
	}
    
    
}
