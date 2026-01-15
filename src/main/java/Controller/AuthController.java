/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.User;
import Model.CurrentUser;
import java.io.*;
import java.util.ArrayList;


public class AuthController {

    private static final String FILE_NAME = "users.dat";
    private static ArrayList<User> users = new ArrayList<>();

    // LOAD USERS WHEN APP STARTS
    static {
        loadUsers();
    }

   // ================= LOGIN =================
    public static String login(String username, String password, String role) {

        if (role.equalsIgnoreCase("admin")) {

            for (User u : users) {
                if (u.getUsername().equalsIgnoreCase(username)) {

                    if (!u.getRole().equalsIgnoreCase("Admin")) {
                        return "WRONG_USERNAME";
                    }

                    if (!u.getPassword().equals(password)) {
                        return "WRONG_PASSWORD";
                    }

                    return "SUCCESS";
                }
            }

            // fallback admin
            if (username.equals("admin") && password.equals("admin123")) {
                return "SUCCESS";
            }

            return "WRONG_USERNAME";
        }

        if (role.equalsIgnoreCase("user")) {

            for (User u : users) {
                if (u.getUsername().equalsIgnoreCase(username)) {

                    if (!u.getRole().equalsIgnoreCase("User")) {
                        return "WRONG_USERNAME";
                    }

                    if (!u.getPassword().equals(password)) {
                        return "WRONG_PASSWORD";
                    }

                    return "SUCCESS";
                }
            }

            return "WRONG_USERNAME";
        }

        return "BOTH_WRONG";
    }



    // ================= REGISTER =================
    public static void register(User user) 
    {
        users.add(user);
        saveUsers();
    }

    // ================= USERNAME CHECK =================
    public static boolean usernameExists(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    // ================= SAVE =================
    public static void saveUsers() {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(users);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD =================
    @SuppressWarnings("unchecked")
    private static void loadUsers() {

        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            users = (ArrayList<User>) in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static User getUserByUsername(String username) 
    {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

}
