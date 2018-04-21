package Server;
import java.rmi.*;
import java.util.ArrayList;
import com.healthmarketscience.rmiio.*;

public interface ChatServerInt extends Remote
{
        
	void broadcast(String msg)throws RemoteException;
	void register(ClientInt cliebntRef)throws RemoteException;
	void unRegister(ClientInt cliebntRef)throws RemoteException;
        UserDTO login(UserDTO logger)throws RemoteException;
        ArrayList<UserDTO> getContactList(UserDTO user)throws RemoteException;
        public void tellUser(Message msg, UserDTO sender, int receiverId) throws RemoteException;
        public void tellUser(String fileName, UserDTO sender, int receiverId, RemoteInputStream data, String extension, long timesOfSending, long packetNum) throws RemoteException;
        void signup(UserDTO user)throws RemoteException;
        ArrayList<UserDTO> getFriendRequests(UserDTO curruser)throws RemoteException;
         ArrayList<UserDTO> searchClients(String email)throws RemoteException;
        void sendRequest(UserDTO sender , UserDTO receiver)throws RemoteException;
        void notify(UserDTO sender,UserDTO receiver,boolean reject)throws RemoteException;
        boolean checkRequest(UserDTO sender,UserDTO receiver)throws RemoteException;
        void acceptRequest(UserDTO sender, UserDTO receiver) throws RemoteException;
        boolean areFriends(UserDTO client, UserDTO friend)throws RemoteException;
        void rejectRequest(UserDTO receiver,UserDTO sender)throws RemoteException;
        void unfriend(UserDTO client,UserDTO friend)throws RemoteException;
        void changeStatus(String status, int id)throws RemoteException;
        boolean checkNotificationSeen(UserDTO receiver , UserDTO sender)throws RemoteException;
        void setSeen(UserDTO receiver , UserDTO sender)throws RemoteException;
        void refreshContactList(UserDTO receiver)throws RemoteException;
        void refreshSearch(UserDTO receiver)throws RemoteException;
        
}  