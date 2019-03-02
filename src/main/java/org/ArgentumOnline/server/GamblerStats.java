package org.ArgentumOnline.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class GamblerStats {
	private static Logger log = LogManager.getLogger();

	private long ganancias;
	private long perdidas;
	private long jugadas;
	
	public GamblerStats() {
		this.loadStats();
	}
	
	public long getGanancias() {
		return ganancias;
	}
	
	public long getPerdidas() {
		return perdidas;
	}
	
	public long getJugadas() {
		return jugadas;
	}
	
	public void loadStats() {
    	log.trace("loading gambler stats");
    	
		try {
			IniFile ini = new IniFile(Constants.DATDIR + File.separator + "apuestas.dat");
			
	        ganancias = ini.getLong("Main", "Ganancias");
		    perdidas = ini.getLong("Main", "Perdidas");
		    jugadas = ini.getLong("Main", "Jugadas");
	        
		} catch (FileNotFoundException e) {
			log.fatal(e);
		} catch (IOException e) {
			log.fatal(e);
		}
	}
	
	private void saveStats() {
    	log.trace("saving gambler stats");
    	
		try {
			IniFile ini = new IniFile(Constants.DATDIR + File.separator + "apuestas.dat");
			
	        ini.setValue("Main", "Ganancias", ganancias);
		    ini.setValue("Main", "Perdidas", perdidas);
		    ini.setValue("Main", "Jugadas", jugadas);
	        
		} catch (FileNotFoundException e) {
			log.fatal(e);
		} catch (IOException e) {
			log.fatal(e);
		}
	}

	public void incrementLost(int gold) {
		this.perdidas += gold;
		this.jugadas++;
		
		saveStats();
	}

	public void incrementWins(int gold) {
		this.ganancias += gold;
		this.jugadas++;
		
		saveStats();
	}

}
