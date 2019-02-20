# Changelog (in Spanish)
Todos los cambios notables del proyecto serán documentados en este archivo.

## [SIN PUBLICAR]

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
