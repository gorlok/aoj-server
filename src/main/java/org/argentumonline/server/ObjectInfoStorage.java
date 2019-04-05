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
package org.argentumonline.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.util.IniFile;

public class ObjectInfoStorage {
	private static Logger log = LogManager.getLogger();
	
    private List<ObjectInfo> objects  = new ArrayList<ObjectInfo>(0);
    
    public ObjectInfo getInfoObjeto(int objid) {
        return this.objects.get(objid - 1);
    }

    /** Load objects */
    public void loadObjectsFromStorage() {
    	log.trace("loading objects");
        IniFile ini = new IniFile();
        try {
            ini.load(Constants.DAT_DIR + java.io.File.separator + "Obj.dat");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        int cant = ini.getInt("INIT", "NumOBJs");
        
        this.objects = new ArrayList<ObjectInfo>(cant);
        for (short i = 1; i <= cant; i++) {
            ObjectInfo obj = new ObjectInfo();
            obj.load(ini, i);
            this.objects.add(obj);
        }
    }

}
