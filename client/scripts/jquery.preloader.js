$.preloadImages = function(a) {
  for (var i = 0; i < a.length; i++) {
    $("<img />").attr("src", a[i]);
  }
}