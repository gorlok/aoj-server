##TO-DO LIST

* new networking (wip)
* implement full AO's network protocol (wip)


* object pool (for many things, new protocol classes for instance)
* remove singletons, use IoC/DI
* more unit testing

* ACCOUNTS / DB / security
* server status/info, diagnostics, metrics
* server configuration (jcommander - parameters) and config file.
* rest api for managing, web app ?
	* use spark web server (https rest-api?)
	* web app
* login por https y auth token

* role masters
* chatColor in user flags
* En Comercio, REVISAR si se hace el chequeo de 
	' Cantidad maxima de objetos por slot de inventario
	Public Const MAX_INVENTORY_OBJS As Integer = 10000
* implementar Party system
* los npcs hacen respawn cerca del usuario
* refactorizar sistemas de comercio, trabajo, hechizos, combate
* MoveNPCChar revisar Caspers y WriteForceCharMove
* NPC Alineacion debería ser un enum? Qué significan las constantes? !=0 parece ser Npc "malvado". En DAT hay 0 y 2, aunque en el código hay 1 y 4 también :-/
* revisar toda la AI de NPCs
* implementar pretorianos

* add docker support
	docker run -it --rm aoserver
	docker run -it --rm -p 7666:7666 -p 9999:9999 aoserver

