package phrApp;

/**
 * @author Uraz
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseHandler.authentication("127.0.0.1", 3306, "richard", "12345");
		System.out.println(DatabaseHandler.searchForaesBufByPHRID(2));
		System.out.println(DatabaseHandler.searchForaesBufByPHRID(2));

	}

}
