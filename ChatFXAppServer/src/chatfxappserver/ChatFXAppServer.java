/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatfxappserver;



import Server.UserDTO;
import java.util.ArrayList;
import java.util.Vector;
import javafx.application.Application;
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
public class ChatFXAppServer extends Application {
    public static Vector<UserDTO> online_users;
    public static UserDTO admin;
//    private Stage window;
    private double xPosition = 0;
    private double yPosition = 0;
    public static  Stage parentStage; 

    Parent root;
    @Override
    public void start(Stage stage) throws Exception {
        
//        window = stage;
        parentStage = stage;
        FXMLLoader loader = new FXMLLoader();
        root = loader.load(getClass().getResource("FXMLDocument.fxml")
                .openStream());
        FXMLDocumentController controller = loader.getController();

        root.setOnMousePressed((MouseEvent event) -> {
            xPosition = stage.getX() - event.getScreenX();
            yPosition = stage.getY() - event.getScreenY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() + xPosition);
            stage.setY(event.getScreenY() + yPosition);
        });
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("serverfxml.css").toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }
//    @FXML
//    private void sceneHandler() throws IOException{
//        System.out.println("Scene changing...");
//        root = FXMLLoader.load(getClass().getResource("mainGUI.fxml"));
//        window.setScene(new Scene(root));
//    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
