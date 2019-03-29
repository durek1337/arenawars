function Timer(id,v,s,p,t,f){ // DOM-Id, Value, Step, Precission, Time between each Countdown, function to execute
var self = this;
this.d = id; // DOM target
this.value = v;
this.p = p;
this.t = t;
this.funct = f;
this.d.text(v.toFixed(p));

this.cd = function(){
// alert(self.value);
 self.value -= s;
if(self.value <= 0)
self.value = 0;
self.d.html(self.value.toFixed(p));

if(self.value == 0){
clearInterval(self.interval);
self.funct();
}

}


this.interval = setInterval(this.cd,t);

}