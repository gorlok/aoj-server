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
package org.ArgentumOnline.server;

import java.util.Arrays;
import java.util.Scanner;

public class ManageServer extends Thread {
	GameServer server;
	
	enum Command {
		help("help"),
		shutdown("shutdown"),
		reload("reload"),
		status("status"),
		threads("threads");
		
		private String name;
		private Command(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	};

	public ManageServer(GameServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		String line;

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("\n======================================================");
			printHelp();			
			while (this.server.running) {
				try {
					line = scanner.nextLine();
				} catch (Exception e) {
					unkownCommand();
					continue;
				}
				
				if (line.isEmpty())
					continue;
				
				String[] command = line.split("\\p{Blank}");
				if (command.length < 1)
					continue;
				
				Command cmd;
				try {
					cmd = Command.valueOf(command[0].toLowerCase());
				} catch (Exception e) {
					unkownCommand();
					continue;
				}
				
				processCommand(cmd);
			}
		}
	}

	private void processCommand(Command cmd) {
		switch (cmd) {
		case help:
			printHelp();
			break;
			
		case shutdown:
			this.server.shutdown();
			break;
	
		case reload:
			this.server.getAdmins().loadAdmins();
			break;
			
		case threads:
			showThreads();
			break;
			
		case status:
			this.server.showStatus();
			break;
			
		default:
			unkownCommand();
		}
	}

	private void unkownCommand() {
		System.out.println("Comando desconocido");
		printHelp();
	}

	private void printHelp() {
		System.out.println("Available commands: " + Arrays.toString(Command.values()));
		System.out.println();
	}

	private void showThreads() {
		Thread.getAllStackTraces().keySet().forEach((t) -> {
		    String name = t.getName();
		    Thread.State state = t.getState();
		    int priority = t.getPriority();
		    String type = t.isDaemon() ? "Daemon" : "Normal";
		    
		    System.out.printf("%-20s \t %s \t %d \t %s\n", name, state, priority, type);
		});
		System.out.println();
	}

}
