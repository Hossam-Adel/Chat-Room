/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Server.UserDTO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javax.imageio.ImageIO;

/**
 *
 * @author Ahmed Ali
 */
public class ListItemFactory implements Callback<ListView<UserDTO>, ListCell<UserDTO>> {

    ContactListController obj;

    public ListItemFactory(ContactListController c) {
        obj = c;
    }
    private ListView<HBox> list;
    private HBox hBox;
    private ImageView imageV;
    private Image userImage;
    private ChatWindow chatWin;

    @Override
    public ListCell<UserDTO> call(ListView<UserDTO> param) {
        //System.out.println("Hello"+param.getItems().get(0));
        imageV = new ImageView();
        return new ListCell<UserDTO>() {
            @Override
            protected void updateItem(UserDTO user, boolean empty) {
                super.updateItem(user, empty);
                if (user != null || !empty) {
                    try {
                        HBox clientHBox = new HBox();
                        Label nameLbl = new Label(user.getFirstname() + " " + user.getLastname());
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(user.getImg()));
                        userImage = SwingFXUtils.toFXImage(img, null);
                        imageV.setImage(userImage);
                        imageV.setFitHeight(28);
                        imageV.setFitWidth(28);
                        Label status = new Label(user.getStatus());
                        VBox box = new VBox();
                        box.getChildren().addAll(nameLbl,status);
                        Button unfriend = new Button("unfriend");
                        clientHBox.setSpacing(10.0);
                        clientHBox.getChildren().addAll(imageV,box,unfriend);
                        clientHBox.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                try {
                                    System.out.println("//****///"+Client.currClient.checkContactChat(user.getId()));
                                    if(!Client.currClient.checkContactChat(user.getId())&&!user.getStatus().equals("offline"))
                                        chatWin = new ChatWindow(user);
                                    else if(user.getStatus().equals("offline")){
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Offline");
                                        alert.setHeaderText(null);
                                        alert.setGraphic(null);
                                        alert.setContentText("Sorry, your friend is offline right now!");
                                        alert.showAndWait();
                                    }
                                } catch (RemoteException ex) {
                                    Logger.getLogger(ListItemFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                }
                            
                        });
                        unfriend.setOnMousePressed((MouseEvent event) -> {
                            try {
                                
                                Client.ref.unfriend(Client.currClientDTO, user);
                                Client.ref.refreshContactList(user);
                                
                                Client.ref.refreshSearch(user);
                                obj.onContatctListOpen();
                                
                                
                            } catch (RemoteException ex) {
                                Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        setGraphic(clientHBox);
                    } catch (IOException ex) {
                        Logger.getLogger(ListItemFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    setGraphic(null);
                }
            }
        };
    }

}
