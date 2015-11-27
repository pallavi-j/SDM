package view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import cpabe.cpabe.Common;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import phrApp.LoggedInUser;

public class ControllerLogIn implements Initializable {
	
	final FileChooser fileChooser = new FileChooser();
	
	// User login view, getting userID and private-key
	@FXML
	private TextField txtLoginUserID;
	@FXML
	private TextField txtBrowsedFilePath;
	@FXML
	private Button btnBrowse;
	@FXML
	private Button btnUserLoginCancel;
	@FXML
	private Button btnUserLogin;
	
	public ControllerLogIn() {
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnUserLoginCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit(); // cancel will close the application
			}
		});
		
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//Stage currentStage;
				//currentStage = (Stage) btnBrowse.getScene().getWindow();
				//Stage fileChooserStage = currentStage;
				File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    try {
						txtBrowsedFilePath.setText(file.getCanonicalPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
			}
		});
		
		btnUserLogin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					LoggedInUser loggedInUser = new LoggedInUser(Integer.parseInt(txtLoginUserID.getText()), Common.suckFile(txtBrowsedFilePath.getText()));
					Stage thirdStage;
					//Parent root;
					thirdStage = (Stage) btnUserLogin.getScene().getWindow();
					FXMLLoader loader = new FXMLLoader(getClass().getResource("LoggedIn.fxml"));
					//root = FXMLLoader.load(getClass().getResource("LoggedIn.fxml"));
					Scene scene = new Scene(loader.load());
					thirdStage.setScene(scene);
					thirdStage.sizeToScene();
					Controller controller = loader.<Controller>getController();
					controller.initData(loggedInUser);
					thirdStage.show();
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		
	}

}
