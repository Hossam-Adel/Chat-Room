package client;

import Server.ChatServerInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import Server.UserDTO;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


public class RegisterController implements Initializable {
    ChatServerInt server = Client.ref;
    UserDTO  user;
    byte[] image;
    @FXML
    private AnchorPane register_pane;
    @FXML
    private Label empty;
    @FXML
    private ComboBox box;
    @FXML
    private Label invalid_email;
    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;
    @FXML
    private TextField email;
    @FXML
    private ComboBox country;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirm_password;
    @FXML
    private Label passwords_match;
    @FXML
    private Button register;
    @FXML
    private Button cancel;
    @FXML
    private ImageView view;
    @FXML
    private ImageView insert;
    @FXML
    private void handleButtonAction(ActionEvent event) throws FileNotFoundException, IOException {
        invalid_email.setVisible(false);
        passwords_match.setVisible(false);
        empty.setVisible(false);
        if (password.getText().equals(confirm_password.getText()) && validate(email.getText()) && notNull()) {
            user = new UserDTO(first_name.getText(), last_name.getText(), email.getText(),
                    country.getValue().toString(), "offline", box.getValue().toString(), password.getText(), "u",image);
            if(user.getImg()!=null){
                server.signup(user);
                Parent root1 = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Scene scene1 = new Scene(root1);
                LoginMain.parentStage.setScene(scene1);
                
            }
            if(user.getImg()==null){
                empty.setText("please attach an image");
                empty.setVisible(true);
            }

        } else if (!validate(email.getText())) {
            invalid_email.setVisible(true);
            if (!password.getText().equals(confirm_password.getText())) {
                passwords_match.setVisible(true);
            }
        } else if (!password.getText().equals(confirm_password.getText())) {
            passwords_match.setVisible(true);
        } else {
            empty.setText("passwords don't match");
            empty.setVisible(true);
        } 
    }

    public void cancelAction(ActionEvent e) {
        try {
            Parent root1 = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene1 = new Scene(root1);
            LoginMain.parentStage.setScene(scene1);
        } catch (IOException ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void boxAction(ActionEvent event) throws FileNotFoundException {
//        box.    
    }

    boolean validate(String email) {
        boolean check = email.matches("[Aa-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$");
        return check;
    }

    boolean notNull() {
        return (!"".equals(first_name.getText()) || !"".equals(last_name.getText()) || !"".equals(email.getText()) || !"".equals(country.getValue()) || !"".equals(password.getText()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            File file= new File("icons8-add-new-50.png");
            insert.setImage(new Image(file.toURI().toURL().toString(), 100, 100, true, true));
            
            box.getItems().addAll("male","female");
            country.getItems().addAll("Australia", "Canada", "China", "Egypt", "Finland",
             "Germany", "Ireland", "Japan", "New Zealand", "Norway" , "Palestine", 
             "South Korea", "Spain", "United Kingdom");
        } catch (MalformedURLException ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void insertImage(){
        try {
            FileChooser choose=new FileChooser() ;
            File opened =choose.showOpenDialog(LoginMain.parentStage.getScene().getWindow());
            FileInputStream input;
            if(opened!=null){
                input= new FileInputStream(opened);
                image=new byte[(int)opened.length()];
                input.read(image);
                view.setImage(new Image(opened.toURI().toURL().toString(), 100, 100, true, true));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
