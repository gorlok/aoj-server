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
package org.ArgentumOnline.server.areas;

import org.ArgentumOnline.server.Constants;

public class Areas implements Constants {
	
	long countentrys;
	long optvalue;
	long userentrys[];
	int curday;
	int curhour;
	
	int areasInfo[][] = new int[100][100];
	int postoarea[] = new int[100];
	
	int areasrecive[] = new int[12];
	
	public final static int USER_NUEVO = 255;
	
	
public void InitAreas() {
	int i = 0;
	int x = 1;
	
    while (i < 12) {
        areasrecive[i] = (2 ^ i);
        i++;
        if (i==12) { break;}
    }                
    
    i = 1;
    
    while (i < 101) {
        postoarea[i] = (i / 9);
        i++;
        if (i==100) { break;}
    }    
    
    i = 1;
    
    while (i < 101) {
    	while (x < 101) {
        areasInfo[x][i] = (i / 9 + 1);
        x++;
    	}
        i++;
    }   
    
    i = 0;
    x = 0;
}

	
}
