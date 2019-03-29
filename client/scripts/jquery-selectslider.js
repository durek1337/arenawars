/**
JQuery Sliderplugin by Dominik Hoeltgen
**/
var combineSelectSlider = function(select,slider){
var min = slider.slider("option","min");
var max = slider.slider("option","max");
var step = slider.slider("option","step");

i=min;
select.html('');
while(i <= max){
select.append("<option value='"+i+"'>"+i+"</option>");
i = i+step;
}

select.change(function(){
    var value = $(this).val();
    if(slider.slider("value") != value)
    slider.slider("value",value);
});
    var f = function(){
    var value = $(this).slider("value");

    if(select.val() != value)
        select.children("option[value="+value+"]").attr("selected","selected");

}
slider.on("slidechange",f);

}