

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;

public class Conectar { // conectar el programa con nuestra base de datos , login ,  genera una instancia para intercambio de informacion

	static String bd = "Datos_Nodos";   
	static String login = "root";   
	static String password ="155070847";   
	static String url = "jdbc:mysql://localhost/"+bd;   
	Connection conn = null;   
	public void DbConnection() {      
		try {        
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, login, password);
		}
		catch(SQLException e){
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
	}
	public Connection getConnection(){
		return conn;
	}
	public void desconectar(){ // cerrar conexion con la base de datos 
		conn = null;
	}



}
