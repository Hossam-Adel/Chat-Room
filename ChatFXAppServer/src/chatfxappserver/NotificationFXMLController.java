package chatfxappserver;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Shemo
 */
public class NotificationFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextArea notificationText;
    @FXML
    private Button sendButton;
    @FXML
    private Button CancelButton;
    
    public void cancel() {
       ((Stage) notificationText.getScene().getWindow()).close();
    }

    public void send() {
        try {
            ServerFXMLController.serverImpObj.broadcast(notificationText.getText());
            ((Stage) notificationText.getScene().getWindow()).close();
        } catch (RemoteException ex) {
            Logger.getLogger(NotificationFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
