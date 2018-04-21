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
 * @author Hossam Adel
 */
public class SearchListFactory implements Callback<ListView<UserDTO>, ListCell<UserDTO>> {

    ContactListController obj;

    public SearchListFactory(ContactListController c) {
        obj = c;
    }
    ListView<HBox> list;
    
//    Image userImage;

    @Override
    public ListCell<UserDTO> call(ListView<UserDTO> param) {
        //System.out.println("Hello"+param.getItems().get(0));
        ImageView imageV = new ImageView();
        return new ListCell<UserDTO>() {
            @Override
            protected void updateItem(UserDTO user, boolean empty) {
                super.updateItem(user, empty);
                if (user != null || !empty) {
                    try {
                        if(user.getId()!= Client.currClientDTO.getId()){
                            HBox clientHBox = new HBox();
                        VBox info = new VBox();
                        Label nameLbl = new Label(user.getFirstname() + " " + user.getLastname());
                        Label email = new Label(user.getEmail());
                        info.getChildren().addAll(nameLbl,email);
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(user.getImg()));
                        Image userImage = SwingFXUtils.toFXImage(img, null);
                        imageV.setImage(userImage);
                        imageV.setFitHeight(28);
                        imageV.setFitWidth(28);
                        clientHBox.setSpacing(10.0);
                        Button add = new Button("add");
                        Button accept = new Button("accept");
                        Button reject = new Button("reject");
                        Button cancel = new Button("canel request");
                        Button unfriend = new Button("unfriend");
                       clientHBox.getChildren().addAll(imageV, info);
                        if(Client.ref.checkRequest(Client.currClientDTO, user)){
                            clientHBox.getChildren().add(cancel);
                        }else if(Client.ref.checkRequest(user,Client.currClientDTO)){
                            clientHBox.getChildren().add(accept);
                            clientHBox.getChildren().add(reject);
                        }else if(Client.ref.areFriends(Client.currClientDTO, user)){
                            clientHBox.getChildren().add(unfriend);
                        }
                        else{
                            clientHBox.getChildren().add(add);
                        }
                        cancel.setOnMousePressed((MouseEvent event) -> {
                            try {
                               
                                Client.ref.rejectRequest(user, Client.currClientDTO);
                                Client.ref.notify(Client.currClientDTO,user,true);
                                clientHBox.getChildren().remove(cancel);
                                clientHBox.getChildren().add(add);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                            System.out.println(Client.currClientDTO.getFirstname());

                                            obj.onContatctListOpen();
                                            obj.searchAction();

                                    }
                                });
                            } catch (RemoteException ex) {
                                Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        
                        reject.setOnMousePressed((MouseEvent event) -> {
                            try {
                                
                                Client.ref.rejectRequest(Client.currClientDTO, user);
                                
                                clientHBox.getChildren().remove(reject);
                                clientHBox.getChildren().add(add);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                            System.out.println(Client.currClientDTO.getFirstname());
                                            
                                            obj.onContatctListOpen();
                                            obj.searchAction();

                                    }
                                });
                            } catch (RemoteException ex) {
                                Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        
                        unfriend.setOnMousePressed((MouseEvent event) -> {
                            try {
                               
                                Client.ref.unfriend(Client.currClientDTO, user);
                                Client.ref.refreshContactList(user);
                                clientHBox.getChildren().remove(unfriend);
                                clientHBox.getChildren().add(add);
                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                            System.out.println(Client.currClientDTO.getFirstname());

                                            obj.onContatctListOpen();
                                            obj.searchAction();

                                    }
                                });
                            } catch (RemoteException ex) {
                                Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                         
                        add.setOnMousePressed((MouseEvent event) -> {
                            try {
                                
                                Client.ref.sendRequest(Client.currClientDTO, user);
                                Client.ref.notify(Client.currClientDTO, user,false);
                                clientHBox.getChildren().remove(add);
                                clientHBox.getChildren().add(cancel);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                            System.out.println(Client.currClientDTO.getFirstname());

                                            obj.onContatctListOpen();
                                            obj.searchAction();

                                    }
                                });
                                
                            } catch (RemoteException ex) {
                                Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        accept.setOnMousePressed((MouseEvent event) -> {
                            try {
                                Client.ref.acceptRequest(user,Client.currClientDTO);
                                clientHBox.getChildren().remove(accept);
                                clientHBox.getChildren().add(add);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                            System.out.println(Client.currClientDTO.getFirstname());
                                            
                                            obj.onContatctListOpen();
                                            obj.searchAction();

                                    }
                                });
                            } catch (RemoteException ex) {
                                Logger.getLogger(SearchListFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        setGraphic(clientHBox);
                        }
                        
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
