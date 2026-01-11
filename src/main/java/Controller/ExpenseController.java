package Controller;

import Model.ExpenseModel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ExpenseController 
{

    // ===================== DATA STRUCTURES =====================

    // Main storage (replaces CSV)
    public static ArrayList<ExpenseModel> expenses = new ArrayList<>();

    // Queue → track added records
    public static Queue<ExpenseModel> addQueue = new LinkedList<>();

    // Stack → undo delete
    public static Stack<ExpenseModel> deleteStack = new Stack<>();

    // ===================== Data =====================
    public static void loadData() 
    {  
        if (!expenses.isEmpty()) return;
    
        expenses.add(new ExpenseModel(836, "Erick Pradhan", "Student Expense", "Food & Groceries", 2500, "9800000000", "2025-01-05"));
        expenses.add(new ExpenseModel(283, "Sabal Shrestha", "Hostel Expense", "Transport / Travel", 1800, "9811111111", "2025-01-06"));
        expenses.add(new ExpenseModel(103, "Shahil Basnet", "Tribal Rain Concert", "Entertainment / Leisure", 1000, "9822222222", "2025-01-11"));
        expenses.add(new ExpenseModel(822, "Papit Ghimire", "Table Tennis Classes", "Entertainment / Leisure", 1500, "9833333333", "2025-01-02"));
        expenses.add(new ExpenseModel(638, "Semi Limbu", "Cosmetics", "Health & Personal Care", 8000, "9877777777", "2025-01-24"));
    }

    // ===================== ADD =====================
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
            if (amount <= 0)
                errors.append("• Amount must be greater than 0.\n");
        } catch (Exception e) {
            errors.append("• Amount must be numeric.\n");
        }

        if (!contact.matches("\\d{10}"))
            errors.append("• Contact must be exactly 10 digits.\n");

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
            errors.append("• Date must be in YYYY-MM-DD format.\n");

        if (errors.length() > 0)
            return errors.toString();

        // ---- CREATE & ADD ----
        ExpenseModel e = new ExpenseModel(
                id, name, title, category, amount, contact, date
        );

        expenses.add(e);
        addQueue.offer(e);

        return "SUCCESS";
    }


    // ===================== SEARCH =====================
    public static ExpenseModel findExpense(int id, String category) {
        for (ExpenseModel e : expenses) {
            if (e.getId() == id &&
                e.getCategory().equalsIgnoreCase(category)) {
                return e;
            }
        }
        return null;
    }


    // ===================== DELETE =====================
    public static String deleteExpense(int id, String category) {
        ExpenseModel target = findExpense(id, category);

        if (target == null)
            return "NOT_FOUND";

        deleteStack.push(target);
        expenses.remove(target);

        return "SUCCESS";
    }


    // ===================== UNDO DELETE =====================
    public static String undoDelete() {
        if (deleteStack.isEmpty())
            return "EMPTY";

        ExpenseModel e = deleteStack.pop();

        for (ExpenseModel x : expenses) {
            if (x.getId() == e.getId())
                return "DUPLICATE";
        }

        expenses.add(e);
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

        return "SUCCESS";
    }
}
