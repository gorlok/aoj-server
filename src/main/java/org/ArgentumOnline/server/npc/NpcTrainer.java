package org.ArgentumOnline.server.npc;

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.util.IniFile;

public class NpcTrainer extends Npc {

    // <<<<Entrenadores>>>>>
    byte m_criaturas_entrenador_cant = 0;
    TrainerMascot[] m_criaturas_entrenador;
    
    short petsCount    = 0;
    Npc pets[] = new Npc[MAX_MASCOTAS_ENTRENADOR];
    
	protected NpcTrainer(int npc_numero, GameServer server) {
		super(npc_numero, server);
	}

    public short getCantMascotas() {
        return this.petsCount;
    }
	
    public void quitarMascotaNpc(Npc mascota) {
        for (int i = 0; i < this.pets.length; i++) {
            if (this.pets[i] == mascota) {
                this.pets[i] = null;
                this.petsCount--;
                return;
            }
        }
    }
    
    public void agregarMascota(Npc mascota) {
    	for (int i = 0; i < this.pets.length; i++) {
    		if (this.pets[i] == null) {
    			this.pets[i] = mascota;
                mascota.setPetNpcOwner(this);
                this.petsCount++;
                return;
    		}
    	}
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
    public void enviarListaCriaturas(Player cliente) {
    	List<Object> criaturas = new LinkedList<Object>();
        criaturas.add(this.m_criaturas_entrenador_cant);
        for (int i = 0; i < this.m_criaturas_entrenador_cant; i++) {
            criaturas.add(this.m_criaturas_entrenador[i].npc_name);
        }
      //  cliente.enviar(MSG_LSTCRI, criaturas);
    }
    
    @Override
    protected void leerNpc(IniFile ini, int npc_ind) {
    	super.leerNpc(ini, npc_ind);
        String section = "NPC" + npc_ind;
    	
        //Entrenador
        this.m_criaturas_entrenador = new TrainerMascot[MAX_CRIATURAS_ENTRENADOR];
        this.m_criaturas_entrenador_cant = (byte) ini.getShort(section, "NroCriaturas");
        
        if (this.m_criaturas_entrenador_cant > MAX_CRIATURAS_ENTRENADOR) {
        	this.m_criaturas_entrenador_cant = MAX_CRIATURAS_ENTRENADOR;
        }
        
        if (this.m_criaturas_entrenador_cant > 0) {
            for (int c = 0; c < this.m_criaturas_entrenador_cant; c++) {
            	short npcIndex = ini.getShort(section, "CI" + (c+1));
            	String npcName = ini.getString(section, "CN" + (c+1));
            	this.m_criaturas_entrenador[c] = new TrainerMascot(npcIndex, npcName);
            }
        }
    }
    
    public boolean isTrainerIsFull() {
    	return this.getCantMascotas() >= MAX_MASCOTAS_ENTRENADOR;	
    }

	public void spawnTrainerPet(short slot) {
		if (getCantMascotas() < Player.MAX_MASCOTAS_ENTRENADOR) {
			if (slot > 0 && slot <= getCantCriaturas()) {
				Npc criatura = Npc.spawnNpc(getCriaturaIndex(slot), pos(), true, false);
				if (criatura != null) {
					agregarMascota(criatura);
				}
			}
		}
	}
    
    
}
