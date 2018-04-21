package chatfxappserver;

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Operations;

/**
 * FXML Controller class
 *
 * @author Shemo
 */
public class ServerFXMLController implements Initializable {

    @FXML
    private PieChart chart;
    @FXML
    private ToggleButton serviceButton;
    @FXML
    private VBox statisticsBox;
    @FXML
    private RadioButton genderRadioB;
    @FXML
    private RadioButton countriesRadioB;
    @FXML
    private Label numberOfUsersLabel;
    @FXML
    private Label numberOfOnlineUsers;
    @FXML
    private Label numberOfOfflineUsers;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView closeIcon;
    
    private static int statisticType = 0;
    private boolean serviceState;
    public static ChatServerImpl serverImpObj;
    Registry registry;

    @FXML
    private void changeSreviceState() {
        String url="rmi://192.168.43.171:1099/chatting";
        try {
            if (serviceButton.isSelected()) {
                Naming.rebind(url, serverImpObj);
                serviceButton.setText("Stop Service");
                serviceButton.setStyle("-fx-background-color: #ff3232;");
                System.out.println("okkkk");
                
               new Operations().setOfline();
            } else {
                Naming.unbind(url);
                serviceButton.setText("Start Service");
                serviceButton.setStyle("-fx-background-color: #48e82c;");
                System.out.println("service stopped");
                 new Operations().setOfline();
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    public void closeWindow() {
        new Operations().setOfline();
        Platform.exit();
    }

    public void minimize() {
        ((Stage) minimizeIcon.getScene().getWindow()).setIconified(true);
    }
    
    public void notifyUsers() {
        Stage newStage = new Stage();
                    NotificationFXMLController notificationCtrl = new NotificationFXMLController();
                    FXMLLoader loader = new FXMLLoader();
//                    loader.setController(notificationCtrl);
                    try {
                        Parent newRoot = loader.load(getClass().getResource("NotificationFXML.fxml").openStream());
                        newRoot.getStylesheets().clear();
//                                newRoot.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                        newStage.setScene((new Scene(newRoot)));
                        newStage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
    }
    
    @FXML
    private void setStatisticChoice(){
        if(genderRadioB.isSelected())
            statisticType = 0;
        else
            statisticType = 1;
        chart.setData(generateData()); 
        chart.setClockwise(false);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            serviceState = false;
            serverImpObj = new ChatServerImpl();
//            System.setProperty("java.rmi.server.hostname","10.0.0.146");
//            registry = LocateRegistry.createRegistry(3000);
            registry = LocateRegistry.createRegistry(1099);
//            registry = LocateRegistry.getRegistry();
            closeIcon.setImage(new Image(getClass().getResource("close.png").toExternalForm()));
            minimizeIcon.setImage(new Image(getClass().getResource("mini.png").toExternalForm()));
            numberOfUsersLabel.setText((new Operations().getNumberOfUsers()) + "");
            numberOfOnlineUsers.textProperty().bind(serverImpObj.getClientsVectorSize().asString());
            final NumberBinding offlineUsers = Bindings.subtract(new Operations().getNumberOfUsers(), serverImpObj.getClientsVectorSize());
            numberOfOfflineUsers.textProperty().bind(offlineUsers.asString());
            ToggleGroup radioGroup = new ToggleGroup();
            genderRadioB.setToggleGroup(radioGroup);
            countriesRadioB.setToggleGroup(radioGroup);
            genderRadioB.selectedProperty().setValue(true);
            chart.setData(generateData()); 
            chart.setClockwise(false);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ObservableList<PieChart.Data> generateData() {
        if(statisticType ==0)
        return (new Operations().getGenderStatistics());
        else
            return (new Operations().getContriesStatistics());
    }
}
