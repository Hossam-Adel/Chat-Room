package chatfxappserver;

import Server.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Operations;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private AnchorPane login_pane;
    @FXML
    private TextField login_email;
    @FXML
    private PasswordField login_password;
    @FXML
    private Button sigin;
    @FXML
    private Button signup;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView closeIcon;

    private double xPosition = 0;
    private double yPosition = 0;

    public void signupAction(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        Scene scene = new Scene(root);
        ChatFXAppServer.parentStage.setScene(scene);

    }

    public void loginAction(ActionEvent e) throws IOException {
        Server.UserDTO user = new Server.UserDTO();
        user.setEmail(login_email.getText());
        user.setPassword(login_password.getText());
        Object obj = new Operations().login(user);
        if (obj instanceof UserDTO ) {
            
            ChatFXAppServer.admin = (UserDTO)obj;
            if(ChatFXAppServer.admin.getType().equals("a")){
                
                Parent root = FXMLLoader.load(getClass().getResource("ServerFXML.fxml"));
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            root.setOnMousePressed((MouseEvent event) -> {
                xPosition = ChatFXAppServer.parentStage.getX() - event.getScreenX();
                yPosition = ChatFXAppServer.parentStage.getY() - event.getScreenY();
            });
            root.setOnMouseDragged((MouseEvent event) -> {
                ChatFXAppServer.parentStage.setX(event.getScreenX() + xPosition);
                ChatFXAppServer.parentStage.setY(event.getScreenY() + yPosition);
            });
            ChatFXAppServer.parentStage.setScene((new Scene(root)));
//                 Scene scene = new Scene(root);
//                 ChatFXAppServer.parentStage.setScene(scene);
            }
            

        
        }   
        else{
            label.setText("invalid info");
        }
    }
    public void closeWindow() {
        Platform.exit();
    }

    public void minimize() {
        ((Stage) minimizeIcon.getScene().getWindow()).setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
