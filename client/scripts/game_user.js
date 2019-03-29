user = {
    id : 0,
    name : "",
    roomid : 0,
    pos : 0,
    ingame : false,
    alive : false,
    active : false,
    modalmsg : 0,
    display : {w: 800, h : 600},
    follow : null,
    soundFx : 0.5,
    soundMusic : 1,
    setClientLatency : function(l){
      l = (l>0) ? l : 0;
        $("#clientlatency").css('background-color','#'+((l > 100) ? ((l > 500) ? 'ff0000' : 'ffff00') : '00ff00')).attr('title',"Client-Latenz: "+l+"ms").text(l);
    },
    login : function(name,pw){
        $("#loginstatus").html('Authentifiziere...<hr>');
        server.send({"type": 3, "stype" : 10, "id" : name, "pw" : pw});
        db.setItem("usrname",name);
        db.setItem("usrpw",pw);

    },
    logout : function(){
        user.active = false;
        server.send({"type": 3, "stype" : 11});
        db.removeItem("usrname");
        db.removeItem("usrpw");
    },

    createRoom : function(form){
        data = {
          "title" : form.title.value,
          "mapid" : parseInt(form.mapid.value),
          "limit" : parseInt(form.limit.value),
          "rounds" : parseInt(form.rounds.value),
          "health" : parseInt(form.health.value),
          "teams" : (form.teams.checked),
          "friendlyfire" : (form.friendlyfire.checked),
          "respawn" : (form.respawn.checked),
          "weapondrop" : (form.weapondrop.checked),
          "revenge" : (form.revenge.checked),
          "powerups" : (form.powerups.checked)

        };
        console.log(data);
        server.send({"type" : 4, "stype" : 0, "data" : data});

        return false;
    },
    getRoom : function(){
      var room = user.cache.rooms[user.roomid];
      if(typeof room == "undefined") return null;
      else return room;
      },

    leaveRoom : function(){
        console.log("Schicke Anfrage zum Verlassen des Spiels");
        server.send({"type" : 4, "stype" : 1});
    },
    joinRoom : function(id){
        console.log("Versuche Raum #"+id+" beizutreten...");
       server.send({"type": 4, "stype" : 2, "roomid" : id});
    },
    cache: {
     player : [],
     rooms : []
    },

    options : {
      "movable": false,
      "rotatable" : false,
      "kb": false,
      "menu" : false,
      "firable" : false,
      "sounds" : true,
      "music" : true
      },
    configs : {
      "sounds" : true,
      "music" : true
    },
    setConfig : function(k,v){
      console.log("Set config "+k+" to "+v);
      user.configs[k] = v;
        var handler = {
            "music" : function(value){
               $backgroundMusic.muted=!value;
               $("#musiccontrol").attr("src","images/"+((value) ? "" : "no")+"sound.png");
            }
        }
        var hf = handler[k];
        if(typeof hf != "undefined") hf(v);
      db.setItem('config',$.stringify(user.configs));
    },
    getConfig : function(k){
       return user.configs[k];
    },
    initConfig : function(){

      var c = db.getItem("config");
      if(c != null){
        c = $.parseJSON(c);
      for (var k in c) user.setConfig(k,c[k])
      }
    },
        setOption : function(opt, val){
        console.log("Useroption: "+opt+" = "+val);
        user.options[opt] = val;
        },
        getOption: function(opt){
          if(this.options[opt] != undefined)
          return this.options[opt];
          else {
            console.log("Useroption "+opt+" nicht gefunden")
          return false;
          }
        },

    dist : function(mouseX,mouseY) {
      var elem = this.cache.player[this.id].dom;
        return {x : Math.floor(mouseX - (elem.offset().left+(elem.width()/2))), y :  Math.floor(mouseY - (elem.offset().top+(elem.height()/2)))};
    },
    showError : function(msg){
         $("#errorDialog").html(msg).dialog("open");
    },
    showMessage : function(msg){
         $("#messageDialog").html(msg).dialog("open");
    },
    sendChatMessage : function(msg){
        server.send({"type" : 4, "stype" : 4, "msg" : msg})
    },
    closeAll : function(){
      console.log("Verberge alle UIs...");
      $("#lobby, #room, .publicchat").hide();
      $(".ui-dialog-content").not("#connectionDialog").dialog("close");
      $("#player").html("");
      $("#loginscreen").show();

      if(user.ingame) user.onLeave();

    },
    onSpectator : function(){
      user.setOption("kb", false);
      user.setOption("firable", false);
      user.setOption("rotatable", false);
      user.setOption("movable", false);
     if(isMobileDevice)
      $(".touchcontrol").hide();

      $("#handlerAll").hide();

     $("#roomchat").show().css("opacity",0.6);
     var $interface = $("#spectatorinterface");
     var $ddmenu = $("select",$interface);
     $ddmenu.html('<option value=0>[ Niemandem ]</option>');
     for(pos in user.cache.player){
       $ddmenu.append($("<option />",{value: pos, text : user.cache.player[pos].name}));
     }
     $ddmenu.change(function(){
       if(this.value == 0)
       user.noFollow();
       else
       user.cache.player[this.value].follow();
     });
     $interface.show();
    },

    onPlayer : function(){
        user.alive = true;

      user.setOption("kb", true);
      user.setOption("firable", true);
      user.setOption("rotatable", true);
      user.setOption("movable", true);
     $("#spectatorinterface").hide();
     $("#roomchat").hide();
     if(isMobileDevice)
     $(".touchcontrol").show();

     $("#handlerAll").show();

    },
    onEnter : function(){
      user.ingame = true;
      user.setOption("menu",true);
      $("#gameobjectsGround, #gameobjectsOver").html('');
      $("#overlayComplete").hide();
      $("#overlayMid").show();
      console.log("ENTERED GAME");
      $backgroundMusic.volume = 0.1;
    },
    onLeave : function(){
     $("#roomchat").css("opacity",1).hide();
     $("#spectatorinterface, #hud").hide();
     $("#gameobjectsGround").html('');
     $("#player").html('');
     $("#map,#overlayMid,#overlayComplete").hide();

     if(isMobileDevice)
      $(".touchcontrol").hide();

      $("#handlerAll").hide();


     $(".bullet").remove();
     user.cache.player = [];
     var r = user.getRoom();
     var rt = null;
     if(r != null) var rt = r.roundtimer;

     if(user.roomid > 0 && rt != null){
     clearTimeout(rt);
     }
     user.pos = 0;
     user.follow = null;
     user.setOption("kb", false);
     user.setOption("menu",false);
     user.setOption("firable", false);
     user.setOption("movable", false);
     user.setOption("rotatable", false);

    },
    onDeath : function(){
      user.alive = false;
      console.log("You died...");
      user.onSpectator();

    },
    noFollow : function(){
      if(this.follow != null)
      this.follow.isFollowing = false;
      this.follow = null;
      $("#hud").hide();
    }
};