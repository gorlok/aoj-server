package org.ArgentumOnline.server.api;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;
import static spark.Spark.secure;

import org.ArgentumOnline.server.GameServer;

public class ManagerServer {
	
	/*
	 * keytool -genkeypair -keystore localhost.keystore -keyalg RSA -alias localhost -dname "CN=localhost O=localhost" -storepass password -keypass password
	 * 
	 * curl -k https://localhost:9999/online
	 * 
	 * curl -k -X post https://localhost:9999/shutdown
	 */
	public ManagerServer() {
        port(9999);
        secure("misc/localhost.keystore", "password", null, null);

        get("/ping", (request, response) -> "pong");
        
        get("/online", (request, response) -> {
        	return "Connected users: " + 
        			GameServer.instance().getUsuariosConectados();
        });
        
        post("/shutdown", (request, response) -> {
        	GameServer.instance().shutdown(); 
        	return "bye!"; 
        });
	}
	
}
