package org.ArgentumOnline.server;

import static org.ArgentumOnline.server.Constants.MAX_MASCOTAS_USER;

import java.util.ArrayList;
import java.util.List;

import org.ArgentumOnline.server.npc.Npc;

public class UserPets {
	
	private List<Npc> pets = new ArrayList<>();

	public List<Npc> getPets() {
		return pets;
	}

	public boolean hasPets() {
		return this.pets.size() > 0;		
	}

	public boolean isFullPets() {
		return this.pets.size() >= MAX_MASCOTAS_USER;
	}
	
	public void addPet(Npc pet) {
		this.pets.add(pet);
	}
	
	public int removeAll() {
		int count = pets.size();
		pets.forEach(pet -> {
			pet.releasePet();
			pet.quitarNPC();
		});
		pets.clear();
		return count;
	}
	
	/** Ordenar a las mascotas del usuario atacar a un Npc */
	public void petsAttackNpc(Npc targetNpc) {
		pets.forEach(pet -> pet.setPetTargetNpc(targetNpc));
	}

	public void petsFollowMaster() {
		pets.forEach(pet -> pet.followMaster());
	}

	/**
	 * Las mascotas que tienen como objetivo al Npc target, deben volver a seguir al
	 * amo
	 */
	public void petsFollowMaster(Npc targetNpc) {
		pets.stream()
			.filter(pet -> pet.getTargetNpc() == targetNpc)
			.forEach(pet -> pet.followMaster());
	}

	public void petsFollowMaster(short target) {
		pets.stream()
			.filter(pet -> pet.getTargetUser()== target)
			.forEach(pet -> {
				pet.setTargetUser(0);
				pet.followMaster();
			});
	}

	public void removePet(Npc npc) {
		if (pets.contains(npc)) {
			pets.remove(npc);
			npc.releasePet();
		}
	}
	
}
