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
    public static String login(String username, String password, String role)
    {
        if(role.equalsIgnoreCase("admin"))
        {
            boolean correctUser = username.equalsIgnoreCase("admin");
            boolean correctPass = password.equals("admin123");

            if(correctUser && correctPass)
                return "SUCCESS";

            if(!correctUser && !correctPass)
                return "BOTH_WRONG";

            if(!correctUser)
                return "WRONG_USERNAME";

            return "WRONG_PASSWORD";
        }

        if(role.equalsIgnoreCase("user"))
        {
            boolean correctUser = username.equalsIgnoreCase("user");
            boolean correctPass = password.equals("user123");

            if(correctUser && correctPass)
                return "SUCCESS";

            if(!correctUser && !correctPass)
                return "BOTH_WRONG";

            if(!correctUser)
                return "WRONG_USERNAME";

            return "WRONG_PASSWORD";
        }

        return "BOTH_WRONG";
    }
}
