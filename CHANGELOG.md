# Changelog (in Spanish)
Todos los cambios notables(?) del proyecto serán documentados en este archivo.

## [SIN PUBLICAR]

### 2019-04-01
- agregados /MOD y /SLOT

### 2019-03-31
- agregado el comando /MODMAPINFO en todas sus variantes

### 2019-03-30
- corregido error al conectar (usuario ya conectado)
- se corrige la generación del jar y sus dependencias

### 2019-03-29
- agregado /CENTINELAACTIVADO

### 2019-03-28
- implementado el Centinela
- refactory en GameServer

### 2019-03-27
- agregado /ACEPTCONSE /ACEPTCONSECAOS /ACEPTCONSECAOS

### 2019-03-26
- agregado /CRIMSG /ACC /RACC /BMSG /SEGUIR /RESETINV /RECOMPENSA /ENLISTAR /RETIRAR (faction) /AI1-/AI5 /AC1-/AC5

### 2019-03-25
- agregados /RELOADNPCS /RELOADSINI /RELOADHECHIZOS /RELOADOBJ /ROL /AEMAIL /ANAME /APASS /BLOQ /TRIGGER

### 2019-03-24
- agregado /MOTD /IPToNick /GRABAR /GUARDAMAPA /HABILITAR
- agregados /PING /FORCEMIDI /FORCEWAV /FORCEMIDIMAP /FORCEWAVMAP /TELEPLOC /NICK2IP /_BUG
- agregado Mover Inventario del Banco. 

### 2019-03-23
- agregados /CARCEL, /PERDON, /CONDEN, /BAN, /UNBAN, /BANIP, /UNBANIP, /BANIPLIST, /BANIPRELOAD

### 2019-03-22
- nuevos comandos de usuario: /DENUNCIAR
- nuevos comandos de GM:  /LASTEMAIL /ESTUPIDO /NOESTUPIDO /SILENCIAR /PENAS /ADVERTENCIA /BORRARPENA

### 2019-03-21
- nuevos comandos de GM: /NICK2IP /LASTIP

### 2019-03-20
- se agregan comandos de GM: /BAL, /INV, /BOV, /STAT, /SKILLS

### 2019-03-19
- se agregan comandos de GM: /CC, /MATA y /MASSKILL, /CT, /DT, /PISO, /MASSDEST, /INFO

### 2019-03-18
- SEGURIDAD: no se guarda más el password del personaje, sino un hash salteado con papas.
- bugfix con colores rgb
- se agregan más comandos de GM (panel ME completo): showName, changeColor, SetCharDescription
- se agregan otros comandos de GM: limpiar mundo, /REM 

### 2019-03-17
- nuevos comandos de GM: /showname, /ignored /navigate
- refactory de AiType y agrego AI de NpcObjeto

### 2019-03-16
- corregido worldSave, guardados de mapas.
- se agregan comandos faltantes de GM.

### 2019-03-14
- corregido el enviar objetos al entrar en área, que las puertas no enviaban el blocked/unblocked izquierdo.
- corregido en el doble-clic el seleccionar NCP para Comerciar o alguna otra acción por defecto (cuando tenía objetos cerca).
- al moverse un jugador sobre un casper, ahora intercambian posiciones. En la jerga AO: "patea al casper" :D (un "casper" un fantasma, un usuario mortito)
- corregido, que permitía usar la barca estando en tierra sin agua.
- corregido la persistencia de las mascotas domadas al reconectarse. Y que las mascotas no entren en zonas seguras.

### 2019-03-13
- corregido bug de clones con el warpMe \o/

### 2019-03-12
- corregido bugs de areas!! \o/

### 2019-03-11
- corregido bug de usuarios que quedaban online al desconectarse el cliente (por cierre de app)
- agregados más comandos de GM
- corregido el apagado del servidor, para que sea más limpio, cerrando conexiones.

### 2019-03-10
- arreglado el modo seguro
- corregido en decoder del ClientPacketID que al ser signed byte podía ser negativo
- corregido el checkSummonTimeout por modificación concurrente de listas
- modificado lookAtTile y tags de usuarios,gm,etc
- se implementan varios comandos de GM

### 2019-03-09
- se reimplementan los hechizos que lanzan los NPCs
- se corrigen bugs con la experiencia ganada y los skills
- se corrige el mover hechizos en el inventario

### 2019-03-08
- se reimplementa y revisan todo el sistema de hechizos... ni el Mago Blanco se animó a tanto (?)

### 2019-03-07
- se agregan más operaciones en la API
- se refactorizan los hechizos, se agregan flags faltantes, bug fixes, etc

### 2019-03-06
- corregido comercio con npc, pero debería reimplementarse completo
- se crea una API REST para la administración del servidor, usando Spark.

### 2019-03-05
- corregido el forum, con persistencia en json. Completo.
- corregido que envíe el level-up al enviar skills, si corresponde. No se enviaba.
- corregido crear fogatas y descansar.

### 2019-02-28
- se corrigieron muchos comandos y más refactory.
- se mide y se mejoró la performace de GameServer.

### 2019-02-27
- se manejan varios paquetes básicos del protocolo.

### 2019-02-26
- nuevo networking, usando Netty. Se hizo un generador de código usando ANTLR4 para todos los paquetes del protocolo.

### 2019-02-22
- nuevo protocolo, compatible con AO 0.12.3 (se usa el cliente gs-zone-v0.12.3 como referencia)

### 2019-02-20
- escribo en archivo una definición del protocolo binario, para usarlo en un generador de código

### 2019-02-19
- Refactory de clanes
- Nuevo protocolo binario. Está todo roto ahora \o/

### 2019-02-18
- Se mueve el código de carga y guardado de chars a UserStorage
- Se mueve la carga del ObjectInfo a ObjectInfoStorage
- Se cambian las armaduras faccionarias
- Se mueven comanddos de Admin a nueva clase

### 2019-02-14
- Cambiado: nuevo logging usando log4j2
- Refactory de MOTD, Forum, UPnP
- Agregado: comando de status básico en la Consola.

### 2019-02-13
- Modificado: se mejoró la consola de gestión del servidor
- Agregado: nuevo comando en la consola para mostrar threads
- Corregido: en maven la generación del jar, carpeta destino y main-class.
- Agregado: pruebas unitarias con JUnit5, comenzando con clases básicas WorldPos y MapPos

### 2019-02-12
- Agregado: soporte para UPnP. Se usa la implementación de [Federico Dossena](https://github.com/adolfintel/WaifUPnP).
- Corregido: los threads no terminaban al finalizar el servidor.
- Agregado: comando "0" para cerrar el servidor desde la consola.
