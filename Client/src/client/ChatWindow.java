/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Server.UserDTO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Shemo
 */
public class ChatWindow {

    private double xOffset = 0;
    private double yOffset = 0;
    public static Stage parentStage;
    private FXMLDocumentController chatCntrl;
    
    public ChatWindow(UserDTO user) {
        try {

            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            chatCntrl = new FXMLDocumentController(user);
            loader.setController(chatCntrl);
            System.out.println(getClass().getName());
            loader.setLocation(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public FXMLDocumentController getChatController(){
        return chatCntrl;
    }
}
