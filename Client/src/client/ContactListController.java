package client;

import Server.ChatServerInt;
import Server.UserDTO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Ahmed Ali
 */
public class ContactListController implements Initializable {

    /**
     * Initializes the controller class.
     */
    ChatServerInt ref = Client.ref;
    @FXML
    private ListView listview;
    private ObservableList<UserDTO> friends;
    private ObservableList<UserDTO> friendRequests;
    @FXML
    private Label userNameProfile;
    @FXML
    private Label status;
    @FXML
    private ScrollPane requestsPane;
    @FXML
    private ListView requestsListView;
    @FXML
    private TextField search_field;
    @FXML
    private Button search_button;
    @FXML
    private ListView search_list;
    @FXML
    private Label search_result;
    @FXML
    private Tab notification;
    @FXML
    private Tab contactList;
    @FXML private ImageView profile;
    @FXML
    private ComboBox status_box;
    @FXML
    private Button Logout;
    @FXML
    private ImageView mini;
    @FXML
    private ImageView cancel;
    @FXML
    private void add() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Parent root1 = FXMLLoader.load(getClass().getResource("FXMLAdd.fxml"));
                    Scene scene1 = new Scene(root1);
                    LoginMain.parentStage.setScene(scene1);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Client.currClient.setContactListCntrl(this);
            friends = FXCollections.observableArrayList();
            friendRequests = FXCollections.observableArrayList();

            userNameProfile.setText(Client.currClientDTO.getFirstname()+" "+Client.currClientDTO.getLastname());
            
            friends.setAll(ref.getContactList(Client.currClientDTO));
            listview.setCellFactory(new ListItemFactory(this));
            listview.setItems(friends);

            status_box.setCellFactory(new ComboBoxFactory());
            status_box.getItems().addAll("online","busy","away");
            status_box.setButtonCell((ListCell) status_box.getCellFactory().call(null));
            //new edit
            status_box.setValue("online");
            if(Client.currClientDTO.getImg()==null){
                System.out.println("no image");
            }
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(Client.currClientDTO.getImg()));
            Image userImage = SwingFXUtils.toFXImage(img, null);
            profile.setImage(userImage);
        } catch (RemoteException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        notification.setOnSelectionChanged((event) -> {
            System.out.println("client.ContactListController.initialize()");
            onNotificationsOpen(Client.currClientDTO);
            
        });
        contactList.setOnSelectionChanged((event) -> {
            System.out.println("client.ContactListController.initialize()");
            onContatctListOpen();
        });
    }
    public void searchAction(){
        
        ArrayList<UserDTO> result;// 
        ObservableList<UserDTO> returned= FXCollections.observableArrayList();
        try {
            result= ref.searchClients(search_field.getText().toLowerCase());
            if(result==null){
               search_result.setText("person not found");
            }else{
               
                search_list.setCellFactory(new SearchListFactory(this));
                returned.setAll(result);
                search_list.setItems(returned);
                
            }
            search_result.setText("");
        } catch (RemoteException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void onContatctListOpen(){
     friends = FXCollections.observableArrayList();
        friendRequests = FXCollections.observableArrayList();
         try {
            //userNameProfile.setText(Client.currClientDTO.getFirstname());
            //status.setText(Client.currClientDTO.getStatus());
            friends.setAll(ref.getContactList(Client.currClientDTO));
            listview.setCellFactory(new ListItemFactory(this));
            listview.setItems(friends);
            
           // friendRequests.setAll(ref.getFriendRequests(Client.currClientDTO));
            //requestsListView.setCellFactory(new ListItemFactory(this));
            //requestsListView.setItems(friendRequests);
        }catch (RemoteException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public void onNotificationsOpen(UserDTO revevier){
        
        System.out.println("note");
        ArrayList<UserDTO> requests; 
        ObservableList<UserDTO> returned= FXCollections.observableArrayList();
        try {
            requests=ref.getFriendRequests(revevier);
            requestsListView.setCellFactory(new RequestsNotificationFactory(this));
            returned.setAll(requests);
            requestsListView.setItems(returned);
            
            
        } catch (RemoteException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public void changeStatus(){
        try {
            
           
            Client.ref.changeStatus(status_box.getValue().toString(), Client.currClientDTO.getId());
        } catch (RemoteException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void logout(){
        try {
            Client.ref.changeStatus("offline", Client.currClientDTO.getId());
            Client.ref.unRegister(Client.currClient);
            Client.currClientDTO=null;
            Parent root1 = FXMLLoader.load(getClass().getResource("Login.fxml"));
                            Scene scene1 = new Scene(root1);
                            LoginMain.parentStage.setScene(scene1);
        } catch (RemoteException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContactListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     @FXML
    private void mini(MouseEvent e) {

        Stage stage = (Stage) ((Node) (e.getSource())).getScene().getWindow();
        stage.setIconified(true);

    }
        @FXML
    private void cancel(MouseEvent e) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                ((Node) (e.getSource())).getScene().getWindow().hide();
                
                Client.ref.changeStatus("offline", Client.currClientDTO.getId());
                Client.ref.unRegister(Client.currClient);
                Client.currClientDTO=null;
                
            } catch (Exception ex) {
                 ex.printStackTrace();
            }
        } else {}
    }
}
