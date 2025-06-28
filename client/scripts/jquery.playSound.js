(function($){
  let defaultSettings = {controls: false, volume: 1.0, loop: false, forcePlay: true};

  $.extend({
    playSound: function(s, opts = defaultSettings) {
      opts = {...defaultSettings, ...opts}; // Merge default settings with provided options
      console.log("playSound ",s,opts);

      // opts: { volume: 1.0, loop: false, controls: false, forcePlay: false }
      const $audio = $("<audio />", {
        autoplay: false,
        controls: !!opts.controls,
        loop: !!opts.loop
      });
      $audio.append($("<source />", { src: s }));
      $audio.css("display", opts.controls ? "" : "none");
      $audio.appendTo('body');

      // Optionale Lautstärke
      if (opts.volume !== undefined)
        $audio[0].volume = opts.volume;

      // Versuchen zu spielen
      if (opts.forcePlay) {
        const playPromise = $audio[0].play();
        if (playPromise !== undefined) {
          playPromise.then(() => {
            // Erfolg
            console.log("Playback started for:", s);
          }).catch((error) => {
            // Wahrscheinlich Autoplay blockiert
            console.warn("Playback blocked:", error);
            if (opts.controls) {
              // Optional: Controls anzeigen, damit User das Audio manuell starten kann
              $audio.attr("controls", "controls").show();
            }
          });
        }
      }


      return $audio[0];
    }
  });

})(jQuery);
