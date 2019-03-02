package org.ArgentumOnline.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 */
public class GamblerStats {
	private static final String APUESTAS_DAT = "apuestas.dat";

	private static Logger log = LogManager.getLogger();

	private long ganancias = 0;
	private long perdidas = 0;
	private long jugadas = 0;

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
			IniFile ini = new IniFile(Constants.DATDIR + File.separator + APUESTAS_DAT);

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
			if (!Files.exists(Paths.get(Constants.DATDIR + File.separator + APUESTAS_DAT))) {
				Files.createFile(Paths.get(Constants.DATDIR + File.separator + APUESTAS_DAT));
			}
			IniFile ini = new IniFile(Constants.DATDIR + File.separator + APUESTAS_DAT);

			ini.setValue("Main", "Ganancias", ganancias);
			ini.setValue("Main", "Perdidas", perdidas);
			ini.setValue("Main", "Jugadas", jugadas);
			
			ini.store(Constants.DATDIR + File.separator + APUESTAS_DAT);
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
