package content;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;

import javax.json.*;


public class Datahandler {
	public static boolean isValidEmail(String m){
		String p = "^([^\\x00-\\x20\\x22\\x28\\x29\\x2c\\x2e\\x3a-\\x3c\\x3e\\x40\\x5b-\\x5d\\x7f-\\xff]+|\\x22([^\\x0d\\x22\\x5c\\x80-\\xff]|\\x5c[\\x00-\\x7f])*\\x22)(\\x2e([^\\x00-\\x20\\x22\\x28\\x29\\x2c\\x2e\\x3a-\\x3c\\x3e\\x40\\x5b-\\x5d\\x7f-\\xff]+|\\x22([^\\x0d\\x22\\x5c\\x80-\\xff]|\\x5c[\\x00-\\x7f])*\\x22))*\\x40([^\\x00-\\x20\\x22\\x28\\x29\\x2c\\x2e\\x3a-\\x3c\\x3e\\x40\\x5b-\\x5d\\x7f-\\xff]+|\\x5b([^\\x0d\\x5b-\\x5d\\x80-\\xff]|\\x5c[\\x00-\\x7f])*\\x5d)(\\x2e([^\\x00-\\x20\\x22\\x28\\x29\\x2c\\x2e\\x3a-\\x3c\\x3e\\x40\\x5b-\\x5d\\x7f-\\xff]+|\\x5b([^\\x0d\\x5b-\\x5d\\x80-\\xff]|\\x5c[\\x00-\\x7f])*\\x5d))*$";
		return m.matches(p);
	}
	public static boolean isValidName(String n){
		String p = "^[\\w_-]*$";
		return n.matches(p);
	}
	
	public static int getNewPosition(HashMap<Integer, Object> arrlist, int limit){ // Eine freie Position wird aufsteigend gesucht. Hat ein Spieler die Position verlassen, kann sie eingenommen werden
		int pos = 1;

		synchronized(arrlist){
		Object[] objkeys = arrlist.keySet().toArray();
		Integer[] keys = Arrays.copyOf(objkeys, objkeys.length, Integer[].class);
		Arrays.sort(keys);

		for(int id : keys){
			if(pos < id){
				 return pos;
			} else pos++;	
		}
		if(pos > limit)
			pos = 0;

		}

		return pos;
		}		
	
	
	public static boolean isValidJSON(String jsonMessage) {
	    try {
	      // Check if incoming message is valid JSON
	      Json.createReader(new StringReader(jsonMessage)).readObject();
	      return true;
	    } catch (Exception e) {
	      return false;
	    }
	  }
	
	public static JsonObject JSONdecode(String jsonMessage){
		return Json.createReader(new StringReader(jsonMessage)).readObject();
	}
	public static JsonArray JSONdecodeArray(String jsonMessage){
		return Json.createReader(new StringReader(jsonMessage)).readArray();
	}
	
	public static int[] JsonIntArrayToArray(JsonArray jarr){
		int size = jarr.size();
		int[] arr = new int[size];
		if (jarr != null) {
		   for (int i=0;i<size;i++){ 
		    arr[i] = jarr.getInt(i);
		   } 
		} 
		return arr;
	}

	

}