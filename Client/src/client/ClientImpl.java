package client;

import Server.ClientInt;
import Server.Message;
import Server.UserDTO;
import com.healthmarketscience.rmiio.RemoteInputStream;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;
import javafx.application.Platform;
import javafx.fxml.Initializable;

public class ClientImpl extends UnicastRemoteObject implements ClientInt {

    private int id, foundFlag = 0;
    private String sender;
    private Vector<FXMLDocumentController> chatCntrls;
    ContactListController contactListCntrl ;

    @Override
    public ContactListController getContactListCntrl()throws RemoteException {
        return contactListCntrl;
    }

   

   

    public ClientImpl() throws RemoteException {
        id = Client.currClientDTO.getId();
        sender = Client.currClientDTO.getFirstname();
        chatCntrls = new Vector<>();
    }

    @Override
    public void setIdd(int i) throws RemoteException {
        this.id = i;
    }

    @Override
    public int getIdd() throws RemoteException {
        return this.id;
    }

    @Override
    public void setName(String s) throws RemoteException {
        this.sender = s;
    }

    @Override
    public String getName() throws RemoteException {
        return this.sender;
    }

    @Override
    public void addChatController(Initializable chatCntrl) throws RemoteException {
        this.chatCntrls.add((FXMLDocumentController) chatCntrl);
        for (FXMLDocumentController controller : chatCntrls) {
                System.out.println("added vector : "+controller.getStageId());
        }
    }

    @Override
    public  boolean checkContactChat(int friendID)throws RemoteException {
        for (FXMLDocumentController controller : this.chatCntrls) {
            if (friendID == controller.getStageId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void receive(Message msg, UserDTO sender) throws RemoteException {
        System.out.println("Received");
        foundFlag = 0;
        System.out.println("sender = "+ sender.getId());
        for (FXMLDocumentController controller : this.chatCntrls) {
                System.out.println("whooooole vector : "+controller.getStageId());
        }
        for (FXMLDocumentController controller : this.chatCntrls) {
            if (sender.getId() == controller.getStageId()) {
                System.out.println("found inside vector");
                foundFlag = 1;
                controller.display(msg, sender);
            }
        }
        if (foundFlag == 0) {
            System.out.println(" outside vector");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ChatWindow chatWin = new ChatWindow(sender);
                    System.out.println("call display first time");
                    chatWin.getChatController().display(msg, sender);
                }
            });
        }
    }

    @Override
    public void receive(String fileName, UserDTO sender, RemoteInputStream data, String extension, long timesOfSending, long packetNum) throws RemoteException {
        for (FXMLDocumentController controller : chatCntrls) {
            System.out.println("File Received2");
            if (sender.getId() == controller.getStageId()) {
                foundFlag = 1;
                controller.display(fileName, sender, data, extension, timesOfSending, packetNum);
            }
        }
        if (foundFlag == 0) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ChatWindow chatWin = new ChatWindow(sender);
                    chatWin.getChatController().display(fileName, sender, data, extension, timesOfSending, packetNum);
                }
            });
        }
    }

    @Override
    public void removeFromChatControllers(Initializable chatCntrl) throws RemoteException{
        this.chatCntrls.remove(chatCntrl);
    }
    
    @Override
    public void receiveBroadcast(String msg) throws RemoteException {
       System.out.println(msg);
       new NotificationPopUp().showPopupMessage(msg, LoginMain.parentStage);
    }
    @Override
    public void receiveNotification(String msg,boolean reject) throws RemoteException {
        if(reject){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                        System.out.println(Client.currClientDTO.getFirstname());

                        contactListCntrl.onNotificationsOpen(Client.currClientDTO);

                }
            });
        }else{
            new NotificationPopUp().showPopupMessage(msg, LoginMain.parentStage);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                        System.out.println(Client.currClientDTO.getFirstname());

                        contactListCntrl.onNotificationsOpen(Client.currClientDTO);

                }
            });
        }
       
       
       
    }

    @Override
    public void setContactListCntrl(Initializable contactListCntrl) throws RemoteException {
        this.contactListCntrl = (ContactListController) contactListCntrl;
    }

    @Override
    public void refreshContatcList() throws RemoteException {
        Platform.runLater(new Runnable() {
           @Override
           public void run() {
               
                   System.out.println(Client.currClientDTO.getFirstname());
                   
                   contactListCntrl.onContatctListOpen();
               
           }
       });
    }

    @Override
    public void refreshSearch() throws RemoteException {
        Platform.runLater(new Runnable() {
           @Override
           public void run() {
               
                   System.out.println(Client.currClientDTO.getFirstname());
                   
                   contactListCntrl.searchAction();
               
           }
       });
    }

    
    
    
}
