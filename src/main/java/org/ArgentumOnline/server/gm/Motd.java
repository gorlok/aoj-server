package org.ArgentumOnline.server.gm;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.Constants;
import org.ArgentumOnline.server.util.FontType;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Motd {
	private static Logger log = LogManager.getLogger();
	
    private List<String> m_motd = new Vector<String>();
    
    public Motd() {
	}

    public void loadMotd() {
    	log.trace("loading MOTD");
        try {
            String msg;
            this.m_motd.clear();
            IniFile ini = new IniFile(Constants.DATDIR + java.io.File.separator + "Motd.ini");
            short cant = ini.getShort("INIT", "NumLines");
            for (int i = 1; i <= cant; i++) {
                msg = ini.getString("MOTD", "Line"+i, "");
                this.m_motd.add(msg);
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    void guardarMotd() {
        try {
            IniFile ini = new IniFile();
            ini.setValue("INIT", "NumLines", this.m_motd.size());
            int i = 0;
            for (Object element : this.m_motd) {
                ini.setValue("MOTD", "Line"+(++i), (String)element);
            }
            ini.store(Constants.DATDIR + java.io.File.separator + "Motd.ini");
        } catch (Exception e) {
            log.fatal("ERROR EN guardarMOTD()", e);
        }
    }

    List<String> getMOTD() {
    	return this.m_motd;
    }
    
    void setMOTD(List<String> motd) {
    	this.m_motd.clear();
    	this.m_motd.addAll(motd);
    }

	public void doIniciarCambiarMOTD(Client client) {
		// Iniciar el cambio de MOTD
		// Comando /MOTDCAMBIA
		if (!client.esDios()) {
			return;
		}
		String CRLF = "" + (char) 13 + (char) 10;
		Log.logGM(client.getNick(), "/MOTDCAMBIA");
		StringBuffer sb = new StringBuffer();
		List<String> motd = getMOTD();
		if (!motd.isEmpty()) {
			for (String line : motd) {
				sb.append(line + CRLF);
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		// enviar(MSG_ZMOTD, sb.toString());
	}

	public void doFinCambiarMOTD(Client client, String s) {
		// Finalizar el cambio de MOTD
		// Comando ZMOTD
		String CRLF = "" + (char) 13 + (char) 10;
		Log.logGM(client.getNick(), "ZMOTD " + s);
		Vector<String> motd = new Vector<String>();
		for (StringTokenizer st = new StringTokenizer(s, CRLF); st.hasMoreTokens();) {
			motd.add(st.nextToken());
		}
		setMOTD(motd);
		guardarMotd();
		client.enviarMensaje("MOTD actualizado.", FontType.INFO);
	}

	public void doEnviarMOTD(Client client) {
		// Comando /MOTD
		// Envia los mensajes del dia.
		List<String> motd = getMOTD();
		if (motd.isEmpty()) {
			return;
		}
		client.enviarMensaje("Mensaje del dia:", FontType.INFO);
		for (String line : motd) {
			// enviar(MSG_MOTD, line);
			// FIXME
		}
	}
    
}
