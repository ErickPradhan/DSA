/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.*;
import java.util.*;
import Model.ExpenseModel;

public class ExpenseRepository 
{
    private static final String FILE_NAME = "Expenses.csv";

    // Main shared expense list
    public static ArrayList<ExpenseModel> expenses = new ArrayList<>();


    // ---------------- LOAD DATA ----------------
    public static void load() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            Scanner sc = new Scanner(file);
            expenses.clear();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",");

                if (data.length == 7) {
                    ExpenseModel e = new ExpenseModel(
                        Integer.parseInt(data[0].trim()),
                        data[1].trim(),
                        data[2].trim(),
                        data[3].trim(),
                        Double.parseDouble(data[4].trim()),
                        data[5].trim(),
                        data[6].trim()
                    );
                    expenses.add(e);
                }
            }

            sc.close();

        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
        }
    }


    // ---------------- SAVE DATA ----------------
    public static void save() {
        try {
            PrintWriter pw = new PrintWriter(FILE_NAME);

            for (ExpenseModel e : expenses) {
                pw.println(
                        e.getId() + "," +
                        e.getName() + "," +
                        e.getExpenseTitle() + "," +
                        e.getCategory() + "," +
                        e.getAmount() + "," +
                        e.getContact() + "," +
                        e.getDate()
                );
            }

            pw.close();

        } catch (Exception e) {
            System.out.println("Error saving CSV: " + e.getMessage());
        }
    }
}
