package content;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.websocket.RemoteEndpoint.Basic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;






import java.util.TreeMap;

import org.glassfish.tyrus.server.Server;

//import org.glassfish.tyrus.container.grizzly.*;
//import org.glassfish.tyrus.spi.ServerContainerFactory;

public class Init { // Initialisierungsklasse	
	public static int charSize = 50; // 50x50 pixel
	public static int charWidth = 50; // 50x30 pixel Kollision
	public static int charHeight = 30;
	
	
	
	static MySQL db = new MySQL();
	public static Channel lobby = null;
	public static HashMap<Integer, Account> accounts = new HashMap<>(); // Table for depositing active accounts
	public static HashMap<String, Basic> wssessions = new HashMap<>(); // Table for linking active websocket-sessions to logged in Account-IDs
	public static HashMap<String, Integer> wsacc = new HashMap<>(); // Table for connecting wssid with accountid
	public static TreeMap<Integer,Room> rooms = new TreeMap<>();
	public static HashMap<Integer, Map> maps = new HashMap<>();
	public static HashMap<Integer, Weapon> weapons = new HashMap<>();
	public static HashMap<Integer, Gameobject> gameobjects = new HashMap<>();

	
	
	public static void main(String args[]){

		int counter = 0;

		System.out.println("*** Initialisierung eingeleitet! ***");
		// counter = maps();
		lobby = new Channel(0,"Lobby",(Account a )->{ a.send(Room.getListJSON()); },(Account a )->{});
		System.out.println("* Lobby-Channel initialisiert");
		counter = maps();
		System.out.println("* Maps ("+counter+") initialisiert");
		counter = gameobjects();
		System.out.println("* Gameobjects ("+counter+") initialisiert");		
		counter = weapons();
		System.out.println("* Waffen ("+counter+") initialisiert");		

		System.out.println("*** -Starten des Servers- ***");
		runServer();

	}

	private static void runServer() {
		  Server server = new Server("localhost", 8081, "/arenawars", null, Websocket.class);

			 try {
				 server.start();
				 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				 System.out.println("Please press a key to stop the server...");
				 reader.readLine();
				 
				 } catch (Exception e) {
				 e.printStackTrace();
				 } finally {
					 try{
					 server.stop();
					 } catch(Exception e){
						 e.printStackTrace();
					 } finally {
						 System.out.println("--- Server shutted down ---");
						 System.exit(0);
					 }
				 }
	     

	    }


	static int maps(){
		int i = 0;
		Map testmap;
		ResultSet rs = db.query("SELECT `id` FROM `map`");
		try{
			while(rs.next()){
				i++;
				int mapid = rs.getInt("id");
				testmap = new Map(mapid);
				maps.put(mapid, testmap);
				
			}
		} catch(SQLException ex){
			
		}
		return i;
	}
	static int gameobjects(){
		Gameobject[] go = {
				new Gameobject(1,"Kiste").setSize(50, 50).setPassable(false).setGround(true).setInterleaving(false), // Kiste
				};
		int i;
		for(i=0;i<go.length;i++){
			Init.gameobjects.put(i+1,go[i]);	
		}
		i++;
		
		return i;
	}
	static int weapons(){
		Weapon[] w = {
				
				new Weapon(1,"Pistole",12,600,200,2000,12,false).setBulletSize(4, 20).setDistance(40).setGroundSize(35, 35),
				new Weapon(2,"Minigun",6,600,150,5000,60,true).setBulletSize(4, 20).setDistance(60).setGroundSize(60, 60),
				new Weapon(3,"Shotgun",16,1000, 300, 1000, 2, false).setBulletSize(20, 20).setDistance(50).setGroundSize(60, 60)
		};
		int i;
		for(i=0;i<w.length;i++){
			Init.weapons.put(i+1,w[i]);	
		}
		i++;
		
		return i;
	}

	static void addaccount(int id, Account a){
	accounts.put(id,a);
	}
	static void addsession(String s, Basic b, int a){
		wssessions.put(s,b);
		wsacc.put(s, a);
	}
}
