package org.ArgentumOnline.server.classes;

public enum Clazz {

    Mage(new MagueClass()),        // Mago
    Cleric(new ClericClass()),      // Clérigo
    Warrior(new WarriorClass()),     // Guerrero
    Assassin(new AssassinClass()),     // Asesino
    Thief(new ThiefClass()),       // Ladrón
    Bard(new BardClass()),        // Bardo
    Druid(new DruidClass()),       // Druida
    Bandit(new BanditClass()),      // Bandido
    Paladin(new PaladinClass()),     // Paladín
    Hunter(new HunterClass()),      // Cazador
    Fisher(new FisherClass()),      // Pescador
    Blacksmith(new BlacksmithClass()),  // Herrero
    Lumberjack(new LumberjackClass()),  // Leñador
    Miner(new MinerClass()),       // Minero
    Carpenter(new CarpenterClass()),   // Carpintero
    Pirate(new PirateClass());      // Pirata

	private AbstractClazz clazz;
	
	private Clazz(AbstractClazz clazz) {
		this.clazz = clazz;
	}
	
	public byte id() {
		return (byte) (ordinal() + 1);
	}
    
	public AbstractClazz clazz() {
		return this.clazz;
	}
	
	public static Clazz[] values = Clazz.values();
	public static Clazz value(int i) {
		return values[i-1];
	}
	
	public static Clazz findByName(String name) {
		for (Clazz c : values) {
			if (c.clazz().getName().equalsIgnoreCase(name))
				return c;
		}
		return null;
	}
}
