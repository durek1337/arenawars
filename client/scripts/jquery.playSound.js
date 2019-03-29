(function($){

  $.extend({
    playSound: function(s){
      $a = $("<audio />",{"autoplay" : "autoplay"});
      $a.append($("<source />",{"src" : s}));
      //$a.append($("<embed />", {"src" : s, "hidden" : "true", "autostart" : "true", "loop" : "false"}));
      return $a.appendTo('body')[0];
    }
  });

})(jQuery);