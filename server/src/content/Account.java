package content;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.json.*;
import javax.websocket.Session;
//import org.json.simple.JSONObject;


public class Account {
int id,rank;
String name,pw,wssid;
Spectator spectator;
Session session;
ArrayList<Channel> channels = new ArrayList<>();

public Account(int accid, String websocketsid){
ResultSet rs = null;
this.spectator = null;

try{
rs = Init.db.query_first("SELECT * FROM `account` WHERE id="+accid);
id=accid;
wssid = websocketsid;
rank = rs.getInt("rank");
name = rs.getString("name");
session = null;

} catch(SQLException ex){
System.out.println(ex.getMessage());
}


}

void enterChannel(Channel c){
	synchronized(this.channels){
	c.add(this);
	this.channels.add(c);
	}
}
void leaveChannel(Channel c){
	synchronized(this.channels){
	c.remove(this);
	this.channels.remove(c);
	}
}
void leaveAllChannels(){
	synchronized(this.channels){
		ArrayList<Channel> ccopy = new ArrayList<Channel>(this.channels);
	
		for(Channel c : ccopy){
			leaveChannel(c);
		}
	}
		

}
public void bindSpectator(Spectator s){
this.spectator = s; 
}


void send(JsonObject obj) throws Exception{
	System.out.println(this.getID()+": "+obj.toString());
if(this.session != null){
		if(this.session.isOpen())
		this.session.getBasicRemote().sendText(obj.toString());
}
}




static Integer verify(String n, String p){ // If the Account exists return the id else return 0
int accid = 0;
try {
ResultSet rs = null;
rs = Init.db.query_first("SELECT `id`,`name` FROM `account` WHERE name='"+MySQL.esc(n)+"' AND pw=MD5('"+MySQL.esc(p)+"')"); 

if(rs != null)
accid = rs.getInt("id");
} catch(Exception ex){
ex.printStackTrace();
}
return accid;
}



public static Account get(String wssid){
	return Init.accounts.get(Init.wsacc.get(wssid));

}
public String getID(){
	return "Account #"+this.id;
}
@Override
public String toString(){
	return getID();
}



public static void logout(String wssid) throws Exception{
Integer accid = Init.wsacc.get(wssid);
Account checkacc = null;
if(accid != null)
checkacc = Init.accounts.get(accid);
if(checkacc != null)
checkacc.logout();
}

public void logout() throws Exception{
System.out.println("Account #"+this.id+" tries to logout");
leaveAllChannels();

	JsonObject obj = Json.createObjectBuilder()
			.add("type", 3) // type 3: Authentifizierung
			.add("stype",11)
			.build();
	this.send(obj);
	this.session = null;
	Init.accounts.remove(this.id);
	Init.wsacc.remove(this.wssid);
	System.out.println("Account #"+this.id+" logged out");
	
}


public void login(Session session){
	this.session = session;
	Init.addaccount(this.id, this);
	Init.addsession(session.getId(),session.getBasicRemote(),this.id); // Key is session.getId()
	enterChannel(Init.lobby); // Lobbychannel
	
}

public void sendError(String msg) {
	try {
		this.send(Json.createObjectBuilder().add("type", 1).add("stype", 0).add("msg", msg).build());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}
public void sendMessage(String msg) {
	try {
		this.send(Json.createObjectBuilder().add("type", 1).add("stype", 1).add("msg", msg).build());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

public static int getId(String n){
	try{
	ResultSet rs = Init.db.query_first("SELECT `id`,`name` FROM `account` WHERE name='"+MySQL.esc(n)+"'");

if(rs == null) return 0;
return rs.getInt("id");
} catch(Exception e){
	return 0;
}
	
}
public static boolean register(String n, String pw, String email){
	if(getId(n) > 0 || n.length() <= 3 || !Datahandler.isValidName(n) || pw.length() < 6 || !Datahandler.isValidEmail(email))
		return false;
	
	 try {
		 System.out.println("Versuche Account mit Name "+n+" zu erstellen");
		PreparedStatement preparedStmt = Init.db.conn.prepareStatement("INSERT INTO `account` (`name`,`pw`,`email`) VALUES (?, MD5(?), ?)");
		preparedStmt.setString(1, n);
		preparedStmt.setString(2, pw);
		preparedStmt.setString(3, email);
		preparedStmt.execute();
		return true;
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	 
}
}

