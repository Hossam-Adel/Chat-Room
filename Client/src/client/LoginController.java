/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Server.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Ahmed Ali
 */
public class LoginController implements Initializable {

//    Registry reg;
    @FXML
    private Label invalidUser;
    private double xPosition = 0;
    private double yPosition = 0;
    // public static UserDTO user;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private TextField textField;
    @FXML
    private PasswordField password;
    @FXML
    private Button register;

    public void registerButtonPressed() throws IOException {
        Parent root1 = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Scene scene1 = new Scene(root1);
        LoginMain.parentStage.setScene(scene1);
    }

    @FXML
    public void login() {

        try {
            System.out.println("login btn clicked");
            UserDTO user1 = new UserDTO();
            user1.setEmail(textField.getText());
            user1.setPassword(password.getText());

            Object obj = Client.ref.login(user1);
            if (obj == null) {
                invalidUser.setVisible(true);
                invalidUser.setText("Invalid");
                System.out.println("no client");
            } else {
                Client.currClientDTO = (UserDTO) obj;
                Client.ref.changeStatus("online", Client.currClientDTO.getId());
                if (Client.currClientDTO.getType().equals("u")) {
                    Client.currClient = new ClientImpl();
                    Client.ref.register(Client.currClient);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Parent root1 = FXMLLoader.load(getClass().getResource("ContactList.fxml"));
                                Scene scene1 = new Scene(root1);
                                root1.setOnMousePressed((MouseEvent event) -> {
                                    xPosition = LoginMain.parentStage.getX() - event.getScreenX();
                                    yPosition = LoginMain.parentStage.getY() - event.getScreenY();
                                });
                                root1.setOnMouseDragged((MouseEvent event) -> {
                                    LoginMain.parentStage.setX(event.getScreenX() + xPosition);
                                    LoginMain.parentStage.setY(event.getScreenY() + yPosition);
                                });
                                LoginMain.parentStage.setScene(scene1);

                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                }
            }

        } catch (RemoteException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
