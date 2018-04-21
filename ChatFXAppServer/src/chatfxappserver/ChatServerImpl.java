package chatfxappserver;

import Server.ChatServerInt;
import Server.ClientInt;
import Server.Message;
import Server.UserDTO;
import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.FileNotFoundException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.xml.bind.JAXBException;
import javafx.beans.property.SimpleIntegerProperty;
import model.Operations;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServerInt {

    Vector<ClientInt> clientsVector = new Vector<>();
    private SimpleIntegerProperty usersCount = new SimpleIntegerProperty(this, "usersCount", 0);

    public ChatServerImpl() throws RemoteException {
    }

    @Override
    public void register(ClientInt clientRef) throws RemoteException {
        boolean exist = false;
        for(ClientInt clientIntReference : clientsVector) {
            if(clientIntReference.getIdd() == clientRef.getIdd()){
                exist = true;
                break;
            }
        }
        if(!exist) {
        clientsVector.add(clientRef);
        System.out.println("Client added");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                usersCount.set(clientsVector.size());
                System.out.println(usersCount.getValue());
            }
        });
        }
    }
        
        @Override
        public void signup(UserDTO user)throws RemoteException{
            try {
                new Operations().register(user);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ChatServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        

    public void unRegister(ClientInt clientRef) throws RemoteException {
        clientsVector.remove(clientRef);
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
                usersCount.set(clientsVector.size());
                System.out.println(usersCount.getValue());
            }
        });
        System.out.println("Client removed");
    }

    @Override
    public UserDTO login(UserDTO logger) throws RemoteException {
        UserDTO loged = new Operations().login(logger);
        return loged;
    }

    @Override
    public ArrayList<UserDTO> getContactList(UserDTO user) throws RemoteException {
        ArrayList<UserDTO> friends = new Operations().getContactList(user);
        return friends;

    }

    @Override
    public ArrayList<UserDTO> getFriendRequests(UserDTO curruser) throws RemoteException {
        ArrayList<UserDTO> friendrequests = new Operations().getFriendRequests(curruser);
        return friendrequests;
    }

     @Override
    public ArrayList<UserDTO> searchClients(String email)throws RemoteException{
        ArrayList<UserDTO> available= new Operations().searchForContacts(email);
        if(available.size()!=0){
            System.out.println("search size ="+available.size() );
            return available; 
        }
       return null;
    }
    @Override
    public void sendRequest(UserDTO sender , UserDTO receiver)throws RemoteException{
        try {
            new Operations().sendRequest(sender, receiver);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void notify(UserDTO sender,UserDTO receiver,boolean reject) throws RemoteException {
           for(ClientInt rec:clientsVector){
               if(rec.getIdd()==receiver.getId()){
                   rec.receiveNotification(sender.getFirstname()+" "+sender.getLastname()+" wants to add you to his contact list",reject);
               }
           }
    }
    @Override
    public boolean checkRequest(UserDTO sender,UserDTO receiver)throws RemoteException{
        boolean yes = new Operations().requestExists(sender.getId(), receiver.getId());
        return yes;
    }
    @Override
    public void acceptRequest(UserDTO sender, UserDTO receiver) throws RemoteException {
        try {
            new Operations().acceptRequest(sender, receiver);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public boolean areFriends(UserDTO client, UserDTO friend)throws RemoteException{
        boolean yes = new Operations().areFriends(client.getId(), friend.getId());
        System.out.println(yes+"are friends");
        return yes;
    }
    @Override
    public void unfriend(UserDTO client,UserDTO friend)throws RemoteException{
        try {
            new Operations().removeContact(friend, client);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void rejectRequest(UserDTO receiver,UserDTO sender)throws RemoteException{
        new Operations().reject(receiver.getId(), sender.getId());
    }
    @Override
    public void changeStatus(String status, int id)throws RemoteException{
        new Operations().changeStatus(status, id);
    }

    public SimpleIntegerProperty getClientsVectorSize() {    //private SimpleIntegerProperty value = new SimpleIntegerProperty(this, "value");
        return usersCount;
    }

    @Override
    public void broadcast(String msg) throws RemoteException {
        for (ClientInt clientRef : clientsVector) {
            try {
				clientRef.receiveBroadcast(msg);

            } catch (RemoteException ex) {
                System.out.println("Can not send notification to client");
                ex.printStackTrace();

            }

        }
    }

    @Override
    public void tellUser(Message msg, UserDTO sender, int receiverId) throws RemoteException {
        System.out.println("Message received: " + msg.getMsg());
        for (ClientInt clientRef : clientsVector) {
            System.out.println(clientRef.getIdd());
            if (clientRef.getIdd() == receiverId) {
                try {
                    clientRef.receive(msg, sender);
                } catch (RemoteException ex) {
                    System.out.println("Can not send message to client");
                    ex.printStackTrace();
                }
            }
        }
       SaveChat obj=new SaveChat(receiverId , msg , sender.getId());
        try {
            obj.write();
        } catch (JAXBException ex) {
            System.err.println("can't save in xml");
        }
    }

    @Override
    public void tellUser(String fileName, UserDTO sender, int receiverId, RemoteInputStream data, String extension, long timesOfSending, long packetNum) throws RemoteException {
        for (ClientInt clientRef : clientsVector) {
            if (clientRef.getIdd() == receiverId) {
                try {
                    clientRef.receive(fileName, sender, data, extension, timesOfSending, packetNum);
                } catch (RemoteException ex) {
                    System.out.println("Can not send File to client");
                    ex.printStackTrace();
                }
            }
        }
    
    
    }
    @Override
    public boolean checkNotificationSeen(UserDTO receiver , UserDTO sender)throws RemoteException{
        boolean seen = new Operations().isSeen(receiver, sender);
        return seen;
    }
    @Override
    public void setSeen(UserDTO receiver , UserDTO sender)throws RemoteException{
        new Operations().setSeen(receiver,sender);
    }

    @Override
    public void refreshContactList(UserDTO receiver) throws RemoteException {
        for(ClientInt rec:clientsVector){
               if(rec.getIdd()==receiver.getId()){
                   rec.refreshContatcList();
               }
           }
    }
    @Override
    public void refreshSearch(UserDTO receiver) throws RemoteException {
        for(ClientInt rec:clientsVector){
               if(rec.getIdd()==receiver.getId()){
                   rec.refreshSearch();
               }
           }
    }
    
}
