/**
JQuery Rotateplugin by Dominik Hoeltgen
Rotates in the correct direction by depositing angle in .data

**/

$.fn.rotate = function(angle, duration, easing, complete) {
  return this.each(function() {
    var $elem = $(this);
    var ang = $elem.data('angle');
    if(typeof ang == 'undefined') ang = 0;
    if((ang-angle)>180) ang=ang-360;
    if((angle-ang)>180) ang=ang+360;


    $({deg:  ang}).animate({deg: angle}, {
      duration: duration,
      easing: easing,
      step: function(now) {
        $elem.css({
           transform: 'rotate(' + now + 'deg)'
         }).data('angle',now%360);
      },
      complete: complete || $.noop
    });
  });
};