package org.ArgentumOnline.server;

import java.util.List;
import java.util.Vector;

import org.ArgentumOnline.server.util.IniFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectInfoStorage {
	private static Logger log = LogManager.getLogger();
	
    private List<ObjectInfo> m_objetos  = new Vector<ObjectInfo>();
    
    public ObjectInfo getInfoObjeto(int objid) {
        return this.m_objetos.get(objid - 1);
    }

    /** Load objects / Cargar los m_objetos */
    public void loadObjectsFromStorage() {
    	log.trace("loading objects");
        this.m_objetos = new Vector<ObjectInfo>();
        IniFile ini = new IniFile();
        try {
            ini.load(Constants.DATDIR + java.io.File.separator + "Obj.dat");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        int cant = ini.getInt("INIT", "NumOBJs");
        
        for (short i = 1; i <= cant; i++) {
            ObjectInfo obj = new ObjectInfo();
            obj.load(ini, i);
            this.m_objetos.add(obj);
        }
    }

}
