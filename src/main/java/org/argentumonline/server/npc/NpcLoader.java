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
import org.argentumonline.server.util.IniFile;

public class NpcLoader {
	
	private GameServer server;
	
    public NpcLoader(GameServer server) {
    	this.server = server;
	}
    
    /**
     * NPC factory method
     * @param npcNumber
     * @return a new NPC of the correct type
     */
    public Npc createNpc(int npcNumber) {
        IniFile ini = getIniFile(npcNumber, this.server.isLoadBackup());
        String section = "NPC" + npcNumber;
        NpcType npcType = NpcType.value(ini.getShort(section, "NpcType"));
        boolean comerciante = (ini.getInt(section, "Comercia") == 1);        
    	
    	if (npcType == NpcType.NPCTYPE_CASHIER)
    		return new NpcCashier(npcNumber, server);
    	
    	if (npcType == NpcType.NPCTYPE_TRAINER)
    		return new NpcTrainer(npcNumber, server);
    	
    	if (npcType == NpcType.NPCTYPE_GAMBLER)
    		return new NpcGambler(npcNumber, server);
    	
    	if (comerciante)
    		return new NpcMerchant(npcNumber, server);
    		
    	return new Npc(npcNumber, server);
    }
    
    public IniFile getIniFile(int npc_ind, boolean loadBackup) {
        if (loadBackup) {
        	IniFile iniBackup = openIniBackup();
            // Si esta en el backup, lo usamos. Sino, procedemos normalmente.
            if (!iniBackup.getString("NPC" + npc_ind, "Name").equals("")) {
				return iniBackup;
			}
        }
        if (npc_ind < 500) {
            return openIniNpc();
        }
        return openIniHostiles();
    }

    private static IniFile iniNPC = null;
    private static IniFile iniHostiles = null;
    private static IniFile iniBackup = null;
    
	private synchronized static IniFile openIniNpc() {
		if (iniNPC == null) {
		    iniNPC = new IniFile();
		    try {
		        iniNPC.load(Constants.DAT_DIR + java.io.File.separator + "NPCs.dat");
		    } catch (java.io.FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (java.io.IOException e) {
		        e.printStackTrace();
		    }
		}
		return iniNPC;
	}

	private synchronized static IniFile openIniBackup() {
		if (iniBackup == null) {
		    iniBackup = new IniFile();
		    try {
		        iniBackup.load("worldBackup" + java.io.File.separator + "backNPCs.dat");
		    } catch (java.io.FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (java.io.IOException e) {
		        e.printStackTrace();
		    }
		}
		return iniBackup;
	}

	private synchronized static IniFile openIniHostiles() {
		if (iniHostiles == null) {
            iniHostiles = new IniFile();
            try {
                //iniHostiles.load(Constants.DATDIR + java.io.File.separator + "NPCs-HOSTILES.dat");
                iniHostiles.load(Constants.DAT_DIR + java.io.File.separator + "NPCs.dat");
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return iniHostiles;
	}
}
