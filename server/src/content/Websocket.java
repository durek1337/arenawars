package content;
import java.io.IOException;
//import java.util.Calendar;




import javax.websocket.CloseReason; // Nutzen
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.server.ServerEndpoint;
import javax.json.*;

 
/** 
 * @ServerEndpoint gives the relative name for the end point
 */
@ServerEndpoint(value = "/control") 
public class Websocket {

	
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
        try {
        	Init.db.checkConnection();
//			session.getBasicRemote().sendText(Json.createObjectBuilder().add("type",0).add("online",0).add("registered",0).toString());
		
        } catch (Exception e) {
			e.printStackTrace();
		}
        
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
 

	@OnMessage
    public void onMessage(String message, Session session){
		//System.out.println(message);
		
	if(Datahandler.isValidJSON(message)){
    JsonObject obj = Datahandler.JSONdecode(message); // decode the String to a JSON-object
    int type = obj.getInt("type");
    Account acc = Account.get(session.getId());
    
    int accid = acc == null ? 0 : acc.id;
    Boolean ingame = (accid > 0 && acc.spectator != null) ? true : false;
	Basic user = session.getBasicRemote();
    JsonObjectBuilder databuilder = Json.createObjectBuilder();

	//final Calendar now = Calendar.getInstance();
	
switch(type){
case 0:
		databuilder.add("type", 0);
		if(ingame) databuilder.add("l", acc.spectator.room.getLatency());
break;

case 1: // Type 1: Keyboard
	int subtype = obj.getInt("stype");
	int key = obj.getInt("key");
	if(ingame){
		if(subtype == 0)
		acc.spectator.keyDown(key);
		if(subtype == 1)
		acc.spectator.keyUp(key);
		if(subtype == 2)
		acc.spectator.keyPressed(key);
	} else System.out.println("Empfange Tastaturdaten ausserhalb des Spiels");
	
break;
case 2: // Typ 2: Mouse
	switch(obj.getInt("stype")){
	case 1:
		if(accid != 0 && acc.spectator != null)
		acc.spectator.rotate(obj.getInt("angle"));
	break;
	}
break;
case 3: // Type 3: Authentifizierung
	switch(obj.getInt("stype")){
		case 1:
			databuilder.add("type",3).add("stype", 1).add("exists", Account.getId(obj.getString("name")) > 0);
		break;
		case 2:
			databuilder.add("type",3).add("stype",2).add("succeed", Account.register(obj.getString("n"), obj.getString("pw"), obj.getString("email")));
		break;
		case 10: // Accountlogin
			System.out.println("Typ 3 (Authentifizierung): Versuche Accountlogin...");
		    accid = Account.verify(obj.getString("id"),obj.getString("pw")); // Check the existence of the account with account-name as identifier and the given password. Finally return the id.
		    if(accid > 0){
		// Accountinformation correct
			Account checkacc = Init.accounts.get(accid);
				try {
					if(checkacc != null){
					System.out.println("Account bereits eingeloggt...");
					checkacc.logout();
					}
				} catch (Exception e) {
					System.out.println("Fehler beim Kicken von "+checkacc.getID());
					e.printStackTrace();
				} // Daher kicken
			
		Account testacc = new Account(accid,session.getId());
		testacc.login(session); // Save Account into Init-Tables and enter Lobby Channel


		System.out.println("Account #"+accid+": "+testacc.name+" logged in");


		databuilder
			.add("id", accid)
			.add("type",3)
			.add("stype",10)
			.add("name", testacc.name)
			.add("rank",testacc.rank)
			.add("sid",session.getId());



		} else {
			
		databuilder
			.add("type",3)
			.add("stype",10)
			.add("id", 0);

			System.out.println("Account nicht gefunden. Return ID #0.");
		}			
		break;
		case 11: // Accountlogout
		try {
			acc.logout();
		} catch (Exception e) {
			acc.sendError("Fehler beim Logout");
		}
		break;
	}

break;

case 4: // Game-Data
	if(accid > 0){ // Wenn eingeloggt
	switch(obj.getInt("stype")){
		case 0: // Erstelle Room
			new Room(acc,obj.getJsonObject("data"));
			
		break;
		case 1: // Verlasse Room oder Spiel
			if(acc.spectator != null){// Wenn Mitglied eines Raumes
				acc.spectator.room.leave(acc);
			
				acc.enterChannel(Init.lobby); // Geh zurück zur Lobby
			}
				//System.out.println(acc.getID()+": Trying to leave "+acc.spectator.room.getID());

		break;
		case 2: // Trete Room bei
			if(acc.spectator == null){ // Wenn noch kein Mitglied eines Raumes
				Room r = Init.rooms.get(obj.getInt("roomid"));
				if(r != null)
				r.join(acc);
			}
		break;
		case 3: // Starte Spiel / (Ready)
			if(acc.spectator != null && (acc.spectator.isLeader() || acc.spectator.room.creator == null)) // Nur Leader
				acc.spectator.room.startgame();
			else acc.sendMessage("Du musst Raumersteller sein um das Spiel zu starten");
		break;
		case 4: // Chatnachricht
			try{
			if(acc.spectator != null)
				acc.spectator.room.gamechannel.receive(Json.createObjectBuilder().add("type", 1).add("stype",2).add("pos",acc.spectator.pos).add("n",acc.spectator.getName()).add("msg", obj.getString("msg")).build());
			} catch(Exception ex){
				ex.printStackTrace();
			}
		break;

	}
	}
	
	
break;

}


JsonObject jo = databuilder.build();
if(jo.size() > 0){

// Sende Antwort
        try {
        	String a = jo.toString();
        	System.out.println("Antwort: "+a);
            user.sendText(a);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
}
// Sende Antwort Ende
 
    } else
		System.out.println("["+session.getId()+"] Not valid: "+message); // Print msg if it was not a valid JSON-String
}
 
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason){
    	System.out.println(session.getId()+" is disconnecting...");
        try {
			Account.logout(session.getId());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fehler beim Verbindungsabbruch-Logout");
		}
        Init.wssessions.remove(session.getId());
        System.out.println(session.getId()+" has been disconnected");
    }

    
}