package phrApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Statement;

import cpabe.cpabe.Cpabe;

public class DatabaseHandler {
	
	private static String url;
	private static String user;
	private static String password;
	private static Connection connection = null;
	private static PHR resultPHR;
	private static List<PHR> resultPHRList;
	private static List<Integer> resultIDList;
	//private static List<String> resultTESTList;
	private static int successFlag;
	private static int rowsAffected;
	//private static String responseAddEnt;
	
	//private static Cpabe cpabe2;
	private static String privateFile = "F:/University of Twente/SDM/";
	private static String publicFile = "C:/Users/Arthur/git/SDM/publicKey/";
	private static String masterFile = "C:/Users/Arthur/git/SDM/masterKey/master_key";
	
	public DatabaseHandler() {
		//cpabe2 = new Cpabe();
	}

	
	
	/**
	 * Initializes private fields user, password and URL and checks the success of database connection 
	 * using these parameters.
	 * 
	 * @param ip					The IP address of the database server.
	 * @param port					The port of the database server.
	 * @param user					The user account on the database server.
	 * @param password				The password for the user account on the database server.
	 * @return						True/false for successful/unsuccessful connection attempt.
	 */
	public static boolean authentication(String ip, int port, String user, String password) {
		DatabaseHandler.user = user;
		DatabaseHandler.password = password;
		url = "jdbc:mysql://" + ip + ":" + port + "/access_control_DB";
		if (startConnection() == 0) {
			closeConnection();
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a Patient Health Record (PHR) to the database with all columns.
	 * 
	 * @param detail				Encrypted payload of the PHR to be added.
	 * @param policy				Policy of the PHR to be added.
	 * @param ownerID				Owner ID of the PHR to be added.
	 * @param authorID				Author ID of the PHR to be added.
	 * @param doctorID				Doctor ID of the PHR to be added.
	 * @param insuranceID			Insurance ID of the PHR to be added.
	 * @return						0/-1 for successful/unsuccessful execution.
	 * @exception SQLException 		An exception that provides information on a database access error or other errors.
	 */
	public static int addPatientHealthRecord(byte[] aesBuf, byte[] cphBuf, String policy, int ownerID, int authorID, int doctorID, int insuranceID) {
		startConnection();
		PreparedStatement preparedStatement = null;
		successFlag = -1;
		rowsAffected = 0;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO access_control_DB.patient_health_record " +
					"(aesBuf, cphBuf, policy, owner_patient_user_id, author_user_id, doctor_user_id, insurance_co_user_id) VALUES (?, ?, ?, ?, ?, ?, ?);");
			preparedStatement.setBytes(1, aesBuf);
			preparedStatement.setBytes(2, cphBuf);
			preparedStatement.setString(3, policy);
			preparedStatement.setInt(4, ownerID);
			preparedStatement.setInt(5, authorID);
			preparedStatement.setInt(6, doctorID);
			preparedStatement.setInt(7, insuranceID);
			rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0)
				successFlag = 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(preparedStatement);
		return successFlag;
	}
	
	//TODO: temp method, shoudl be deleted when Cpabe.enc and Cpabe.dec are completely implemented. 
	public static int addTemp(byte[] aesBuf, byte[] cphBuf) {
		startConnection();
		PreparedStatement preparedStatement = null;
		successFlag = -1;
		rowsAffected = 0;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO access_control_DB.patient_health_record " +
					"(aesBuf, cphBuf) VALUES (?, ?);");
			preparedStatement.setBytes(1, aesBuf);
			preparedStatement.setBytes(2, cphBuf);
			rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0)
				successFlag = 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(preparedStatement);
		return successFlag;
	}
	
	/**
	 * Searches for PHRs within the database, containing patientID as the search criteria.
	 * 
	 * @param patientID				The previously selected patient's ID, which will be used for matching PHRs.
	 * @return						A List containing PHR objects for each PHR matching the search criteria.
	 * @exception SQLException 		An exception that provides information on a database access error or other errors.
	 */
	public static List<PHR> searchForPHRByPatientID(int patientID) {
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		resultPHRList = new ArrayList<PHR>();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM access_control_DB.patient_health_record WHERE owner_patient_user_id = ?;");
			preparedStatement.setInt(1, patientID);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				resultPHR = new PHR(resultSet.getInt("id"), resultSet.getBytes("aesBuf"), resultSet.getBytes("cphBuf"), resultSet.getString("policy"), 0, 0, 0, 0); //TODO fill in foreign keys
						//resultSet.getInt("owner_patient_user_id"), resultSet.getInt("author_user_id"), resultSet.getInt("doctor_user_id"), 
						//resultSet.getInt("insurance_user_id"));
				resultPHRList.add(resultPHR);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(preparedStatement, resultSet);
		return resultPHRList;
	}
	
	/**
	 * Searches for patient IDs within the database, containing patientName as the search criteria.
	 * 
	 * @param patientName			A patient's name, which will be used for matching patient IDs.
	 * @return						An array containing patient IDs, matching the search criteria.
	 * @exception SQLException 		An exception that provides information on a database access error or other errors.
	 */
	public static List<Integer> searchForPatientIDByPatientName(String patientName) {
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		resultIDList = new ArrayList<Integer>();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM access_control_DB.user WHERE name = ? AND "
					+ "EXISTS (SELECT 1 FROM access_control_DB.patient WHERE access_control_DB.user.id = access_control_DB.patient.user_id);");
			preparedStatement.setString(1, patientName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				resultIDList.add(resultSet.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(preparedStatement, resultSet);
		return resultIDList;
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
	 * 
	 * @param preparedStatement		preparedStatement to be checked before the closure of the connection.
	 * @param resultSet				resultSet to be checked before the closure of the connection.
	 */
	private static void closeConnection(PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
            if (resultSet != null)
            	resultSet.close();
            if (preparedStatement != null)
            	preparedStatement.close();
            if (connection != null)
            	connection.close();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Closes a connection to the database.
	 * 
	 * @param preparedStatement		preparedStatement to be checked before the closure of the connection.
	 */
	private static void closeConnection(PreparedStatement preparedStatement) {
		try {
            if (preparedStatement != null)
            	preparedStatement.close();
            if (connection != null)
            	connection.close();
        } catch (SQLException e) {
        	e.printStackTrace();
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
	
	public static byte[] searchForaesBufByPHRID(int PHRID) {
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		resultPHRList = new ArrayList<PHR>();
		byte[] output = null;
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM access_control_DB.patient_health_record WHERE idTemp = ?;");
			preparedStatement.setInt(1, PHRID);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			Blob blob = resultSet.getBlob("aesBuf");
			output = blob.getBytes(1, (int) blob.length());
			blob.free();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(preparedStatement, resultSet);
		return output;
	}
	
	public static byte[] searchForcphBufByPHRID(int PHRID) {
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		resultPHRList = new ArrayList<PHR>();
		byte[] output = null;
		try {
			preparedStatement = connection.prepareStatement("SELECT cphBuf FROM access_control_DB.patient_health_record WHERE idTemp = ?;");
			preparedStatement.setInt(1, PHRID);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			Blob blob = resultSet.getBlob("cphBuf");
			output = blob.getBytes(1, (int) blob.length());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(preparedStatement, resultSet);
		return output;
	}

	/*
	//This is just for testing purposes.....
	public List<String> getUserList(){
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		resultTESTList = new ArrayList<String>();
		
		try {
			preparedStatement = connection.prepareStatement("SELECT name FROM access_control_DB.user;");
			//preparedStatement.setString(1, patientName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				resultTESTList.add(resultSet.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		closeConnection(preparedStatement, resultSet);
		return resultTESTList;
		
	}*/

	public static void  addEntity(String nameEntity,String[] rolesEntity,String dateAdded, Cpabe cpabe2) throws Exception{
		//responseAddEnt = "";
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet generatedID = null;
		int affectedRows;
		int affectedRowId;
		
		try {
			String insertUserSQL = "INSERT INTO access_control_db.user "
					  			 + "(name) VALUES"
					  			 + "(?);";
			
			preparedStatement = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, nameEntity);

			affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 1) {
				generatedID = preparedStatement.getGeneratedKeys();
				if (generatedID.next()){
					affectedRowId = generatedID.getInt(1);
					int count = 0;
					while(count<rolesEntity.length){
						String insertRoleSQL = "INSERT INTO access_control_db." + rolesEntity[count++]
					  			 			 + " (user_id,detail) VALUES"
					  			 			 + " (?,?);";
						preparedStatement = connection.prepareStatement(insertRoleSQL);
						preparedStatement.setInt(1,affectedRowId);
						preparedStatement.setString(2, dateAdded);
						//JOptionPane.showMessageDialog(null, insertRoleSQL);
						affectedRows = preparedStatement.executeUpdate();
						if (affectedRows != 1) {
							throw new SQLException("Creating user failed, no rows affected.");
						}
					}
					
					/*---------------------------------------------------------------*/
					//Create Keys
					publicFile = publicFile + nameEntity + affectedRowId;
					boolean successFolder = (new File(publicFile)).mkdirs();
					if (successFolder) {
					    //Directory user creation
						publicFile = publicFile + "/pub_key";
						File successFile = new File(publicFile);
						if(!successFile.exists()){
							//File user creation
							successFile.createNewFile();
							
							System.out.println("//start to setup");
							//try {
								cpabe2.setup(publicFile, masterFile);
							//} catch (ClassNotFoundException | IOException e) {
								// TODO Auto-generated catch block
						//		e.printStackTrace();
							//}
							System.out.println("//end to setup");
							
							System.out.println("//start to keygen");
							try {
								String attr = "userName:"+nameEntity + " userId:" + affectedRowId;
								for(int i=0;i<rolesEntity.length;i++){
									attr += " role" + i + ":" + rolesEntity[i];
								}
								privateFile = privateFile + "prv_key" + affectedRowId;
								cpabe2.keygen(publicFile, privateFile, masterFile, attr);
							} catch (NoSuchAlgorithmException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("//end to keygen");
						} else
							throw new Exception("Failed to create entity.");
					} else
						throw new Exception("Failed to create entity.");
					
					
					
					/*
					System.out.println("//start to enc");
					cpabe.enc(publicFile, policy, "Patient is infected");
					System.out.println("//end to enc");

					System.out.println("//start to dec");
					cpabe.dec(publicFile, privateFile, 1);
					System.out.println("//end to dec");*/
					/*---------------------------------------------------------------*/
	            } else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
	        } else{
	        	throw new SQLException("Creating user failed, no rows affected.");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnection(preparedStatement, generatedID);
		//return responseAddEnt;
	}
	
	public static void  removeEntity(String removeName,String[] removeEntity,Integer removeID){
		//responseAddEnt = "";
		startConnection();
		PreparedStatement preparedStatement = null;
		ResultSet generatedID = null;
		if(removeEntity[0].equals("empty")){
			String[] removeEntity2 = {"patient","doctor","employer","insurance_co","user"};
			removeEntity = removeEntity2;
		}
		
		//int affectedRows;
		ResultSet test = null;
		/*int affectedRowId;*/
		
		try {
			int count=0;
			while(count<removeEntity.length){
				String column = (removeEntity[count]=="user") ? "id=" : "user_id=";
				String removeRoleSQL = "DELETE FROM access_control_db." + removeEntity[count++]
									 + " WHERE " + column +  + removeID + ";";
				preparedStatement = connection.prepareStatement(removeRoleSQL);
				preparedStatement.executeUpdate();
				//if (affectedRows != 1) {
				//	JOptionPane.showMessageDialog(null, "Deleting Entity failed.\n One of the entity you're trying to delete may not have existed");
				//	throw new SQLException("Deleting entity failed");
				//}
			}
			JOptionPane.showMessageDialog(null, "Succesfully deleted entity related to user "+removeName+"("+removeID+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnection(preparedStatement, test);
		//return responseAddEnt;
	}
	
}
