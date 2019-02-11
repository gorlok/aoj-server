package org.ArgentumOnline.server;

import java.util.Scanner;

public class manageServer extends Thread{
	AojServer server;
	
	private final byte reload_admin = 1;
	private final byte cual_area = 2;
	
	public manageServer(AojServer server) {
		this.server = server;
	}
	
	@Override
	public void run() {
	int command = 0;
	
	while(true) {	
		 Scanner getCommand = new Scanner (System.in);
		 
		 try {
	         command = getCommand.nextInt();
		 } catch (Exception e) {
			 System.out.println("Comando inválido");
			 break;
		 }
	     
	     switch(command) {
	     
	     case reload_admin:
	    	 reloadAdmin();
	    	 System.out.println("Comando ejecutado");
	    	 break;
	    	 
	     case cual_area:
	    	 System.out.println("Introduca x");
	    	 int mx = getCommand.nextInt();
	    	 System.out.println("Introduzca y");
	    	 int my = getCommand.nextInt();
	    	 
	        // server.testArea(mx, my);
	     break;
	    	 
	     default:
	    	 System.out.println("Comando NO reconocido");
	     
	     }
	     
	}       
	}
	
	public void reloadAdmin() {
		server.loadAdmins();
	}

}
