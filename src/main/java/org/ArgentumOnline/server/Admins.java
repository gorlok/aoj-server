package org.ArgentumOnline.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Admins {
	private static Logger log = LogManager.getLogger();
	
    private List<String> m_dioses = new ArrayList<>();
    private List<String> m_semidioses = new ArrayList<>();
    private List<String> m_consejeros = new ArrayList<>();
    
    private List<String> m_nombresInvalidos = new Vector<String>();

    public boolean esDios(String name) {
        return this.m_dioses.contains(name.toUpperCase());
    }
    
    public boolean esSemiDios(String name) {
        return this.m_semidioses.contains(name.toUpperCase());
    }
    
    public boolean esConsejero(String name) {
        return this.m_consejeros.contains(name.toUpperCase());
    }
    
    public boolean nombrePermitido(String nombre) {
        return (!this.m_nombresInvalidos.contains(nombre.toUpperCase()));
    }
    
    public void loadInvalidNamesList() {
    	log.trace("loading invalid names list");
        this.m_nombresInvalidos.clear();
        try {
            BufferedReader f = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(Constants.DATDIR + File.separator + "NombresInvalidos.txt")));
            try {
                String str = f.readLine();
                if (str == null) {
					return;
				}
                str = str.trim().toUpperCase();
                if (!"".equals(str)) {
					this.m_nombresInvalidos.add(str);
				}
            } finally {
                f.close();
            }
        } catch (java.io.FileNotFoundException e) {
            log.warn("Error abriendo archivo de nombres inválidos");
            e.printStackTrace();
        } catch (java.io.IOException e) {
            log.warn("Error leyendo archivo de nombres inválidos");
            e.printStackTrace();
        }
    }
    
    public void loadAdmins() {
    	log.trace("loading admins");
        try {
            // Limpiar las listas de admins.
            this.m_dioses.clear();
            this.m_semidioses.clear();
            this.m_consejeros.clear();
            
            // Cargar dioses:
            IniFile ini = new IniFile(Constants.DATDIR + java.io.File.separator + "Server.ini");
            short cant = ini.getShort("DIOSES", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("DIOSES", "Dios"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.m_dioses.add(nombre);
				}
            }
            // Cargar semidioses:
            cant = ini.getShort("SEMIDIOSES", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("SEMIDIOSES", "Semidios"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.m_semidioses.add(nombre);
				}
            }
            // Cargar consejeros:
            cant = ini.getShort("CONSEJEROS", "Cant");
            for (int i = 1; i <= cant; i++) {
                String nombre = ini.getString("CONSEJEROS", "Consejero"+i, "").toUpperCase();
                if (!"".equals(nombre)) {
					this.m_consejeros.add(nombre);
				}
            }
            
            Factions.loadFactionArmors(ini);
			
			log.warn("Admins recargados");			
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }



}
