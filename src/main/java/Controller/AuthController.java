/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import Model.User;


/**
 *
 * @author erick
 */
public class AuthController 
{
    // Hardcoded admin (allowed for coursework)
    private static final User ADMIN =
            new User("admin", "admin123", "admin");
    
    // Hardcoded user
    private static final User USER =
            new User("user", "user123", "user");

    public static boolean login(String username, String password, String role) 
    {

        if (role.equals("admin")) 
        {
            return username.equals(ADMIN.getUsername())
                    && password.equals(ADMIN.getPassword());
        }

        // Simple user login logic
        if (role.equals("user")) 
        {
            return username.equals(USER.getUsername())
                    && password.equals(USER.getPassword());
        }

        return false;
    }
}
