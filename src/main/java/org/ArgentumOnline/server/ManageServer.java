package org.ArgentumOnline.server;

import java.util.Arrays;
import java.util.Scanner;

public class ManageServer extends Thread {
	AojServer server;
	
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

	public ManageServer(AojServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		String line;

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("\n======================================================");
			printHelp();			
			while (this.server.m_corriendo) {
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
