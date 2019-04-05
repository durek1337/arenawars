package content;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


/**
    while(rs.next()){ // Datenreihen auswerten
    System.out.println(rs.getString("name"));
    	
    }
 */



public class MySQL {
	private String db_host = "localhost";
	private String db_user = "root";
	private String db_pw = "";
	private String db_name = "arenawars";

	public Connection conn = null;
	
	// Notice, do not import com.mysql.jdbc.*
	// or you will have problems!
//public static void main(String[] args){
	/**
static void main(String args[]){


}
**/
public MySQL(){
LoadDriver(); // In Glassfish nicht notwendig
connect();
}
public void connect(){
	System.out.println("Stelle Verbindung zur Datenbank her");
	try{
		this.conn = DriverManager.getConnection("jdbc:mysql://"+this.db_host+"/"+this.db_name+"?" +
	            "user="+db_user+"&password="+db_pw);
		conn.setAutoCommit(true);
		
	} catch (Exception ex) {
	    System.out.println("Die Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden");
	    ex.printStackTrace();
	} 
}
public void LoadDriver(){
//	System.out.println("Initiere Datenbanktreiber");

	try {
        // Der Aufruf von newInstance() ist ein Workaround
        // für einige misslungene Java-Implementierungen
        Class.forName("com.mysql.jdbc.Driver").newInstance(); // com.mysql.jdbc.Driver
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
public void checkConnection() throws SQLException{
	if(this.conn.isClosed())
		this.connect();
}

public ResultSet query(String query){
	Statement stmt = null;
	ResultSet rs = null;
  // statements allow to issue SQL queries to the database
	try{
	checkConnection();
	stmt = conn.createStatement();
	stmt.executeQuery(query);
    
	rs = stmt.getResultSet();
	} catch(SQLException sqlEx) {
		System.out.println(sqlEx.getMessage());
	}

	return rs;
}

public PreparedStatement update(String query){
	PreparedStatement pstmt = null;
	try{
	checkConnection();
	pstmt = conn.prepareStatement(query);
//	System.out.println("MySQL: Bereite Update vor");
	} catch(SQLException sqlEx) {
		System.out.println(sqlEx.getMessage());
	}

return pstmt;	
}

public ResultSet query_first(String query){
	Statement stmt = null;
	ResultSet rs = null;
	  // statements allow to issue SQL queries to the database
		try{
		checkConnection();
		stmt = conn.createStatement();
		stmt.executeQuery(query+" LIMIT 1");
	    
		rs = stmt.getResultSet();
		if(!(rs.isBeforeFirst() && rs.first()))
		rs = null;
		
		} catch(SQLException sqlEx) {
			System.out.println(sqlEx.getMessage());
		}

		return rs;

}
public static ResultSet getFirst(ResultSet rs){
	try {
		rs.first();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
return rs;
}

public static String esc(String str) { // "Escapes" a String, preventing MySQL-Injection
          if (str == null) {
              return null;
           }
           return str.replace("'", "''");
      }

public Boolean login_check(int charid, String sid){
	ResultSet rs = null;
	Boolean check = false;


rs = query("SELECT count(*) FROM `character` WHERE id='"+charid+"' AND sid='"+sid+"' LIMIT 1");

try {
	rs.next();
    if(rs.getLong(1) == 1)
    check = true;
} catch(SQLException ex) {
	
}


return check;
}


public String test(){
String name="";
ResultSet rs = null;
System.out.println("Testaufruf Anfang");

	rs = query("SELECT `name` FROM `character`");
try{
    while(rs.next()){
    name = rs.getString("name");
    }
} catch(SQLException ex) {
	
}
System.out.println("Testaufruf Ende");	
return name;

}




}
