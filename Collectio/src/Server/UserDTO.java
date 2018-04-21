package Server;

import java.io.File;
import java.io.Serializable;

public class UserDTO implements Serializable{
    int id; String firstname,lastname,email,country,status,gender,password, type;byte[] img;int[] contacts;

    public int[] getContacts() {
        return contacts;
    }

    public void setContacts(int[] contacts) {
        this.contacts = contacts;
    }
    public UserDTO(){}
    public UserDTO(String firstname,String lastname , String email,String country, String status,String gender,String pass , String type,byte[] img){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email= email;
        this.country = country;
        this.status= status;
        this.gender = gender;
        password = pass;
        this.type = type;
        this.img = img;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
