package org.ArgentumOnline.server.api;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.gm.ManagerServer;

import com.google.gson.Gson;

import spark.ResponseTransformer;

public class ManagerApi {

	/*
	 * keytool -genkeypair -keystore localhost.keystore -keyalg RSA -alias localhost
	 * -dname "CN=localhost O=localhost" -storepass password -keypass password
	 * 
	 * curl -k https://localhost:9999/online
	 * 
	 * curl -k -X post https://localhost:9999/shutdown
	 */
	private GameServer game;
	
	public ManagerApi(GameServer game) {
		this.game = game;
		Gson gson = new Gson();
		
		port(9999);
		secure("misc/localhost.keystore", "password", null, null);

		get("/ping", (request, response) -> "\"pong\"");

		get("/online", (request, response) -> game.getUsuariosConectados(), gson::toJson );

		get("/raining", (request, response) -> game.isRaining());
		post("/raining", (request, response) -> {
			admin().toggleRain(); // FIXME 
			return game.isRaining();	
		});
		
		get("/status", (request, response) -> game.serverStatus(), gson::toJson );

		post("/reload", (request, response) -> {
			admin().loadAdmins();
			return "bye!";
		});
		
		post("/shutdown", (request, response) -> {
			GameServer.instance().shutdown();
			return "bye!";
		});
	}
	
	private ManagerServer admin() {
		return this.game.manager();
	}
	
	public class JsonTransformer 
	implements ResponseTransformer {
		private Gson gson = new Gson();
		@Override
		public String render(Object model) {
			return gson.toJson(model);
		}

	}
}
