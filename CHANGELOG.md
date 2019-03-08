# Changelog (in Spanish)
Todos los cambios notables del proyecto serán documentados en este archivo.

## [SIN PUBLICAR]

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
