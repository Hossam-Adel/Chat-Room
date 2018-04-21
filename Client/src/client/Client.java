package client;

import Server.ChatServerInt;
import Server.ClientInt;
import Server.UserDTO;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author Ahmed Ali
 */
public class Client extends Application {
    public static UserDTO  currClientDTO;
    public Registry reg ;
    public static  ChatServerInt ref; 
    public static ClientInt currClient;
    
    @Override
    public void start(Stage stage) throws Exception {
        try{
//            System.setProperty("java.rmi.server.hostname","10.0.0.146");
//            reg = LocateRegistry.getRegistry("10.0.0.146", 3000);
            String url="rmi://192.168.43.171:1099/chatting";
//            reg = LocateRegistry.getRegistry();

            ref = (ChatServerInt) Naming.lookup(url);
            LoginMain m = new LoginMain();
        }
        catch(Exception ex){
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Server Unavailable");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Sorry, Server is not available!");
            alert.showAndWait();
        }  
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
