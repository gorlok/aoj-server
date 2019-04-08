package org.argentumonline.server.user;

import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;

public enum FactionArmors {

	// FACCION IMPERIAL:
	ARMADURA_IMPERIAL_1("Armadura Imperial 1"),
	ARMADURA_IMPERIAL_2("Armadura Imperial 2"),
	ARMADURA_IMPERIAL_3("Armadura Imperial 3"), // ENANO / GNOMO
	TUNICA_MAGO_IMPERIAL("Túnica Mago Imperial"),
	TUNICA_MAGO_IMPERIAL_ENANOS("Túnica Mago Imperial Enanos"),
	
    // FACCION CAOS:
	ARMADURA_CAOS_1("Armadura Caos 1"),
	ARMADURA_CAOS_2("Armadura Caos 2"),
	ARMADURA_CAOS_3("Armadura Caos 3"), // ENANO / GNOMO
	TUNICA_MAGO_CAOS("Túnica Mago Caos"),
	TUNICA_MAGO_CAOS_ENANOS("Túnica Mago Caos Enanos");
	
	public static short[] factionArmors = new short[FactionArmors.values().length];
	
	private String name;
	FactionArmors(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static void loadFactionArmors(IniFile ini) {
		factionArmors[FactionArmors.ARMADURA_IMPERIAL_1.ordinal()] = ini.getShort("INIT", "ArmaduraImperial1");
		factionArmors[FactionArmors.ARMADURA_IMPERIAL_2.ordinal()] = ini.getShort("INIT", "ArmaduraImperial2");
		factionArmors[FactionArmors.ARMADURA_IMPERIAL_3.ordinal()] = ini.getShort("INIT", "ArmaduraImperial3");
		factionArmors[FactionArmors.TUNICA_MAGO_IMPERIAL.ordinal()] = ini.getShort("INIT", "TunicaMagoImperial");
		factionArmors[FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS.ordinal()] = ini.getShort("INIT", "TunicaMagoImperialEnanos");
		factionArmors[FactionArmors.ARMADURA_CAOS_1.ordinal()] = ini.getShort("INIT", "ArmaduraCaos1");
		factionArmors[FactionArmors.ARMADURA_CAOS_2.ordinal()] = ini.getShort("INIT", "ArmaduraCaos2");
		factionArmors[FactionArmors.ARMADURA_CAOS_3.ordinal()] = ini.getShort("INIT", "ArmaduraCaos3");
		factionArmors[FactionArmors.TUNICA_MAGO_CAOS.ordinal()] = ini.getShort("INIT", "TunicaMagoCaos");
		factionArmors[FactionArmors.TUNICA_MAGO_CAOS_ENANOS.ordinal()] = ini.getShort("INIT", "TunicaMagoCaosEnanos");
	}
	
	public static short getFactionArmor(FactionArmors factiorArmor) {
		return factionArmors[factiorArmor.ordinal()];
	}
    
	public static void sendFactionArmor(User admin, FactionArmors factiorArmor) {
		String msg = new StringBuilder()
				.append(factiorArmor.getName())
				.append(" es ")
				.append(factionArmors[factiorArmor.ordinal()])
				.toString();
		admin.sendMessage(msg, FontType.FONTTYPE_INFO);
	}
	
	public static void updateFactionArmor(User admin, FactionArmors factiorArmor, short armorObjIdx) {
		factionArmors[factiorArmor.ordinal()] = armorObjIdx;
		
		String msg = new StringBuilder()
				.append(factiorArmor.getName())
				.append(" ha sido actualizada")
				.toString();
		admin.sendMessage(msg, FontType.FONTTYPE_INFO);
	}
	
}