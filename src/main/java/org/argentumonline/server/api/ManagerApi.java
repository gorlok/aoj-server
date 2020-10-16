package org.argentumonline.server.api;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;
import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.path;

import org.argentumonline.server.GameServer;
import org.argentumonline.server.Security;
import org.argentumonline.server.gm.ManagerServer;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserStorage;

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
		secure("dat/localhost.keystore", "password", null, null);

		get("/ping", (request, response) -> "\"pong\"");

		get("/online", (request, response) -> game.getUsuariosConectados(), gson::toJson );

		get("/status", (request, response) -> game.serverStatus(), gson::toJson );
		
		post("/login", (request, response) -> {
			String userName = request.queryParams("username");
			String password = request.queryParams("password");
			
			if (User.userExists(userName)) {
				try {
					String passwordHash = UserStorage.passwordHashFromStorage(userName);
					if (Security.validatePassword(userName, password, passwordHash) && game.manager().isGod(userName)) {
						request.session(true);
						request.session().attribute("userName", userName);
						request.session().attribute("authenticated", true);
						return true;
					}
				} catch (Exception ignored) {
				}
			}
			halt(401, "Login failed");
			return false;
		});
		
		path("/api", () -> {
			before("/*", (request, response) -> {
				Boolean authenticated = request.session().attribute("authenticated");
			    if (authenticated == null || !authenticated) {
			    	halt(401, "No authenticated");
			    }
			});
			
			get("/raining", (request, response) -> game.isRaining());
			
			post("/raining", (request, response) -> {
				admin().toggleRain(); 
				return game.isRaining();	
			});
			
			post("/reload", (request, response) -> {
				admin().loadAdmins();
				return "bye!";
			});
			
			post("/shutdown", (request, response) -> {
				GameServer.instance().backupWorld();
				GameServer.instance().shutdown();
				return "bye!";
			});
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
	
	static boolean notNull(Boolean value) {
		return value == null ? false : value;
	}
}
