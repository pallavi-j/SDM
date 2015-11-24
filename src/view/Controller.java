package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller implements Initializable {
	
	//Write data to specific PHR
	@FXML private TextField txtWrtUserId;
	@FXML private TextField txtWrtUserName;
	@FXML private TextArea txtWrtDetails;
	@FXML private Button btnWrtEncrypt;
	
	//Request data 

	public Controller() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnWrtEncrypt.setOnAction(new EventHandler() {
		    @Override
			public void handle(Event event) {
		    	//Write data to specific PHR
				System.out.println("Encrypt");
				System.out.println("ID: " + txtWrtUserId.getText());
				System.out.println("userName: " + txtWrtUserName.getText());
				System.out.println("Details: " + txtWrtDetails.getText());
				//txfHello.setText("Hello!");
			}
		});

	}

}
