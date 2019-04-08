/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.gm;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Constants;
import org.argentumonline.server.protocol.GuildChatResponse;
import org.argentumonline.server.protocol.ShowMOTDEditionFormResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.IniFile;
import org.argentumonline.server.util.Log;

public class Motd {
	
	private static Logger log = LogManager.getLogger();
	
    private List<String> m_motd = new ArrayList<String>();
    
    public Motd() {
	}

    public void loadMotd() {
    	log.trace("loading MOTD");
        try {
            String msg;
            this.m_motd.clear();
            IniFile ini = new IniFile(Constants.DAT_DIR + java.io.File.separator + "Motd.ini");
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
    
    private void guardarMotd() {
        try {
            IniFile ini = new IniFile();
            ini.setValue("INIT", "NumLines", this.m_motd.size());
            int i = 0;
            for (Object element : this.m_motd) {
                ini.setValue("MOTD", "Line"+(++i), (String)element);
            }
            ini.store(Constants.DAT_DIR + java.io.File.separator + "Motd.ini");
        } catch (Exception e) {
            log.fatal("ERROR EN guardarMOTD()", e);
        }
    }

    private List<String> getMOTD() {
    	return this.m_motd;
    }
    
    private void setMOTD(List<String> motd) {
    	this.m_motd.clear();
    	this.m_motd.addAll(motd);
    }

	public void startUpdateMOTD(User user) {
		// Iniciar el cambio de MOTD
		// Comando /MOTDCAMBIA
		if (!user.isGod()) {
			return;
		}
		String CRLF = "" + (char) 13 + (char) 10;
		Log.logGM(user.getUserName(), "/MOTDCAMBIA");
		StringBuffer sb = new StringBuffer();
		List<String> motd = getMOTD();
		if (!motd.isEmpty()) {
			for (String line : motd) {
				sb.append(line + CRLF);
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		user.sendPacket(new ShowMOTDEditionFormResponse(sb.toString()));
	}

	public void updateMOTD(User user, String s) {
		// Finalizar el cambio de MOTD
		// Comando ZMOTD
		if (!user.isGod()) {
			return;
		}
		String CRLF = "" + (char) 13 + (char) 10;
		Log.logGM(user.getUserName(), "ZMOTD " + s);
		List<String> motd = new ArrayList<String>();
		for (StringTokenizer st = new StringTokenizer(s, CRLF); st.hasMoreTokens();) {
			motd.add(st.nextToken());
		}
		setMOTD(motd);
		guardarMotd();
		user.sendMessage("MOTD actualizado.", FontType.FONTTYPE_INFO);
	}

	public void showMOTD(User user) {
		// Comando /MOTD
		// Envia los mensajes del dia.
		List<String> motd = getMOTD();
		if (motd.isEmpty()) {
			return;
		}
		user.sendPacket(new GuildChatResponse("Mensajes de entrada:"));
		for (String line : motd) {
			user.sendPacket(new GuildChatResponse(line));
		}
	}
    
}
