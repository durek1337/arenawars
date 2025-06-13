# ArenaWars
Dieses Projekt ist im Wintersemester 2015 an der Bergischen Universität Wuppertal entstanden und anschließend von mir weiterentwickelt. Es handelt sich hierbei um ein Echtzeit-Browsergame, in dem man sich in einer Arena gegenseitig bekämpfen kann. Es wäre schön, wenn hier mehrere kluge Köpfe das Projekt fortführen, deswegen lade ich es hier in Github hoch.

Aktuelle Version: 1.04

## Server
* Java EE - Serversoftware (Benötigt einen Application-Server wie bspw. glassfish)
* MySQL-Datenbank
* Docker-Image basiert nun auf OpenJDK&nbsp;17

Mittlerweile würde ich das in NodeJS schreiben. Das Passwort-Hashing geht auch besser.

## Docker-Setup

Um Server und Datenbank schnell zu starten, werden Docker und Docker Compose benötigt. Nach der Installation können beide Dienste mit

```
docker-compose up
```

gestartet werden. Die MySQL-Datenbank wird dabei automatisch mit `arenawars.sql` initialisiert und der Spielserver läuft anschließend unter Port 8081.

Die Zugangsdaten zur Datenbank können über Umgebungsvariablen angepasst werden. `docker-compose.yml` definiert dazu `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PASSWORD` und `MYSQL_NAME`. Diese Werte werden vom Server beim Start eingelesen und überschreiben die Angaben aus `server/config.ini`.

## Client

* WebApp mit JQuery und JQueryUI

Mittlerweile würde ich webpack nutzen



## Beteiligte

**Kommilitonen**

Marvin H. (Planung, Grafiken)

Solomon K. (Planung, Grafiken)

Florian O. (Planung)

Willi S. (Planung)

Kadir T. (Planung)

Erhan Y. (Planung)

Dominik Höltgen (Planung, Grafiken, Code)

**Extern**

Marjan Markelj (Musik + Sound)
