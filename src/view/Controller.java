package view;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import cpabe.cpabe.Cpabe;
import cpabe.cpabe.EncFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import phrApp.DatabaseHandler;
import phrApp.PHR;

public class Controller implements Initializable {

	private Cpabe cpabe;
	private String prvfile, pubfile;

	// Write data to specific PHR
	@FXML
	private TextField txtWrtPatient;
	@FXML
	private TextField txtWrtAuthor;
	@FXML
	private TextField txtWrtDoctor;
	@FXML
	private TextField txtWrtInsurance;
	@FXML
	private TextField txtWrtPolicy;
	@FXML
	private TextArea txtWrtDetails;
	@FXML
	private Button btnWrtEncrypt;

	// Request data
	@FXML
	private ListView<PHR> lstReadEncPHRs;
	@FXML
	private Button btnReadSearch;
	@FXML
	private TextField txtReadsrchName;
	@FXML
	private Button btnReadDecrypt;
	@FXML
	private TextArea txtReadDetails;
	
	//Manage Entity
	@FXML 
	private TextField txtMngEntNameAdd;
	@FXML 
	private TextField txtMngEntRoleAdd;
	@FXML 
	private Button btnMngEntAdd;
	@FXML 
	private TextField txtMngEntNameRemove;
	@FXML 
	private TextField txtMngEntRoleRemove;
	@FXML 
	private TextField txtMngEntIdRemove;
	@FXML 
	private Button btnMngEntRemove;

	public Controller() {
		DatabaseHandler.authentication("127.0.0.1", 3306, "root", "root");
		cpabe = new Cpabe();
		try {
			cpabe.setup("pub", "msk");
			cpabe.keygen("pub", "prv", "msk", "pid:1 did:2");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnWrtEncrypt.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				System.out.println("Write");
				// Write data to specific PHR
				try {
					int userId = Integer.valueOf(txtWrtPatient.getText());
					int authorId = Integer.valueOf(txtWrtAuthor.getText());
					int doctorId = Integer.valueOf(txtWrtDoctor.getText());
					int insuranceId = Integer.valueOf(txtWrtInsurance.getText());
					String policy = txtWrtPolicy.getText();
					String input = txtWrtDetails.getText();
					//breaks because no pubfile
					EncFile enc = cpabe.enc2("pub", policy, input);
					DatabaseHandler.addPatientHealthRecord(enc.getAesBuf(), enc.getCphBuf(), policy, userId, authorId, doctorId, insuranceId);
					//write enc to the database
					
				} catch (NumberFormatException e) {
					txtWrtDetails.setText("Incorrect input parameters!");
				} catch (Exception e) {
					txtWrtDetails.setText("Incorrect input parameters");
					System.out.println("Exception: " +  e.getMessage());
					
				}
			}
		});

		//Search for PHRs
		btnReadSearch.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				System.out.println("Search");
				String patientName = txtReadsrchName.getText();
				// get all users with the name
				List<Integer> patientIds = DatabaseHandler.searchForPatientIDByPatientName(patientName);
				// then get their PHR's
				ObservableList<PHR> phrView = FXCollections.observableArrayList();
				for (Integer i : patientIds) {
					phrView.addAll(DatabaseHandler.searchForPHRByPatientID(i));
				}
				lstReadEncPHRs.setItems(phrView);
			}
		});
		
		// Decrypt specific PHR
		btnReadDecrypt.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {	
				System.out.println("Decrypt");
				PHR selected = lstReadEncPHRs.getSelectionModel().getSelectedItem();
				if (selected != null) {
					System.out.println("Decrypt PHR");
					try {
						txtReadDetails.setText(cpabe.dec("pub", "prv", selected.getAesBuf(), selected.getCphBuf()));
					} catch (Exception e) {
						System.out.println("Exception " + e.getMessage());
						txtReadDetails.setText("Unable to decrypt the PHR with your private key");
					}
				} else {
					txtReadDetails.setText("Please select a PHR record from the list");
				}
				System.out.println("Name: " + txtReadsrchName.getText());
			}
		});
		
		//Manage Entity button handlers
		btnMngEntAdd.setOnAction(new EventHandler() {
		    @Override
			public void handle(Event event) {
		    	//Add entity
			    String addName = txtMngEntNameAdd.getText();
				String resultRoles = txtMngEntRoleAdd.getText();
				String[] rolesArray = resultRoles.split("-");
				
				SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy");
				//DatabaseHandler dbHandler = new DatabaseHandler();
				//dbHandler.authentication("127.0.0.1", 3306, "root", "root");
				DatabaseHandler.addEntity(addName,rolesArray,ft.format(new Date( )));
			}
		});
		btnMngEntRemove.setOnAction(new EventHandler() {
		    @Override
			public void handle(Event event) {
		    	//Remove entity
		    	String removeName = txtMngEntNameRemove.getText();
		    	String[] removeRoles = {"empty"};
		    	if(!txtMngEntRoleRemove.getText().equals(""))
		    		removeRoles = (txtMngEntRoleRemove.getText()).split("-");
				String removeID = txtMngEntIdRemove.getText();
				
				//DatabaseHandler dbHandler = new DatabaseHandler();
				//dbHandler.authentication("127.0.0.1", 3306, "root", "root");
				DatabaseHandler.removeEntity(removeName,removeRoles,Integer.parseInt(removeID));
				
			}
		});
	}

}
