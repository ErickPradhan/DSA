/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalDate;
import javax.swing.ImageIcon;
import java.io.Serializable;

public class User implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String role; // admin or user
    
    // NEW FIELDS (for profile panel)
    private String firstName;
    private String lastName;
    private ImageIcon profileImage;
    private LocalDate dateJoined;
    private String note;
    private String email;
    private String contactNumber;
    private String address;
    private String dob;
    
    public User(String username, String password, String role) 
    {
        this.username = username;
        this.password = password;
        this.role = role;
        this.note = "";
        this.dateJoined = LocalDate.now();
    }
    
    // NEW CONSTRUCTOR (for registration)
    public User(String username, String password, String role,
                String firstName, String lastName,
                ImageIcon profileImage, java.time.LocalDate dateJoined) 
    {

        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.note = "";
        this.dateJoined = dateJoined;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getPassword() 
    {
        return password;
    }

    public String getRole() 
    {
        return role;
    }
    
    public String getFirstName() 
    { 
        return firstName; 
    }
    public String getLastName() 
    { 
        return lastName; 
    }
    public ImageIcon getProfileImage() 
    { 
        return profileImage; 
    }
    public LocalDate getDateJoined() 
    { 
        return dateJoined; 
    }
    public String getNote() 
    { 
        return note; 
    }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public String getDob() { return dob; }
    
    // ===== SETTERS =====
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public void setFirstName(String firstName) 
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) 
    {
        this.lastName = lastName;
    }
    
    public void setNote(String note) 
    {
        this.note = note;
    }
    public void setProfileImage(ImageIcon image) 
    { 
        this.profileImage = image; 
    }
    public void setEmail(String email) { this.email = email; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setDob(String dob) { this.dob = dob; }
}
