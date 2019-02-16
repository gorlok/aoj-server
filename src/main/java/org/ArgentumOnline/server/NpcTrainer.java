package org.ArgentumOnline.server;

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.util.IniFile;

public class NpcTrainer extends Npc {

    // <<<<Entrenadores>>>>>
    byte m_criaturas_entrenador_cant = 0;
    TrainerMascot m_criaturas_entrenador[] = new TrainerMascot[MAX_CRIATURAS_ENTRENADOR];
    
	public NpcTrainer(int npc_numero, boolean loadBackup, AojServer server) {
		super(npc_numero, loadBackup, server);
		
        initCriaturasEntrenador();		
	}

    public short getCantCriaturas() {
        return this.m_criaturas_entrenador_cant;
    }

    public short getCriaturaIndex(short slot) {
        if (slot > 0 && slot <= MAX_CRIATURAS_ENTRENADOR) {
			return this.m_criaturas_entrenador[slot-1].npc_index;
		} 
		return 0;
    }
    
	/** Envia la lista de criaturas del entrenador. */
    public void enviarListaCriaturas(Client cliente) {
    	List<Object> criaturas = new LinkedList<Object>();
        criaturas.add(this.m_criaturas_entrenador_cant);
        for (int i = 0; i < this.m_criaturas_entrenador_cant; i++) {
            criaturas.add(this.m_criaturas_entrenador[i].npc_name);
        }
      //  cliente.enviar(MSG_LSTCRI, criaturas);
    }
    
    public void initCriaturasEntrenador() {
        this.m_criaturas_entrenador_cant = 0;
        for (int i = 0; i < MAX_CRIATURAS_ENTRENADOR; i++) {
			this.m_criaturas_entrenador[i] = new TrainerMascot();
		}
    }
    
    @Override
    protected void leerNpc(IniFile ini, int npc_ind) {
    	super.leerNpc(ini, npc_ind);
    	
        String section = "NPC" + npc_ind;
    	
        //Entrenador
        this.m_criaturas_entrenador_cant = (byte) ini.getShort(section, "NroCriaturas");
        if (this.m_criaturas_entrenador_cant > 0) {
            for (int c = 0; c < this.m_criaturas_entrenador_cant; c++) {
                this.m_criaturas_entrenador[c].npc_index = ini.getShort(section, "CI" + (c+1));
                this.m_criaturas_entrenador[c].npc_name = ini.getString(section, "CN" + (c+1));
            }
        }
    }
    
    @Override
    public void resetNpcMainInfo() {
    	super.resetNpcMainInfo();

        initCriaturasEntrenador();
    }
    
    public boolean isTrainerIsFull() {
    	return this.getCantMascotas() >= MAXMASCOTASENTRENADOR;	
    }

	void spawnTrainerPet(short slot, Npc npc) {
		if (npc.getCantMascotas() < Client.MAXMASCOTASENTRENADOR) {
			if (slot > 0 && slot <= getCantCriaturas()) {
				Npc criatura = Npc.spawnNpc(getCriaturaIndex(slot), npc.getPos(), true, false);
				if (criatura != null) {
					npc.agregarMascota(criatura);
				}
			}
		}
	}
    
    
}
