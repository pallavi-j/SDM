package phrApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
	
	private static String url;
	private static String user;
	private static String password;
	private static Connection connection = null;
	
	/**
	 * Initializes private fields user, password and url and checks the success of database connection 
	 * using these parameters.
	 * 
	 * @param ip					The ip address of the database server.
	 * @param port					The port of the database server.
	 * @param user					The user account on the database server.
	 * @param password				The password for the user account on the database server.
	 * @return						True/false for successful/unsuccessful connection attempt.
	 */
	public static boolean authentication(String ip, int port, String user, String password) {
		DatabaseHandler.user = user;
		DatabaseHandler.password = password;
		url = "jdbc:mysql://" + ip + ":" + port + "/movieDB";
		if (startConnection() == 0) {
			closeConnection();
			return true;
		}
		return false;
	}
	
	/**
	 * Starts a connection to the database.
	 * 
	 * @return						0/-1 for successful/unsuccessful connection attempt.
	 */
	private static int startConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, user, password);
			return 0;
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
			return -1;
		} catch (SQLException e) {
//			e.printStackTrace();
			return -1;
		} catch (InstantiationException e) {
//			e.printStackTrace();
			return -1;
		} catch (IllegalAccessException e) {
//			e.printStackTrace();
			return -1;
		}
		
	}
	
	/**
	 * Closes a connection to the database.
	 */
	private static void closeConnection() {
		try {
            if (connection != null)
            	connection.close();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
	}

}
