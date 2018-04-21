package model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Server.UserDTO;
import java.io.ByteArrayInputStream;
import java.sql.Blob;
import javafx.collections.FXCollections;
import java.sql.Statement;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class Operations {
    
    
    public void register(UserDTO user) throws FileNotFoundException {
        try {
            Connection conn = new Connector().connect();
            String sql = "insert into client(first_name,last_name,email,gender,password,country,type,status,image) values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            byte[] img_file = user.getImg();
            ps.setString(1, user.getFirstname());
            ps.setString(2, user.getLastname());
            ps.setString(3, user.getEmail().toLowerCase());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getCountry());
            ps.setString(7, user.getType());
            ps.setString(8, user.getStatus());
            InputStream in = new ByteArrayInputStream(img_file);
            ps.setBinaryStream(9,in);
            ps.executeUpdate();
            ps.close();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void sendRequest(UserDTO sender, UserDTO receiver) throws FileNotFoundException {
        try {
            Connection conn = new Connector().connect();
            String sql = "insert into requests values (?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Integer.toString(sender.getId()));
            ps.setString(2, Integer.toString(receiver.getId()));
            ps.setInt(3, 0);
            ps.executeUpdate();
            ps.close();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void acceptRequest(UserDTO sender, UserDTO receiver) throws FileNotFoundException {
        try {
            Connection conn = new Connector().connect();
            String sql = "insert into friends values (?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Integer.toString(sender.getId()));
            ps.setString(2, Integer.toString(receiver.getId()));
            ps.addBatch();
            ps.setString(2, Integer.toString(sender.getId()));
            ps.setString(1, Integer.toString(receiver.getId()));
            ps.addBatch();
            ps.executeBatch();
            ps=conn.prepareStatement("delete from requests where sender=? and receiver=?");
            ps.setString(1, Integer.toString(sender.getId()));
            ps.setString(2, Integer.toString(receiver.getId()));
            ps.executeUpdate();
            ps.close();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removeContact(UserDTO sender, UserDTO receiver) throws FileNotFoundException {

        String sql = "delete from friends where client=? and friend=?";
        try {
            Connection conn = new Connector().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Integer.toString(sender.getId()));
            ps.setString(2, Integer.toString(receiver.getId()));
            ps.addBatch();
            ps.setString(2, Integer.toString(sender.getId()));
            ps.setString(1, Integer.toString(receiver.getId()));
            ps.addBatch();
            ps.executeBatch();
            ps.close();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public UserDTO login(UserDTO user) {
        Connection conn = new Connector().connect();
        boolean user_found;
        boolean password_incorect;
        String result = "";
        String sql = "select * from client where email=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getEmail().toLowerCase());
            ResultSet rs = ps.executeQuery();
            UserDTO comparison_user = new UserDTO();
            while (rs.next()) {
                
                comparison_user.setId(rs.getInt("id"));
                comparison_user.setFirstname(rs.getString("first_name"));
                comparison_user.setLastname(rs.getString("last_name"));
                comparison_user.setEmail(rs.getString("email"));
                comparison_user.setGender(rs.getString("gender"));
                comparison_user.setPassword(rs.getString("password"));
                comparison_user.setCountry(rs.getString("country"));
                comparison_user.setType(rs.getString("type"));
                comparison_user.setStatus(rs.getString("status"));
                Blob blob = rs.getBlob("image");
                InputStream in = blob.getBinaryStream();
                int blobLength = (int) blob.length();  
                byte[] buff = blob.getBytes(1, blobLength);
                int len = 0;
                comparison_user.setImg(buff);
            }
            if (comparison_user.getPassword() != null) {
                if (comparison_user.getPassword().equals(user.getPassword())) {
                    ps.close();
                    conn.close();
                    return comparison_user;
                } else {
                    result = "invalidpassword";
                    System.out.println("not valid");
                    ps.close();
                    conn.close();
                    return null;
                }
            } else {
                result = "emailnotfound";
                System.out.println("exception");
                ps.close();
                conn.close();
                return null;
            }
            
        } catch (SQLException ex) {
            //handle login with non valid login info
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            result = "databaseconnecterror";
            System.out.println("exception");
            return null;

        }

    }

    public ArrayList<UserDTO> getContactList(UserDTO user) {
        Connection conn = new Connector().connect();
        String sql = "select * from friends where client =?";
        PreparedStatement ps;
        ArrayList<UserDTO> contacts = new ArrayList();
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            int i=0;
            while (rs.next()) {
                UserDTO contact = getUserbyId(rs.getInt("friend"));
                contacts.add(contact);
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contacts;
    }

    public UserDTO getUserbyId(int id) {
        UserDTO contact = new UserDTO();
        Connection conn = new Connector().connect();
        try {
            String sql = "select * from client where id=?";
            PreparedStatement ps;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                contact.setId(rs.getInt("id"));
                contact.setFirstname(rs.getString("first_name"));
                contact.setLastname(rs.getString("last_name"));
                contact.setStatus(rs.getString("status"));
                contact.setCountry(rs.getString("country"));
                Blob blob = rs.getBlob("image");
                InputStream in = blob.getBinaryStream();
                int blobLength = (int) blob.length();  
                byte[] buff = blob.getBytes(1, blobLength);
                int len = 0;
                contact.setImg(buff);
                contact.setEmail(rs.getString("email"));
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return contact;
    }
    
    public ArrayList<UserDTO> getFriendRequests(UserDTO user) {
        Connection conn = new Connector().connect();
        String sql = "SELECT * FROM requests WHERE receiver = ?";
        PreparedStatement ps;
        ArrayList<UserDTO> friendRequests = new ArrayList();
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            int i=0;
            while (rs.next()) {
                UserDTO friend = getUserbyId(rs.getInt("sender"));
                friendRequests.add(friend);
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friendRequests;
    }
    
//    public void changeAllStatusToOffline(){
//        String sql = "UPDATE * SET status='offline'";
//    }
    
    public int getNumberOfUsers() {
        int numberOfUsers = 0;
        Connection conn = new Connector().connect();
        try {
            String sql = "select * from client";
            PreparedStatement ps;
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                numberOfUsers++;
            }
            ps.close();
            conn.close();
            return numberOfUsers;
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public ObservableList getContriesStatistics() {
        String[] countries = {"Australia", "Canada", "China", "Egypt", "Finland",
             "Germany", "Ireland", "Japan", "New Zealand", "Norway" , "Palestine", 
             "South Korea", "Spain", "United Kingdom"};
        ObservableList data = FXCollections.observableArrayList();
        Connection conn = new Connector().connect();
        try {
        String sql = "select COUNT(*) from client where country = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        
            for(int i=0; i<countries.length; i++){
                ps.setString(1, countries[i]);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){ 
                    if(rs.getInt("count(*)")>0)
                    data.add(new PieChart.Data(countries[i], rs.getInt("count(*)")));
                }
            }
                ps.close();
                conn.close();
            }catch(Exception e){
                e.printStackTrace();
            }  
            return data;
        
    }
    public ObservableList getGenderStatistics() {
        int numberOfMales = 0;
        int numberOfFemales = 0;
        int total = 0;
        double malePercent = 0, femalePercent = 0;
        ObservableList data = FXCollections.observableArrayList();
        Connection conn = new Connector().connect();
        try {
            String sql = "select * from client";
            PreparedStatement ps;
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total++;
                if (rs.getString("gender").equals("male")) {
                    numberOfMales++;
                } else if(rs.getString("gender").equals("female")) {
                    numberOfFemales++;
                }
            }
            ps.close();
            conn.close();
            if (numberOfMales > 0) {
                malePercent =(double) numberOfMales / total * 100;
            }
            if (numberOfFemales > 0) {
                femalePercent = (double) numberOfFemales / total * 100;
            }
            data.add(new PieChart.Data("Males (" + malePercent + "%)", numberOfMales));
            data.add(new PieChart.Data("Females (" + femalePercent + "%)", numberOfFemales));
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public ArrayList<UserDTO> searchForContacts(String email){
        ArrayList<UserDTO> available_contacts = new ArrayList<UserDTO>();
        Connection conn = new Connector().connect();
        try {
            String sql = "SELECT * FROM client WHERE EMAIL like '%"+email+"%' and type='u' ";
            Statement ps = conn.createStatement();         
            ResultSet rs = ps.executeQuery(sql);
            
            while(rs.next()){
                UserDTO contact = new UserDTO();
                contact.setId(rs.getInt("id"));
                contact.setFirstname(rs.getString("first_name"));
                contact.setLastname(rs.getString("last_name"));
                contact.setCountry(rs.getString("country"));
                contact.setEmail(rs.getString("EMAIL"));
                contact.setStatus(rs.getString("status"));
                Blob blob = rs.getBlob("image");
                InputStream in = blob.getBinaryStream();
                int blobLength = (int) blob.length();  
                byte[] buff = blob.getBytes(1, blobLength);
                contact.setImg(buff);
                available_contacts.add(contact);
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return available_contacts;
    }
    public boolean requestExists(int sender, int receiver){
        Connection conn = new Connector().connect();
        String sql = "SELECT * FROM requests WHERE sender = ? and receiver =?";
        boolean yes=false;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,sender);
            ps.setInt(2,receiver);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                yes = true;
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return yes;
    }
    public boolean areFriends(int user, int friend){
        Connection conn = new Connector().connect();
        String sql = "SELECT * FROM friends WHERE client = ? and friend =?";
        boolean yes=false;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,user);
            ps.setInt(2,friend);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                yes = true;
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return yes;
    }
    public void reject(int user, int friend){
        Connection conn = new Connector().connect();
        if(requestExists(friend,user)){
            try {
                String sql = "delete from requests where sender=? and receiver=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, friend);
                ps.setInt(2, user);
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public void changeStatus(String status,int id){
        try {
            Connection con = new Connector().connect();
            String sql= "update client set status=? where id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void setOfline(){
        
     try {
            Connection con = new Connector().connect();
            String sql= "update client set status=? ";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,"offline");
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public boolean isSeen(UserDTO receiver,UserDTO sender){
        boolean seen=false;int i;
        try {
            
            Connection con = new Connector().connect();
            String sql= "select seen from requests where receiver=? and seen=0 and sender=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,receiver.getId());
            ps.setInt(2,sender.getId());
            ResultSet rs=ps.executeQuery();
//            while(rs.next()){
            rs.next();
                i=rs.getInt("seen");
//            }
            ps.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return seen;
    }
    public void setSeen(UserDTO receiver,UserDTO sender){
        try {
            Connection con = new Connector().connect();
            String sql= "update requests set seen=? where sender=? and receiver=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,1);
            ps.setInt(2,sender.getId());
            ps.setInt(3,receiver.getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
