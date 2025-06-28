server = {
  path : "ws://"+location.hostname+":8081/arenawars/control",
  //path : "ws://192.168.178.29:8081/arenawars/control",
  connected : false,
  latencies: [],
  lastping : null,
  pinginterval: 300,
  pingtimer : null,
  beenconnected : false, // Wurde die Verbindung getrennt oder konnte sie gar nicht erst hergestellt werden?
  socket : null,
  timer : null,
  getPingLatency : function(){
    var sum = 0;
    for(i=0;i<server.latencies.length;i++) sum += server.latencies[i];
    return Math.ceil(sum/server.latencies.length);
  },
  ping: function(){
    server.lastping = (new Date()).getTime();
    server.send({type:0});
  },
  lock: function(){

  }, // Wenn die Verbindung getrennt wird, deaktiviere Keyboardfunktion usw.
  unlock: function(){

  },
  send : function(obj){
    console.log(obj);
        server.socket.send($.stringify(obj));
  },
  receive : function(msg){
                 var data = $.parseJSON(msg);

                /**
                Zum Server
                1 = Keyboard
                2 = Maus
                3 = Authentifizierung
                4 = Room&LobbyData

                data['type'] gibt den "Datentyp" an.
                1 = Nachrichten (Fehler, Chat, ...)

                3 = Authentifizierung
                4 = Room&LobbyData [stype -> 0: Liste Lobby, 1: Füge Raum der Liste hinzu, 2: entferne Raum aus Liste, 3: Trete Raum bei, 4: verlasse Raum, 5: Spieleranzahl gestiegen, 6: gesunken]
                **/

                switch(data['type']){
                case 0:
                var hold = 5;
                if(server.latencies.length > hold)
                server.latencies = server.latencies.slice(-hold);
                server.latencies.push((new Date()).getTime()-server.lastping);
                var pl = server.getPingLatency();

                $("#pinglatency").css('background-color','#'+((pl > 100) ? ((pl > 500) ? 'ff0000' : 'ffff00') : '00ff00')).attr('title',"Ping-Latenz: "+pl+"ms").text(pl);

                if(typeof data['l'] != "undefined"){
                  var l = data.l;
                    $("#serverlatency").css('background-color','#'+((l > 100) ? ((l > 500) ? 'ff0000' : 'ffff00') : '00ff00')).attr('title',"Server-Latenz: "+l+"ms").text(l);

                } else $("#serverlatency").css("background-color","#fff").attr("title","Kein aktives Spiel").text("");

                break;
                case 1: // Nachrichten
                      switch(data['stype']){
                        case 0: // Fehlermeldung
                               user.showError(data['msg']);
                        break;
                        case 1: // Meldung
                               user.showMessage(data['msg']);
                        break;
                        case 2: // Chatnachricht
                            if(data['n'].length == 0)
                               $(".chat .chatContent").append("<i>"+data['msg']+"</i><br>");
                            else {
                              var m = $("<span />",{text : data['msg']});
                              m.prepend("<b style='color:#"+playercolors[data['pos']]+";'>"+data['n']+":</b> ")
                              $(".publicchat .chatContent").append(m).append("<br>");
                              }
                               $(".publicchat .chatContent").animate({scrollTop : "+="+100});
                               if(user.ingame && data.pos > 0)
                               user.cache.player[data.pos].showText(data['msg']);
                        break;

                      }

                break;

                case 3: // Authentifizierung
                    switch(data.stype){
                        case 1:
                        var $regForm = $("#registrationDialog form");

                          if(data.exists){
                              $("div[data-subject=name]",$regForm).animate({"backgroundPositionX" : -60},300);
                              $("input[name=name]",$regForm).data("exists",true);
                          } else {
                              $("div[data-subject=name]",$regForm).animate({"backgroundPositionX" : 0},300);
                              $("input[name=name]",$regForm).data("exists",false);
                          }
                          $("input[type=submit]",$regForm).button("option","disabled",!$regForm.data("validation")());
                        break;
                        case 2:
                            var $regForm = $("#registrationDialog form");
                            $regForm.hide();
                          if(data.succeed){
                            console.log("Account erfolgreich erstellt!");
                             $("div[data-subject=succeed]",$regForm.parent()).show();
                          } else{
                             $("div[data-subject=nosucceed]",$regForm.parent()).show().delay(5500).hide("slide");
                             $regForm.delay(6000).show("slide");
                          }
                        break;
                        case 10: // Accountloginantwort
                        if(data['id'] == 0) {
                        console.log("Account nicht gefunden");
                        $("#loginstatus").html("Accountdaten nicht korrekt<hr>");

                        } else {
                          $("#loginscreen").hide();
                          console.log("Roomid: "+user.roomid);
                          if(user.roomid > 0){
                             user.joinRoom(user.roomid);
                             console.log("Reconnect zum Spiel");
                          }
                          user.active = true;

                        console.log("Account #"+data['id']+": "+data['name']+" gefunden");
                        $("#loginDialog").dialog("close");
                        $("#loginstatus").html('');
                        }

                        break;
                        case 11: // Accountlogout
                        user.closeAll();
                        $("#loginDialog").dialog("open");
                        if(user.active){
                        user.active = false;
                        user.showError("Der Account wurde nicht &uuml;ber das Men&uuml; ausgeloggt.<br>Ursachen daf&uuml;r kann ein &quot;Kick&quot;, interner Serverfehler oder aber ein Einloggen in den gleichen Account &uuml;ber einen anderen Client sein.");
                        }
                        break;
                    }

                break;

                case 4: // LobbyData
                      switch(data['stype']){
                        case 0: // Liste Lobby
                        user.cache.rooms = [];
                        $("#roomchat").hide();
                        $("#lobbytable tbody").html('');
                        $("#lobby").show();
                        $("#lobby").tabs("option", "active", 0);
                            for(var i=0;i<data['rooms'].length;i++){
                               console.log(data['rooms'][i]);
                               new Room(data['rooms'][i]).showPlayer();
                            }
                        $backgroundMusic.volume = 0.8*user.soundMusic;

                        $("#lobbyplayer").text(data['online']+1);

                        break;
                        case 1: // Neuer Raum in der Lobby
                               new Room(data['room']);
                        break;
                        case 3: // Trete Raum bei
                        new Room(data['room']);
                        $backgroundMusic.volume = 0.5*user.soundMusic;
                        console.log("Raum "+data['room'].title+" beigetreten");

                        break;
                        case 4: // Verlasse Raum
                        user.cache.rooms[user.roomid].leaveRoom();
                        console.log("Raum verlassen");

                        break;
                        case 5: // Spieler in Raum beigetreten
                        if(typeof data['roomid'] == "undefined"){ // Aktuelles Spiel, Raum-ID nicht notwendig
                            user.cache.rooms[user.roomid].addPlayer(data.spectator);
                        } else
                            user.cache.rooms[data['roomid']].addPlayer(null);
                        break;
                        case 6:  // Spieler Raum verlassen
                        if(typeof data['roomid'] == "undefined"){
                           user.cache.rooms[user.roomid].removePlayer(data['pos']);
                        } else
                            user.cache.rooms[data['roomid']].removePlayer(null);
                        break;
                        case 7: // Raum wurde aufgelöst
                            $("#lobbyrow-"+data.room).remove();
                        break;
                        case 8: // Ein neuer Spieler betritt oder verlässt die Lobby
                            var player = parseInt($("#lobbyplayer").html());
                            if(data.entered) player++; else player--;
                            $("#lobbyplayer").text(player);
                        break;
                      }
                break;
                case 5: // GameData
                    switch(data['stype']){
                      case -1: // Binde an Charakter
                         user.pos = data.pos;
                      break;
                      case 0: // Initialisiere Spiel - Neue Runde
                      console.log("Initialisiere Map");
                        user.getRoom().bindMap(data.map);
                        user.onEnter();

                        if(user.pos > 0) user.onPlayer();

                        for(var i=0;i<data.chars.length;i++)
                            new Player(data.chars[i]);

                         if(user.pos == 0 || !user.alive) user.onSpectator();

                        for(var i=0;i<data.weapons.length;i++)
                            new Weapon(data.weapons[i]);

                        for(var i=0;i<data.gameobjects.length;i++)
                            new Gameobject(data.gameobjects[i].go,data.gameobjects[i].ground);

                        for(var i=0;i<data.powerups.length;i++)
                            new Powerup(data.powerups[i]);

                      break;
                    case 1: // Verlasse Spiel
                        user.onLeave();
                    break;
                    case 2: // Projectile Spawned
                         new Projectile(data.id, data.wid, data.pos, data.dir, data.x, data.y, data.speed);
                    break;
                    case 3: // Projektil verschwindet
                        var pr = user.cache.player[data.pos].shots[data.id];
                        if(typeof pr != "undefined") pr.disappear();
                    break;
                    case 5: // Waffe erscheint
                            new Weapon(data.weapon);
                    break;
                    case 6: // Waffe verschwindet
                           $("#weapon-"+data.id).remove();
                    break;

                    case 7: // Runde vorbei
                        var stat = data.stats;
                        var $table = $("<table />",{"class" : "statistic"});
                        var $row = $("<tr />",{id : "statrow-0"}).append("<td colspan=2>Spieler</td><td>Kills</td><td>Deaths</td><td>Erlittener Schaden</td><td>Ausgeteilter Schaden</td><td>Sch&uuml;sse</td><td>Trefferrate</td>");
                        $table.append($row);

                        for(var i=0; i<data.stats.length;i++){
                          var p = data.stats[i];
                            var $row = $("<tr />",{id : "statrow-"+(i+1)}).css("background-color","#"+playercolors[p.pos]);
                            $row.append($("<td />").text("#"+(i+1)));
                            $row.append($("<td />").text(user.cache.player[p.pos].name));
                            $row.append($("<td />").text(p.kills));
                            $row.append($("<td />").text(p.deaths));
                            $row.append($("<td />").text(p.dmgin));
                            $row.append($("<td />").text(p.dmgout));
                            $row.append($("<td />").text(p.shoots));
                            $row.append($("<td />").text((p.accuracy == -1) ? "-" : p.accuracy+"%"));
                            $table.append($row);
                        }
                        var bottomText;
                        if(data.newgame) bottomText = "Das war Runde "+data.round+", die n&auml;chste Runde startet automatisch in <span id='roundCountdown'>15</span> Sekunden";
                        else {
                          setTimeout(function(){
                            $("tr",$table).not("#statrow-0, #statrow-1, #statrow-bot").fadeOut();
                            $("#statrow-bot").before($("<tr />").append($("<td />",{"text" : "... ist der Sieger dieses Spiels!","colspan" : 7}).append("<br><br>")));
                          },5000);
                          bottomText = "Das Spiel ist vorbei.<br>Verlasse Spiel in <span id='roundCountdown'>15</span>";
                        }
                        $table.append($("<tr />",{"id" : "statrow-bot"}).append($("<td />",{"colspan" : 7}).html(bottomText)));
                        $("#overlayComplete").html($table);



                        user.getRoom().roundtimer = new Timer($("#roundCountdown"),15,1,0,1000,function(){})
                        $("#overlayComplete").show();



                    break;
                    case 8: // Spieler getötet
                        var killer = user.cache.player[data.killer];
                        var victim = user.cache.player[data.victim];


                        var $k = $("<span />",{"text" : killer.name, "class" : "playername"}).css("color","#"+playercolors[data.killer]);
                        var $v = $("<span />",{"text" : victim.name, "class" : "playername"}).css("color","#"+playercolors[data.victim]);

                        var $text = $("<span />",{"class" : "displayMsg"}).append($v).append(" wurde getötet von ").append($k).append("<br>");
                        $text.appendTo($("#overlayMid")).delay(3000).fadeOut(function(){ $(this).remove(); });

                        victim.death();

                        if(data.victim == user.pos){
                          killer.follow();
                          if(data.lifes > 0){
                           var $text = $("<div />").html('Du hast noch '+data.lifes+' Leben &uuml;brig. Respawn in <span id="respawntimer">5</span>').appendTo($("#spectatorinterface"));
                           user.getRoom().roundtimer = new Timer($("#respawntimer"),5,1,0,1000,function(){$text.fadeOut(function(){$(this).remove()});});
                          }
                          }

                    break;
                    case 9: // Neues Gameobject
                        new Gameobject(data.go, data.ground);
                    break;
                    case 10: // Waffe nachladen
                          user.cache.player[data.pos].reloadWeapon(data.t);
                    break;
                    case 11: // Schlagen
                          user.cache.player[data.pos].showPunch(data.t);
                    break;
                    case 12: // Verändere Globale Bewegungsgeschwindigkeit
                          user.getRoom().movementspeed = data.s;
                    break;
                    case 13: // Verändere Globale Projektilgeschwindigkeit
                          user.getRoom().projectilespeed = data.s;
                    break;
                    case 14: // Spawne Powerup
                        new Powerup(data.powerup);
                    break;
                    case 15: // Powerup aktiviert
                        var activator = user.cache.player[data.activator];

                        var $a = $("<span />",{"text" : activator.name, "class" : "playername"}).css("color","#"+playercolors[data.activator]);

                        var $text = $("<span />",{"class" : "displayMsg"}).append($a).append(" hat das PowerUp <b>").append(data.name).append("</b> aktiviert.<br>");
                        $text.appendTo($("#overlayMid")).delay(3000).fadeOut(function(){ $(this).remove(); });
                        $("#powerup-"+data.id).remove();



                    break;
                    }

                break;
                case 6: // CharData
                     switch(data['stype']){
                        case 0: // Create Char
                            var p = new Player(data.char);
                            if(p.pos == user.pos) user.onPlayer();

                        break;
                        case 1: // Entferne Charakter
                            user.cache.player[data['pos']].delete();
                        break;
                        case 2: // Bewege Charakter (x=y=0 => Stoppe)
                        var p = user.cache.player[data['pos']];
                            //p.showText("X: "+data.dir.x+" Y: "+data.dir.y);
                            p.stopMove();
                            p.setCoord(data['x'],data['y']);
                            p.startMove(data['dir'].x,data['dir'].y);
                        break;
                        case 3: // Drehe Charakter
                        var p = user.cache.player[data['pos']];
                        p.rotate(data.dir,0);
                        break;
                        case 4: // Charakter verliert Leben
                        var p = user.cache.player[data.pos];
                        p.getHurt(data.dmg);
                        p.showBlood(data.dir);
                        break;
                        case 5: // Lege Waffe an
                        var p = user.cache.player[data.pos];
                        p.addWeapon(data.weapon);
                        break;
                        case 6: // Verändere Geschwindigkeit
                        var p = user.cache.player[data.pos];
                        p.setSpeed(data.s);
                        break;
                     }
                break;
                }


                },
            connect : function(){
                   console.log("Verbinde mit Server");
                   $("#connectionDialog").html('Verbinde zum Spieleserver...').dialog("open");
                server.socket = new WebSocket(server.path);



                this.socket.onopen = function(event){

                        server.connected = true;
                        server.beenconnected = true;

                         $("#connectionDialog").dialog("close");
                         $("#loginDialog").dialog("open");
                         console.log("Verbunden mit Server");
/*
                         server.ping();
                         server.pingtimer = setInterval(server.ping,server.pinginterval);
                            var usr = db.getItem("usrname");
                            var pw = db.getItem("usrpw");
                         if(usr != null && pw != null) user.login(usr,pw);
                         */
                       };
                this.socket.onmessage = function(event){
                  console.log("Nachricht empfangen: "+event.data);
                                server.receive(event.data);
                                };
                this.socket.onclose = function(event){
                                server.connected = false;

                                if(server.beenconnected){ // Disconnect oder Erstverbindungsproblem?
                                window.clearInterval(server.pingtimer);
                                $("#pinglatency").css("background-color","#fff").attr("title","Keine Verbindung").text("");
                                server.latency = [];

                                user.closeAll();
                                user.setOption("kb",false); // Disable Keyboard functions
                                console.log("Verbindung getrennt");
                                $("#connectionDialog").html('Verbindung wurde getrennt...');
                                } else {
                                console.log("Verbindung kann nicht hergestellt werden");
                                $("#connectionDialog").html('Verbindung kann nicht hergestellt werden...');
                                }
                                $("#connectionDialog").append('<br><hr><br>Neuverbindung in <span id="reconnection_timer"></span>');

                                if(server.beenconnected)
                                this.timer = new Timer($("#reconnection_timer"),3,1,0,1000,function(){server.socket = null; server.connect();});
                                else
                                this.timer = new Timer($("#reconnection_timer"),10,1,0,1000,function(){server.socket = null; server.connect();});

                                $("#connectionDialog").dialog("open");

                                };

  }
};
