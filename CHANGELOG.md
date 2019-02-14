# Changelog
Todos los cambios notables del proyecto serán documentados en este archivo.

## [SIN PUBLICAR]

### 2019-02-14
- Cambiado: nuevo logging usando log4j2

### 2019-02-13
- Modificado: se mejoró la consola de gestión del servidor
- Agregado: nuevo comando en la consola para mostrar threads
- Corregido: en maven la generación del jar, carpeta destino y main-class.
- Agregado: pruebas unitarias con JUnit5, comenzando con clases básicas WorldPos y MapPos

### 2019-02-12
- Agregado: soporte para UPnP. Se usa la implementación de [Federico Dossena](https://github.com/adolfintel/WaifUPnP).
- Corregido: los threads no terminaban al finalizar el servidor.
- Agregado: comando "0" para cerrar el servidor desde la consola.
