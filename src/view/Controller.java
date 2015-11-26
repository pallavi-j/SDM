package view;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import cpabe.cpabe.Cpabe;
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
	private TextField txtWrtUserId;
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
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnWrtEncrypt.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				// Write data to specific PHR
				int userId;
				try {
					userId = Integer.valueOf(txtWrtUserId.getText());
					
					String policy = txtWrtPolicy.getText();
					String input = txtWrtDetails.getText();
					
					cpabe.enc(pubfile, policy, input);
					
				} catch (NumberFormatException e) {
					txtWrtDetails.setText("Incorrect user ID");
				} catch (Exception e) {
					txtWrtDetails.setText("Error occured during the encryption");
				}
			}
		});

		btnReadSearch.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				String patientName = txtReadsrchName.getText();
				// get all users with the name
				List<Integer> patientIds = DatabaseHandler.searchForPatientIDByPatientName(patientName);
				// then get their PHR's
				ObservableList<PHR> phrs = FXCollections.observableArrayList();
				for (Integer i : patientIds) {
					System.out.println("PatientId: " + i);
					phrs.addAll(DatabaseHandler.searchForPHRByPatientID(i));
				}
				lstReadEncPHRs.setItems(phrs);

				System.out.println("Search ended");
				// System.out.println("Name: " + txtReadsrchName.getText());
			}
		});

		btnReadDecrypt.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				// Decrypt specific PHR
				System.out.println("Decrypt");
				PHR selected = lstReadEncPHRs.getSelectionModel().getSelectedItem();
				if (selected != null) {
					System.out.println("Decrypt PHR");
					try {
						txtReadDetails.setText(cpabe.dec(pubfile, prvfile, selected.getAesBuf(), selected.getCphBuf()));
					} catch (Exception e) {
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
