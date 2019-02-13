package org.ArgentumOnline.server;

import java.util.Scanner;

public class ManageServer extends Thread {
	AojServer server;
	Scanner getCommand;

	private final static int SERVER_SHUTDOWN = 0;
	private final static int RELOAD_ADMIN = 1;
	private final static int CUAL_AREA = 2;

	public ManageServer(AojServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		int command = 0;

		getCommand = new Scanner(System.in);
		while (this.server.m_corriendo) {
			try {
				command = getCommand.nextInt();
			} catch (Exception e) {
				System.out.println("Comando inválido");
				continue;
			}

			switch (command) {
				case SERVER_SHUTDOWN:
					this.server.shutdown();
					break;
			
				case RELOAD_ADMIN:
					reloadAdmin();
					break;
					
				case CUAL_AREA:
					cualArea(getCommand);
					break;
	
				default:
					System.out.println("Comando NO reconocido");
			}
		}
	}

	private void cualArea(Scanner getCommand) {
		System.out.println("Introduca x");
		int mx = getCommand.nextInt();
		System.out.println("Introduzca y");
		int my = getCommand.nextInt();
		//server.testArea(mx, my);
	}

	public void reloadAdmin() {
		server.loadAdmins();
		System.out.println("Admins recargados");
	}

}
