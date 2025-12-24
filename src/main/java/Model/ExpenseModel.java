/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author erick
 */
public class ExpenseModel 
{
    private int id;
    private String name;
    private String expenseTitle;
    private String category;
    private double amount;
    private String contact;
    private String date;

    // Constructor
    public ExpenseModel(int id, String name, String expenseTitle, String category, double amount, String contact, String date) {
        this.id = id;
        this.name = name;
        this.expenseTitle = expenseTitle;
        this.category = category;
        this.amount = amount;
        this.contact = contact;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
    
    public String getContact() {
        return contact;
    }
    
    public String getDate() {
        return date;
    }

    // Setters (for Update feature)
    public void setName(String name) {
        this.name = name;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
}
