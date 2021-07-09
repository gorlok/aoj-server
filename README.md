# Gorlok AO
An implementation of Argentum Online using Java.

Argentum Online (a.k.a. AO) is an 2D MMORPG from Argentina.

Read more about Argentum Online on [official wiki community](http://wiki.comunidadargentum.com/) (in Spanish).

# Argentum Online Server
 
__Here be dragons__

WARNING: it's a work in progress, under a big rewrite and totally unstable. 

Currently it's using AO's binary protocol v0.12.3

## Requirements to build

* [JDK 11](https://adoptopenjdk.net/)
* [Gradle](https://gradle.org/) Or [Maven 3.5+](https://maven.apache.org/download.cgi)

__Please note that sources are keep in ISO-8859-1.__

## Build and running (with Gradle)

To build the project, run on the root folder: `gradle`

You will found distribution zip on build/distributions/ao-server-0.12.3.zip

Unzip it on some empty folder and start the game server with: `./bin/ao-server reset` (Linux) or `.\bin\ao-server.bat reset` (Windows)  

## Build and running (with Maven)

To build the project, run on the root folder: `mvn verify`

Start the game server with: `java -jar target/ao-server-0.12.3.jar`

# Argentum Online Client

It's planned to be build a proper client using libGDX (OpenGL).

By the time being, you can use any AO Client compatible. Currently tested with [this one](https://github.com/gorlok/aoj-client), based on GS-Zone AO v.12.3-Final.

## History

This project started on 2003 and it was previously known as AOJava or aoj-server and it lasted a few years. It was hosted on <https://sourceforge.net/projects/aojava/>.

Almost a decade later, this is the official resumption of the project by the original author.

From [Wikipedia](https://es.wikipedia.org/wiki/Argentum_Online) (in Spanish):
> "Argentum Online, también conocido como AO, es un videojuego de rol multijugador masivo en línea libre, disponible para los sistemas operativos Microsoft Windows y publicado en el año 1999 en Internet de manera independiente. El juego está programado en Visual Basic, y su éxito se debió en parte a que necesita muy bajos recursos de red para funcionar por internet y el ser completamente gratuito, algo determinante a principios de los 2000s en Argentina donde nació." 

## License
This project is Free Open Source Software, licensed under [GNU AFFERO GENERAL PUBLIC LICENSE Version 3](https://www.gnu.org/licenses/agpl.html)
