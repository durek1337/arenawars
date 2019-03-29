clientMeassurement = {
  x : 0,
  y : 0,
  f : 1,
  displayW : 1200, // without scalation
  displayH : 675
};

clientConfiguration = {
    keyScale : 1.4
};

var playercolors = ["ccc","f00","0f0","00f","ff0","0ff","f0f","fcc","cfc","ccf","ffc","f00","0f0","00f","ff0","0ff","f0f","fcc","cfc","ccf","ffc"];

var isMobileDevice = (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent)
    || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0,4)));
var isApp = (typeof intel) != "undefined";
function scaleToDisplay(){
    var w = (isMobileDevice) ? clientMeassurement.x : $(window).width();
    var h = (isMobileDevice) ? clientMeassurement.y : $(window).height();

    var factorX = w/clientMeassurement.displayW;
    var factorY = h/clientMeassurement.displayH;
    var factor = (factorX > factorY) ? factorY : factorX;
    clientMeassurement.f = factor;

    console.log("Scale: "+factor+", X: "+factorX+", Y: "+factorY+", Window-width: "+w+", Window-height: "+h);
    $("#display").css("transform","scale("+factor+")");
}
function justify(){
    scaleToDisplay();
    var h = (isMobileDevice) ? clientMeassurement.y : $(window).height();
    $(".ui-dialog-content").dialog("option","maxHeight",h).dialog("option", "position", {my: "center", at: "center", of: "#all"});
}
if(isMobileDevice)
$(window).on("orientationchange",function(){
console.log("Orientation was changing");
//alert("Diese Webapp ist nicht für Mobile Geräte gedacht. Lade dir besser die App herunter!");
setTimeout(function(){
clientMeassurement.x = $(window).width();
clientMeassurement.y = $(window).height();
justify();
},400);
});

if(!isMobileDevice)
$(window).resize(justify);

$(window).disableSelection();

var $backgroundMusic = null;

$(function(){

  console.log("W: "+clientMeassurement.x+" H: "+clientMeassurement.y);


var images = ["images/gameobject/1.png","images/blood.png","images/checkicon.png","images/fist.png"];

for(var i=1;i<=20;i++){
images.push("images/charset/death"+i+".png");
images.push("images/charset/pos"+i+".png");
}
for(var i=1;i<=3;i++){
images.push("images/weapon/"+i+"/bullet.png");
images.push("images/weapon/"+i+"/fire.png");
images.push("images/weapon/"+i+"/ground.png");
images.push("images/weapon/"+i+"/hand.png");
images.push("images/weapon/"+i+"/hud.png");
}

$.preloadImages(images);

$(window).blur(function(){
keyboard.releaseAllKeys();
});


$backgroundMusic = $.playSound("sounds/lobby"+Math.ceil(Math.random()*2)+".mp3");
$backgroundMusic.loop = true;
$backgroundMusic.volume = user.soundMusic;


$("#connectionDialog").dialog({
    title : "Serververbindung",
    autoOpen : false,
    modal: false,
    resizable: false,
    draggable: false,
    open: function(event, ui) { $(".ui-dialog-titlebar-close", $(this).parent()).hide(); },
});


$("#errorDialog, #messageDialog").dialog({
    autoOpen : false,
    modal: true,
    resizable: false,
    draggable: true,
    buttons: {
    Ok: function() {
             $( this ).dialog( "close" );
        }
      },
    open: function(){
      user.modalmsg++;
    },
    close: function(){
      user.modalmsg--;
    }
    });

$(".ttip").tooltip({
  content: function() {
        return $(this).attr('title');
    }
});

  var select = $("#creationForm select[name='limit']");
    var slider = $("#limitSlider");
    slider.slider({
      min: 2,
      max: 10,
      range: "min",
      step: 1
    });
    combineSelectSlider(select,slider);
    slider.slider("value",4);

 var select = $("#creationForm select[name='rounds']");
    var slider = $("#roundsSlider");
    slider.slider({
      min: 1,
      max: 20,
      range: "min",
      step: 1
    });
    combineSelectSlider(select,slider);
    slider.slider("value",3);

 var select = $("#creationForm select[name='health']");
    var slider = $("#healthSlider");
    slider.slider({
      min: 10,
      max: 200,
      range: "min",
      step: 10
    });
    combineSelectSlider(select,slider);
    slider.slider("value",100);



    var $creationCheckboxes = $("input[type=checkbox]",$("#creationForm"));
    $creationCheckboxes.onoff().onoff('enable').not("input[name=respawn],input[name=weapondrop],input[name=powerups]").prop('disabled',true);

mouse.init();
keyboard.init();
chat.init();
auth.init();
menu.init();
user.initConfig();

var checkClientLatency = function(t){
var time = (new Date()).getTime();
var dif = 500;
user.setClientLatency(time-t-dif);
setTimeout(checkClientLatency,dif,time);
}
checkClientLatency();

var applyMeassurements = function(){
clientMeassurement.x = $(window).width();
clientMeassurement.y = $(window).height();

  $("#lobby").tabs({heightStyle: "fill"}).tabs("disable",2);
  $("#room").tabs({heightStyle: "fill"});

justify();
}
scaleToDisplay();

setTimeout(function(){
  applyMeassurements();
  server.connect();
},500); // Leider Fehler im Chrome, selbst nach Laden der DOM-Struktur noch nicht die richtige Übergabe der Fenstergröße

if(!isMobileDevice)
$("#lobbyinstruction").html('<img src="images/instruction.jpg" alt="Anleitung" style="border-radius:20px;">');
else
$("#lobbyinstruction").html('<img src="images/cross.png" alt="Steuerkreuz" style="border-radius:20px;float:left;"> Bewege Dich mithilfe des Steuerkreuzes. Tappe in die Richtung in die du schie&szlig;en oder schlagen m&ouml;chtest.<br><br><div style="float:right;background-image:url(images/key_use.png);width:80px;height:80px;"></div><div style="float:right;background-image:url(images/key_reload.png);width:80px;height:80px;"></div> Verwende das Handsymbol zum Aufheben und das Munitionssymbol zum Nachladen einer Waffe.');

if(isApp)
$(":input").blur(function(){
    intel.xdk.device.hideStatusBar();
});


});

document.addEventListener("intel.xdk.device.ready",function(){
    //this function actually hides the status bar
    intel.xdk.device.hideStatusBar();
},false);

