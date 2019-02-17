package org.ArgentumOnline.server.npc;

import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.util.IniFile;

public class NpcLoader {
	
	private AojServer server;
	
    private static IniFile iniNPC = null;
    private static IniFile iniHostiles = null;
    private static IniFile iniBackup = null;
    
    public NpcLoader(AojServer server) {
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
        short npcType = ini.getShort(section, "NpcType");
        boolean comerciante = (ini.getInt(section, "Comercia") == 1);        
    	
    	if (npcType == Npc.NPCTYPE_BANQUERO)
    		return new NpcCashier(npcNumber, server);
    	
    	if (npcType == Npc.NPCTYPE_ENTRENADOR)
    		return new NpcTrainer(npcNumber, server);
    	
    	if (comerciante)
    		return new NpcMerchant(npcNumber, server);
    		
    	return new Npc(npcNumber, server);
    }
    
    public IniFile getIniFile(int npc_ind, boolean loadBackup) {
        if (loadBackup) {
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
            // Si esta en el backup, lo usamos. Sino, procedemos normalmente.
            if (!iniBackup.getString("NPC" + npc_ind, "Name").equals("")) {
				return iniBackup;
			}
        }
        if (npc_ind < 500) {
            if (iniNPC == null) {
                iniNPC = new IniFile();
                try {
                    iniNPC.load(Constants.DATDIR + java.io.File.separator + "NPCs.dat");
                } catch (java.io.FileNotFoundException e) {
                    e.printStackTrace();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            return iniNPC;
        }
        if (iniHostiles == null) {
            iniHostiles = new IniFile();
            try {
                //iniHostiles.load(Constants.DATDIR + java.io.File.separator + "NPCs-HOSTILES.dat");
                iniHostiles.load(Constants.DATDIR + java.io.File.separator + "NPCs.dat");
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return iniHostiles;
    }

}
