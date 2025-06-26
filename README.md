# ArenaWars
Dieses Projekt ist im Wintersemester 2015 an der Bergischen Universität Wuppertal entstanden und anschließend von mir weiterentwickelt. Es handelt sich hierbei um ein Echtzeit-Browsergame, in dem man sich in einer Arena gegenseitig bekämpfen kann. Es wäre schön, wenn hier mehrere kluge Köpfe das Projekt fortführen, deswegen lade ich es hier in Github hoch.

Aktuelle Version: 1.04

## Server
* Java EE - Serversoftware (Benötigt einen Application-Server wie bspw. glassfish)
* MySQL-Datenbank

Mittlerweile würde ich das in NodeJS schreiben. Das Passwort-Hashing geht auch besser.

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

## Docker-Setup

Mit Docker und docker-compose lässt sich das Projekt unkompliziert starten. Die bereitgestellte `docker-compose.yml` richtet eine MariaDB-Datenbank (Version 10.1), den Java-Server sowie einen kleinen nginx-Container für die statischen Client-Dateien ein.

1. Docker und docker-compose installieren.
2. Im Projektverzeichnis den folgenden Befehl ausführen:
   ```bash
   docker-compose up --build
   ```
3. Der Client ist anschließend unter http://localhost:8080 erreichbar; der Websocket-Server läuft auf Port 8081.

Beim ersten Start wird das SQL-Skript `arenawars.sql` automatisch in die Datenbank importiert.
Der Server greift per Standard-Konfigurationsdatei `server/config.ini` auf die
Datenbank zu. Darin ist `mysql_host=db` konfiguriert, da der
datenbank-Container diesen Servicenamen trägt. Das Standardpasswort lautet
`example` (siehe `docker-compose.yml`).
