/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.*;
import java.util.*;

public class ExpenseController 
{
     // Queue = Tracks added records (for coursework requirement)
    public static Queue<ExpenseModel> addQueue = new LinkedList<>();

    // Stack = Tracks deleted records for Undo
    public static Stack<ExpenseModel> deleteStack = new Stack<>();


    // ===================== ADD RECORD =====================
    public static String addExpense(
            String idText,
            String name,
            String title,
            String category,
            String amountText,
            String contact,
            String date
    ) {
        StringBuilder errors = new StringBuilder();

        int id = 0;
        double amount = 0;

        // ---- VALIDATION ----
        try {
            id = Integer.parseInt(idText);
            if (id <= 0) errors.append("• ID must be positive.\n");
        } catch (Exception e) {
            errors.append("• ID must be a number.\n");
        }

        if (name.isEmpty() || !name.matches("[a-zA-Z ]+"))
            errors.append("• Name must contain only alphabets.\n");

        if (title.isEmpty())
            errors.append("• Expense title cannot be empty.\n");

        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) errors.append("• Amount must be greater than 0.\n");
        } catch (Exception e) {
            errors.append("• Amount must be numeric.\n");
        }

        if (!contact.matches("\\d{10}"))
            errors.append("• Contact must be exactly 10 digits.\n");

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
            errors.append("• Date must be in YYYY-MM-DD format.\n");

        // If validation failed → return errors
        if (errors.length() > 0)
            return errors.toString();

        // ---- Create Model ----
        ExpenseModel e = new ExpenseModel(
                id, name, title, category, amount, contact, date
        );

        ExpenseRepository.expenses.add(e);
        ExpenseRepository.save();

        addQueue.offer(e);     // coursework: Queue usage

        return "SUCCESS";
    }



    // ===================== SEARCH BY ID & CATEGORY =====================
    public static ExpenseModel findExpense(int id, String category) {
        for (ExpenseModel e : ExpenseRepository.expenses) {
            if (e.getId() == id &&
                e.getCategory().equalsIgnoreCase(category))
                return e;
        }
        return null;
    }



    // ===================== DELETE =====================
    public static String deleteExpense(int id, String category) {

        ExpenseModel target = findExpense(id, category);

        if (target == null)
            return "NOT_FOUND";

        deleteStack.push(target);
        ExpenseRepository.expenses.remove(target);

        ExpenseRepository.save();
        return "SUCCESS";
    }



    // ===================== UNDO DELETE =====================
    public static String undoDelete() {

        if (deleteStack.isEmpty())
            return "EMPTY";

        ExpenseModel e = deleteStack.pop();

        // prevent duplicate
        for (ExpenseModel x : ExpenseRepository.expenses) {
            if (x.getId() == e.getId())
                return "DUPLICATE";
        }

        ExpenseRepository.expenses.add(e);
        ExpenseRepository.save();

        return "SUCCESS";
    }



    // ===================== UPDATE =====================
    public static String updateExpense(
            ExpenseModel record,
            String name,
            String title,
            String category,
            String amountText,
            String contact,
            String date
    ) {
        StringBuilder errors = new StringBuilder();
        double amount = 0;

        if (name.isEmpty())
            errors.append("• Name cannot be empty.\n");

        if (title.isEmpty())
            errors.append("• Expense title cannot be empty.\n");

        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0)
                errors.append("• Amount must be greater than 0.\n");
        } catch (Exception e) {
            errors.append("• Amount must be numeric.\n");
        }

        if (!contact.matches("\\d{10}"))
            errors.append("• Contact must be 10 digits.\n");

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
            errors.append("• Date must be YYYY-MM-DD.\n");

        if (errors.length() > 0)
            return errors.toString();

        // ---- APPLY UPDATE ----
        record.setName(name);
        record.setExpenseTitle(title);
        record.setCategory(category);
        record.setAmount(amount);
        record.setContact(contact);
        record.setDate(date);

        ExpenseRepository.save();
        return "SUCCESS";
    }
}
