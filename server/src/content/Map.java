package content;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class Map {
int id,w,h;
String title,layer;
JsonArray overlayer,groundlayer;

public HashMap<Rectangle, Gameobject> gameobjects = new HashMap<>();
public HashMap<Rectangle, Weapon> weapons = new HashMap<>();
public HashMap<Rectangle, PowerUp> powerups = new HashMap<>();
Rectangle rec;


public Map(int mapid){
		try{
			ResultSet rs = Init.db.query_first("SELECT * FROM `map` WHERE id='"+mapid+"'");
			
			this.id = mapid;
			this.title = rs.getString("title"); // Alternative-title
			this.h = rs.getInt("h");
			this.w = rs.getInt("w");
			//JsonObject weapons = Datahandler.JSONdecode(rs.getString("weapons"));
			Blob blob = rs.getBlob("overlayer");
			this.overlayer = Datahandler.JSONdecodeArray(new String(blob.getBytes(1L, (int) blob.length())));
			blob = rs.getBlob("groundlayer");
			this.groundlayer = Datahandler.JSONdecodeArray(new String(blob.getBytes(1L, (int) blob.length())));
			
			this.rec = new Rectangle(new Coord(0,0),null,this.w,this.h,0);
			
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		

	}
public Map clone(){
return new Map(this.id);
}

public JsonObject getJSON(){
		return Json.createObjectBuilder()
				.add("id",id)
				.add("title",this.title)
				.add("w", this.w)
				.add("h", this.h)
				.build();
	}



}