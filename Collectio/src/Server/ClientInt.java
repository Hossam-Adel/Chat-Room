package Server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.rmi.*;
import java.util.Vector;
import javafx.fxml.Initializable;
public interface ClientInt extends Remote
{
        
	public void receive(Message msg, UserDTO name)throws RemoteException;
        public void receive(String fileName, UserDTO sender, RemoteInputStream data, String extension, long timesOfSending, long packetNum) throws RemoteException;
        public void setIdd(int i)throws RemoteException;
        public int getIdd()throws RemoteException;
        public void setName(String s) throws RemoteException;
        public  String getName() throws RemoteException;
        public void addChatController(Initializable chatCntrl) throws RemoteException ;
        public  boolean checkContactChat(int friendID)throws RemoteException ;
        public void removeFromChatControllers(Initializable chatCntrl)throws RemoteException;
        public void receiveBroadcast(String msg)throws RemoteException;
        public void receiveNotification(String msg,boolean reject)throws RemoteException;
        public Initializable getContactListCntrl()throws RemoteException;
        public void setContactListCntrl(Initializable contactListCntrl)throws RemoteException;
        void refreshContatcList()throws RemoteException;
        void refreshSearch()throws RemoteException;
}