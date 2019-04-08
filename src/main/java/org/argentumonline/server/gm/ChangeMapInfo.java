package org.argentumonline.server.gm;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.argentumonline.server.GameServer;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.map.MapConstraint;
import org.argentumonline.server.map.Terrain;
import org.argentumonline.server.map.Zone;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Log;

public class ChangeMapInfo {
	
	public static void changeMapInfoBackup(GameServer server, User admin, boolean doTheBackup) {
		// Command /MODMAPINFO BACKUP
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		
		Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado la información sobre el BackUp del mapa " + admin.pos().map);
        
		map.setBackup(doTheBackup);
		map.saveDatFile();
		
        admin.sendMessage("Mapa " + map.getMapNumber() + " Backup: " + (map.isBackup() ? "1" : "0"), FontType.FONTTYPE_INFO);
	}

	public static void changeMapInfoLand(GameServer server, User admin, String infoLand) {
		// Command /MODMAPINFO TERRENO
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		
		Optional<Terrain> terrain = Terrain.fromName(infoLand);
		if (terrain.isPresent()) {
			Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado la información del Terreno del mapa " 
					+ admin.pos().map + " a " + terrain.toString());
		
			map.setTerrain(terrain.get());
			map.saveDatFile();
			
	        admin.sendMessage("Mapa " + map.getMapNumber() + " Terreno: " + terrain.get(), FontType.FONTTYPE_INFO);
		} else {
			admin.sendMessage("Opciones válidas para Terreno: 'BOSQUE', 'DESIERTO', 'NIEVE'", FontType.FONTTYPE_INFO);
			admin.sendMessage("NOTA: el único valor significante es 'NIEVE', donde los personajes mueren de frío en el mapa.", 
					FontType.FONTTYPE_INFO);
		}
	}

	public static void changeMapInfoZone(GameServer server, User admin, String infoZone) {
		// Command /MODMAPINFO ZONA
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		
		Optional<Zone> zone = Zone.fromName(infoZone);
		if (zone.isPresent()) {
			Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado la información de la Zona del mapa " 
					+ admin.pos().map + " a " + zone.toString());
		
			map.setZone(zone.get());
			map.saveDatFile();
			
	        admin.sendMessage("Mapa " + map.getMapNumber() + " Zona: " + zone.get(), FontType.FONTTYPE_INFO);
		} else {
			var values = Arrays.stream(Zone.values()).map(Zone::toString).collect(Collectors.toList());
			admin.sendMessage("Opciones válidas para Zona: " + values, FontType.FONTTYPE_INFO);
			admin.sendMessage("NOTA: el único valor significante es 'DUNGEON', donde no hay efecto de lluvia.", 
					FontType.FONTTYPE_INFO);
		}
	}

	public static void changeMapInfoNoInvi(GameServer server, User admin, boolean noInvisible) {
		// Command /MODMAPINFO INVISINEFECTO
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado si está prohibido usar Invisibilidad en el Mapa " 
				+ admin.pos().map + " a " + noInvisible);

		map.setInviSinEfecto(noInvisible);
		map.saveDatFile();
		
        admin.sendMessage("Mapa " + map.getMapNumber() + " InvisibilidadSinEfecto: " + map.isInviSinEfecto(), 
        		FontType.FONTTYPE_INFO);
	}

	public static void changeMapInfoNoMagic(GameServer server, User admin, boolean noMagic) {
		// Command /MODMAPINFO MAGIASINEFECTO
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado si está prohibido usar Magia en el Mapa " 
				+ admin.pos().map + " a " + noMagic);

		map.setMagiaSinEfecto(noMagic);
		map.saveDatFile();
		
        admin.sendMessage("Mapa " + map.getMapNumber() + " MagiaSinEfecto: " + map.isMagiaSinEfecto(), 
        		FontType.FONTTYPE_INFO);
	}

	public static void changeMapInfoNoResu(GameServer server, User admin, boolean noResu) {
		// Command /MODMAPINFO RESUSINEFECTO
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado si está prohibido usar Resucitar en el Mapa " 
				+ admin.pos().map + " a " + noResu);

		map.setResuSinEfecto(noResu);
		map.saveDatFile();
		
        admin.sendMessage("Mapa " + map.getMapNumber() + " ResuSinEfecto: " + map.isResuSinEfecto(), 
        		FontType.FONTTYPE_INFO);
	}

	public static void changeMapInfoPK(GameServer server, User admin, boolean noPK) {
		// Command /MODMAPINFO PK
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado el estado de MapaSeguro del Mapa " 
				+ admin.pos().map + " a " + noPK);

		map.setSafeMap(!noPK);
		map.saveDatFile();
		
        admin.sendMessage("Mapa " + map.getMapNumber() + " MapaSeguro: " + map.isSafeMap(), 
        		FontType.FONTTYPE_INFO);
		
	}

	public static void changeMapInfoRestricted(GameServer server, User admin, String status) {
		// Command /MODMAPINFO RESTRINGIR
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		Map map = server.getMap(admin.pos().map);
		if (map == null) {
			return;
		}
		
		MapConstraint constraint = MapConstraint.fromName(status);
		if (constraint != null) {
			Log.logGM(admin.getUserName(), admin.getUserName() + " ha cambiado la Restricción del mapa " 
					+ admin.pos().map + " a " + constraint.toString());
		
			map.setRestricted(constraint);
			map.saveDatFile();
			
	        admin.sendMessage("Mapa " + map.getMapNumber() + " Restricción: " + constraint.toString(), 
	        		FontType.FONTTYPE_INFO);
		} else {
			var values = String.join(", ", MapConstraint.getNames());
			admin.sendMessage("Opciones válidas para Restricción: " + values, FontType.FONTTYPE_INFO);
		}
	}
	
}
