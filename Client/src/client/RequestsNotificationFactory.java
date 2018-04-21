package client;

import Server.UserDTO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javax.imageio.ImageIO;

/**
 *
 * @author Ahmed Ali
 */
public class RequestsNotificationFactory implements Callback<ListView<UserDTO>, ListCell<UserDTO>> {
     ContactListController obj;

    public RequestsNotificationFactory  (ContactListController c) {
        obj = c;
    }
    ListView<HBox> list;
    HBox hBox;
    ImageView imageV;
//    Image userImage;

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
                        clientHBox.setSpacing(10.0);
                        Label nameLbl = new Label(user.getFirstname() + " " + user.getLastname());
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(user.getImg()));
                        Image userImage = SwingFXUtils.toFXImage(img, null);
                        imageV.setImage(userImage);
                        imageV.setFitHeight(28);
                        imageV.setFitWidth(28);
                        Button accept = new Button("accept");
                        Button ignore = new Button("Ignore");
                        clientHBox.getChildren().addAll(imageV, nameLbl,accept,ignore);
                        accept.setAlignment(Pos.CENTER_RIGHT);
                        ignore.setAlignment(Pos.BASELINE_RIGHT);
                        if(Client.ref.checkNotificationSeen(Client.currClientDTO, user)){
                            System.out.println("done");
                            new ClientImpl().receiveNotification(user.getFirstname()+" "+user.getLastname()+" wants to add you to his contact list",false);
                            Client.ref.setSeen(Client.currClientDTO, user);
                        }
                        accept.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                try {
                                    Client.ref.acceptRequest(user,Client.currClientDTO);
                                    Client.ref.refreshContactList(user);
                                    Client.ref.refreshSearch(user);
                                    obj.onNotificationsOpen(Client.currClientDTO);
                                    obj.onContatctListOpen();
                                   
                                    
                                } catch (RemoteException ex) {
                                    Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                               
                            }
                        });
                        ignore.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {                                
                                try {
                                    Client.ref.rejectRequest(Client.currClientDTO, user);
                                    Client.ref.refreshSearch(user);
                                    obj.onNotificationsOpen(Client.currClientDTO);
                                    obj.onContatctListOpen();
                                    
                                } catch (RemoteException ex) {
                                    Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                               
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
