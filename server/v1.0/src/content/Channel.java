package content;

import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;


public class Channel {
	ArrayList<Account> receiver = new ArrayList<>();
	int type;
	String name;
	ChannelAction onEnter;
	ChannelAction onLeave;
	
	public Channel(int type, String name, ChannelAction onEnter, ChannelAction onLeave){
		this.type = type;
		this.name = name;
		this.onEnter = onEnter;
		this.onLeave = onLeave;
	}
	
	public void add(Account acc){
		this.onEnter.proc(acc);
		this.receiver.add(acc);
		System.out.println(acc.getID()+" entered Channel "+name);
		System.out.println(this.receiver.toString());
	}	
	public void remove(Account acc){
		this.receiver.remove(acc);
		this.onLeave.proc(acc);
		System.out.println(acc.getID()+" leaved Channel "+name);
		System.out.println(this.receiver.toString());
	}
	public void removeAll(ChannelAction onLeave){ // Wird nach dem initialisierten onLeave ausgeführt
		ArrayList<Account> rest = new ArrayList<>(this.receiver);
		for(Account a : rest){
			remove(a);
			onLeave.proc(a);
		}	
	}
	public void iterateAll(ChannelAction onCheck){ // Wird nach dem initialisierten onLeave ausgeführt
		ArrayList<Account> rest = new ArrayList<>(this.receiver);
		for(Account a : rest) onCheck.proc(a);
	}


	public void receive(JsonObject obj){
		try{
		for(Account a : this.receiver) a.send(obj);
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(this.receiver.toString());
		}
	}
	public void receiveText(String n, String msg){
		receive(Json.createObjectBuilder().add("type",1).add("stype",2).add("n", n).add("msg",msg).build());
	}
	@Override
	public String toString(){
		return this.name;
	}
	
}
