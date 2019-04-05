package content;

import javax.json.Json;
import javax.json.JsonObject;

public class Statistic implements Comparable<Statistic> {
	int pos;
	int kills, deaths, dmgout, dmgin, shoots, hits, powerups, actions; 
	
	public Statistic(int pos){
		this.pos = pos;
		this.kills=0;
		this.deaths=0;
		this.dmgout=0;
		this.dmgin=0;
		this.shoots=0;
		this.hits=0;
		this.powerups=0;
		this.actions=0;
	}
	public int getDeaths(){
		return this.deaths;
	}
	public void addKills(int k){
		this.kills+=k;
	}
	public void addDeaths(int d){
		this.deaths+=d;
	}
	public void addDmgIn(int d){
		this.dmgin+=d;
	}
	public void addDmgOut(int k){
		this.dmgout+=k;
	}
	public void addShoots(int k){
		this.shoots+=k;
	}
	public void addHits(int h){
		this.hits+=h;
	}
	public void addPowerups(int p){
		this.powerups+=p;
	}
	public JsonObject getJSON(){
		return Json.createObjectBuilder().add("pos",this.pos).add("kills", this.kills).add("deaths", this.deaths).add("dmgout",this.dmgout).add("dmgin", this.dmgin).add("shoots",this.shoots).add("accuracy", (this.shoots > 0) ? Math.round(100*this.hits/this.shoots) : -1).build();
	}
	
	@Override
	public int compareTo(Statistic sr) {
		int r = 0;
		if(sr.deaths < this.deaths)
			r = 1;
		else r = -1;
		
		if(r == 0)
			if(sr.kills > this.kills)
				r = 1;
			else r = -1;
		if(r == 0)
			if(sr.dmgout > this.dmgout)
				r = 1;
			else r = -1;
		if(r == 0)
			if(sr.dmgin < this.dmgin)
				r = 1;
			else r = -1;
		if(r == 0 && sr.shoots > 0 && this.shoots > 0)
			if((sr.hits/sr.shoots) > (this.hits/this.shoots))
				r = 1;
			else r = -1;
			
		return r;
	}
	
	public Statistic add(Statistic sr){
		this.kills += sr.kills;
		this.deaths += sr.deaths;
		this.dmgin += sr.dmgin;
		this.dmgout += sr.dmgout;
		this.hits += sr.hits;
		this.shoots += sr.shoots;
		this.powerups += sr.powerups;

		return this;
	}
	
}
