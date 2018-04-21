package client;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMLAddController implements Initializable {
    Stage addNewStage;
    public FXMLAddController(){
        addNewStage = new Stage();
    }
    
      public void contact(){
      Platform.runLater(new Runnable(){
          @Override
          public void run() {
              try {
                  Parent root = FXMLLoader.load(getClass().getResource("ContactList.fxml"));
                  Scene scene = new Scene(root);
                  addNewStage.setScene(scene);
                  addNewStage.show();
               
              } catch (IOException ex) {
                  Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
              }
          }
          
      });   
   }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
  
    }    
    
}
