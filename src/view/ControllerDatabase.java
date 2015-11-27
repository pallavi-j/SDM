package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import phrApp.DatabaseHandler;

public class ControllerDatabase implements Initializable {

	@FXML
	private TextField txtServerIP;
	@FXML
	private TextField txtServerPort;
	@FXML
	private TextField txtServerUsername;
	@FXML
	private PasswordField txtServerPassword;
	@FXML
	private Button btnDatabaseCancel;
	@FXML
	private Button btnDatabaseOK;
	
	public ControllerDatabase() {
		
	}
	
	private boolean checkAuthentication(String serverIPInput, Integer serverPortInput, String userInput, String passInput) {
		return DatabaseHandler.authentication(serverIPInput, serverPortInput, userInput, passInput);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnDatabaseOK.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Get all the given fields and authenticate to the Database. Then move to Login view.
				if(checkAuthentication(txtServerIP.getText(), Integer.parseInt(txtServerPort.getText()), txtServerUsername.getText(), txtServerPassword.getText())) {
					Stage secondaryStage;
					Parent root;
					secondaryStage = (Stage) btnDatabaseOK.getScene().getWindow();
					try {
						root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
						Scene scene = new Scene(root);
						secondaryStage.setScene(scene);
						secondaryStage.sizeToScene();
						secondaryStage.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		btnDatabaseCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit(); // cancel will close the application
			}
		});
		
	}
	
	

}
