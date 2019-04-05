package content;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import javax.json.Json;
import javax.websocket.RemoteEndpoint.Basic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.glassfish.tyrus.server.Server;

public class Init { // Initialisierungsklasse	
	public static int charSize = 50; // 50x50 pixel
	public static int charWidth = 50; // 50x30 pixel Kollision
	public static int charHeight = 30;
	
	
	
	static MySQL db = null;
	public static Channel lobby = null;
	public static HashMap<Integer, Account> accounts = new HashMap<>(); // Table for depositing active accounts
	public static HashMap<String, Basic> wssessions = new HashMap<>(); // Table for linking active websocket-sessions to logged in Account-IDs
	public static HashMap<String, Integer> wsacc = new HashMap<>(); // Table for connecting wssid with accountid
	public static TreeMap<Integer, Room> rooms = new TreeMap<>();
	public static HashMap<Integer, Map> maps = new HashMap<>();
	public static HashMap<Integer, Weapon> weapons = new HashMap<>();
	public static HashMap<Integer, Gameobject> gameobjects = new HashMap<>();
	public static HashMap<Integer, PowerUp> powerups = new HashMap<>();
	
	
	public static HashMap<String,String> config = new HashMap<>();

	
	
	public static void main(String args[]){
		try{
		int counter = 0;

		System.out.println("*** Initialisierung von ArenaWars v1.04 eingeleitet! ***");
		// counter = maps();
		lobby = new Channel(0,"Lobby",(Account a) -> {
			try{
			a.send(Room.getListJSON());
			} catch(Exception e){
				e.printStackTrace();
			}
			lobby.receive(Json.createObjectBuilder().add("type", 4).add("stype",8).add("entered",true).build());
			},
			(Account a) -> { 
			lobby.receive(Json.createObjectBuilder().add("type", 4).add("stype",8).add("entered",false).build());	
			});
		
		System.out.println("* Lobby-Channel initialisiert");
		BufferedReader br = new BufferedReader(new FileReader("config.ini"));
		String l= br.readLine();
		while(l != null){
			String[] r = l.split("=",2);
			config.put(r[0], (r.length == 1) ? "" : r[1]);
			l = br.readLine();
		}
		br.close();
		
		System.out.println("* Konfigurationsdatei eingelesen");
		MySQL.setConfig(config.get("mysql_host"), config.get("mysql_user"), config.get("mysql_password"), config.get("mysql_name"), new Integer(config.get("mysql_port")));
		db = new MySQL();
		
		System.out.println("* MySQL-Verbindung hergestellt");
		
		
		counter = maps();
		System.out.println("* Maps ("+counter+") initialisiert");
		counter = gameobjects();
		System.out.println("* Gameobjects ("+counter+") initialisiert");		
		counter = weapons();
		System.out.println("* Waffen ("+counter+") initialisiert");		
		counter = powerups();
		System.out.println("* PowerUps ("+counter+") initialisiert");	
		
		System.out.println("*** -Starten des Servers- ***");
		runServer();
		} catch(Exception e){
			System.out.println("Der Server konnte nicht gestartet werden!");
			e.printStackTrace();
			System.exit(0);
		}

	}

	private static void runServer() throws Exception {
		  Server server = new Server("localhost", new Integer(config.get("ws_port")), "/arenawars", null, Websocket.class);
		 		System.out.println("starting...");
		 		server.start();
				System.out.println("started!");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Please press a key to stop the server...");
				reader.readLine();
					
			    try{
			    	System.out.println("Server is shutting down...");
			    	ArrayList<Account> accounts = new ArrayList<>(Init.accounts.values());
					 for(Account a : accounts){
						 a.logout();						 
					 }
			    	server.stop();
					 
				} catch(Exception e){
					e.printStackTrace();
				} finally {
					 System.out.println("--- Server shutted down ---");
					 System.exit(0);
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
				new Gameobject(1,"Kiste").setSize(50, 50).setPassable(false).setGround(true).setInterleaving(false) // Kiste
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
				new Weapon(2,"Minigun",6,600,150,5000,60,true).setBulletSize(4, 20).setWalkSpeed(0.6).setDistance(60).setGroundSize(60, 60),
				new Weapon(3,"Shotgun",16,1000, 300, 1000, 2, false).setBulletSize(20, 20).setWalkSpeed(0.8).setDistance(50).setGroundSize(60, 60)
		};
		int i;
		for(i=0;i<w.length;i++){
			Init.weapons.put(i+1,w[i]);	
		}
		i++;
		
		return i;
	}
	static int powerups(){
		PowerUp[] p = {
				new PowerUp(1,"Global Speed-Walk",0).setMovementSpeed(1.6).setDuration(20000),
				new PowerUp(2,"Global Slow-Shot",0).setProjectileSpeed(0.4).setDuration(20000),
				new PowerUp(3,"Global Slow-Walk",0).setMovementSpeed(0.625).setDuration(20000),
				new PowerUp(4,"Global Fast-Shot",0).setProjectileSpeed(2).setDuration(20000),
				new PowerUp(5,"Fast-Shot",1).setProjectileSpeed(2).setDuration(20000),
				new PowerUp(6,"Fast-Walk",1).setMovementSpeed(1.6).setDuration(20000)
		};
		int i;
		for(i=0;i<p.length;i++){
			Init.powerups.put(i+1,p[i]);	
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
