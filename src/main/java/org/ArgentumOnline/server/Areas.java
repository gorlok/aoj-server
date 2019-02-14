package org.ArgentumOnline.server;

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
