/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;
import Model.ExpenseModel;
import Controller.ExpenseController;
import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;

public class AdminInterface extends javax.swing.JFrame {
    ArrayList<ExpenseModel> ExpenseList = new  ArrayList<>();
    Queue<ExpenseModel> addQueue = new LinkedList<>();
    Stack<ExpenseModel> deleteStack = new Stack<>();
    private ExpenseModel updatingRecord;
    
    // Stores last state of Update Panel fields for Undo
    private ExpenseModel lastUpdateFormState = null;

    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminInterface.class.getName());

    public AdminInterface() {
        initComponents();
        loadFromCSV();

        if (ExpenseList.isEmpty()) {
            saveToCSV();
        }

        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);
        setLocationRelativeTo(null);
        
        jUndoButton.addActionListener(e -> undoDelete());
        jDeleteButton.addActionListener(e -> deleteSelectedRecord());
    }
    
    //Method
    private void loadFromCSV() 
    {
        try {
            java.io.File file = new java.io.File("Expenses.csv");
            if (!file.exists()) return;

            java.util.Scanner sc = new java.util.Scanner(file);
            ExpenseList.clear();

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
                    ExpenseList.add(e);
                }
            }

            sc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load data from CSV file.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    
    //Save to Expense.csv
    private void saveToCSV() {
        try {
            java.io.PrintWriter pw = new java.io.PrintWriter("Expenses.csv");

            for (ExpenseModel e : ExpenseList) {
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

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save data to CSV file.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    
    public static void loadStudentListToTable(javax.swing.JTable table, java.util.Collection<ExpenseModel> list){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        for (ExpenseModel exp : list)
        {
            Object[] row ={
                exp.getId(),
                exp.getName(),
                exp.getExpenseTitle(),
                exp.getCategory(),
                exp.getAmount(),
                exp.getContact(),
                exp.getDate()
            };
            model.addRow(row);
        }
        
    }
    
    public class GradientPanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#353637"),
                    0, getHeight(), Color.decode("#111214")
            );

            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    public class GradientPanelDark extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#23262A"),
                    0, getHeight(), Color.decode("#0E141C")
            );

            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    public class GradientPanelSoft extends JPanel
    {
        public GradientPanelSoft() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#2D2F33"),   // slightly lighter than parent top
                    0, getHeight(), Color.decode("#181B21")  // slightly lighter bottom
            );

            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    //Components
    public class RoundedPanel extends JPanel 
    {

        private int radius;

        public RoundedPanel(int radius) 
        {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }
    
    public class RoundedButton extends JButton 
    {
        final int radius = 30;
        final Color normalColor = new Color(30,30,30);
        final Color hoverColor = new Color(50,50,50);
        final Color pressColor = new Color(20,20,20);

        public RoundedButton(String text)
        {
            super(text);
            setBackground(normalColor);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed())
                g2.setColor(pressColor);
            else if (getModel().isRollover())
                g2.setColor(hoverColor);
            else
                g2.setColor(getBackground());

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }
    
    private void clearAddForm() 
    {
        jAddFieldID.setText("");
        jAddFieldName.setText("");
        jAddFieldExpenseTitle.setText("");
        jComboBoxCategory.setSelectedIndex(0);
        jAddFieldAmount.setText("");
        jAddFieldContact.setText("");
        jAddFieldDate.setText("");
    }

    private void addRecord() 
    {

        StringBuilder errors = new StringBuilder();

        // --- Read all fields ---
        String idText       = jAddFieldID.getText().trim();
        String name         = jAddFieldName.getText().trim();
        String expenseTitle = jAddFieldExpenseTitle.getText().trim();
        String category     = jComboBoxCategory.getSelectedItem().toString();
        String amountText   = jAddFieldAmount.getText().trim();
        String contact      = jAddFieldContact.getText().trim();
        String date         = jAddFieldDate.getText().trim();

        // -------- VALIDATION --------

        // ID = integer only
        int id = 0;
        try {
            id = Integer.parseInt(idText);
            if(id <= 0)
                errors.append("• ID must be a positive number.\n");
        } catch (Exception e) {
            errors.append("• Please enter a valid numeric ID.\n");
        }

        // Name = required letters only
        if(name.isEmpty() || !name.matches("[a-zA-Z ]+"))
            errors.append("• Please enter a valid name (letters only).\n");

        // Expense Title
        if(expenseTitle.isEmpty())
            errors.append("• Expense title cannot be empty.\n");

        // Amount must be number
        double amount = 0;
        try {
            amount = Double.parseDouble(amountText);
            if(amount <= 0)
                errors.append("• Amount must be greater than 0.\n");
        } catch (Exception e) {
            errors.append("• Please enter a valid numeric amount.\n");
        }

        // Contact must be EXACTLY 10 digits
        if(!contact.matches("\\d{10}"))
            errors.append("• Contact number must contain exactly 10 digits.\n");

        // Simple Date validation YYYY-MM-DD
        if(!date.matches("\\d{4}-\\d{2}-\\d{2}"))
            errors.append("• Please enter date in valid format (YYYY-MM-DD).\n");


        // -------- If ANY error, show ALL in one dialog --------
        if(errors.length() > 0){
            JOptionPane.showMessageDialog(
                    this,
                    "Please correct the following issues:\n\n" + errors,
                    "Input Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }


        // -------- If VALID → Create Model --------
        ExpenseModel exp = new ExpenseModel(
                id,
                name,
                expenseTitle,
                category,
                amount,
                contact,
                date
        );

        // Add to ArrayList
        ExpenseList.add(exp);
        
        //Save to file
        saveToCSV();
        
        addQueue.offer(exp);  

        // Refresh Dashboard Table
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);

        // Success message
        JOptionPane.showMessageDialog(
                this,
                "The expense record has been added successfully.",
                "Record Added",
                JOptionPane.INFORMATION_MESSAGE
        );

        clearAddForm();
    }
    
    private void deleteSelectedRecord() 
    {
        String idText = jTextFieldDeleteID.getText().trim();

        // Validate ID
        int id;
        try {
            id = Integer.parseInt(idText);
            if(id <= 0){
                JOptionPane.showMessageDialog(this,
                    "ID must be a positive number.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this,
                "Please enter a valid numeric ID.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String category = jComboBoxDeleteCategory
                            .getSelectedItem()
                            .toString()
                            .trim();

        ExpenseModel target = null;

        for(ExpenseModel e : ExpenseList){
            if(e.getId() == id && e.getCategory().equalsIgnoreCase(category)){
                target = e;
                break;
            }
        }

        if(target == null){
            JOptionPane.showMessageDialog(this,
                "No matching record found for the given ID and Category.",
                "Delete Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // 🔥 CONFIRMATION BOX
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this record?\n\n" +
                "ID: " + target.getId() + 
                "\nName: " + target.getName() +
                "\nCategory: " + target.getCategory(),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if(choice != JOptionPane.YES_OPTION){
            return; // user cancelled
        }

        // Push to undo stack
        deleteStack.push(target);

        // Remove record
        ExpenseList.remove(target);

        // Save + Refresh
        saveToCSV();
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);

        JOptionPane.showMessageDialog(this,
            "Record deleted successfully.",
            "Delete Successful",
            JOptionPane.INFORMATION_MESSAGE
        );

        jTextFieldDeleteID.setText("");
        jComboBoxDeleteCategory.setSelectedIndex(0);
    }
    
    private void undoDelete() 
    {
        if (deleteStack.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "There are no deleted records available to restore.",
                    "Undo Unavailable",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ExpenseModel restored = deleteStack.pop();

        // --- Prevent duplicate restore ---
        boolean exists = false;
        for (ExpenseModel e : ExpenseList) {
            if (e.getId() == restored.getId()) {
                exists = true;
                break;
            }
        }

        if (exists) {
            JOptionPane.showMessageDialog(
                    this,
                    "The deleted record could not be restored because an entry with the same ID already exists.",
                    "Restore Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // --- Add back ---
        ExpenseList.add(restored);

        // OPTIONAL: keep table naturally sorted by ID
        ExpenseList.sort((a, b) -> Integer.compare(a.getId(), b.getId()));

        saveToCSV();
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);

        JOptionPane.showMessageDialog(
                this,
                "The last deleted record has been successfully restored.\n\n"
              + "ID: " + restored.getId() + "\n"
              + "Name: " + restored.getName() + "\n"
              + "Expense: " + restored.getExpenseTitle() + "\n"
              + "Category: " + restored.getCategory() + "\n"
              + "Amount: Rs. " + restored.getAmount(),
                "Undo Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void loadDataToUpdatePanel(ExpenseModel e){
        updatingRecord = e;

        jUpdateFieldID.setText(String.valueOf(e.getId()));
        jUpdateFieldName.setText(e.getName());
        jUpdateFieldExpenseTitle.setText(e.getExpenseTitle());
        jComboBoxUpdateCategory2.setSelectedItem(e.getCategory());
        jUpdateFieldAmount.setText(String.valueOf(e.getAmount()));
        jUpdateFieldContact.setText(e.getContact());
        jUpdateFieldDate.setText(e.getDate());
    }


    private void jUpdateSearchButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {                                                   
        String idText = jUpdateTextFieldIDAdmin.getText().trim();

        // Read category from ComboBox
        String category = jComboBoxUpdateCategory
                .getSelectedItem()
                .toString()
                .trim();

        if(idText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter Expense ID.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "ID must be a valid number.",
                "Invalid ID",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ------- SEARCH RECORD -------
        ExpenseModel target = null;

        for(ExpenseModel e : ExpenseList){
            if(e.getId() == id && e.getCategory().equalsIgnoreCase(category)){
                target = e;
                break;
            }
        }

        if(target == null){
            JOptionPane.showMessageDialog(
                this,
                "No record found with matching ID and Category.",
                "Record Not Found",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // -------- If FOUND → Load into Update Panel --------
        loadDataToUpdatePanel(target);

        // Switch Panel
        jParentPanel.removeAll();
        jParentPanel.add(jActualUpdateRecordPanelAdmin);   // <-- your real update panel
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }
    
    private void updateRecord() {

        if (updatingRecord == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No record selected for update.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        StringBuilder errors = new StringBuilder();

        // Read updated values from fields
        String name = jUpdateFieldID.getText().trim();
        String title = jUpdateFieldName.getText().trim();
        String category = jComboBoxUpdateCategory2.getSelectedItem().toString();
        String amountText = jUpdateFieldAmount.getText().trim();
        String contact = jUpdateFieldContact.getText().trim();
        String date = jUpdateFieldDate.getText().trim();

        double amount = 0;

        if (name.isEmpty()) errors.append("• Name cannot be empty\n");
        if (title.isEmpty()) errors.append("• Expense title cannot be empty\n");

        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) errors.append("• Amount must be greater than 0\n");
        } catch (Exception e) {
            errors.append("• Invalid amount value\n");
        }

        if (!contact.matches("\\d{10}"))
            errors.append("• Contact must be exactly 10 digits\n");

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
            errors.append("• Date must be YYYY-MM-DD\n");

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please correct the following:\n\n" + errors,
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // -------- APPLY UPDATES --------
        updatingRecord.setName(name);
        updatingRecord.setExpenseTitle(title);
        updatingRecord.setCategory(category);
        updatingRecord.setAmount(amount);
        updatingRecord.setContact(contact);
        updatingRecord.setDate(date);

        saveToCSV();
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);

        JOptionPane.showMessageDialog(
                this,
                "Record updated successfully.",
                "Update Complete",
                JOptionPane.INFORMATION_MESSAGE
        );

        updatingRecord = null;   // clear reference
    }

    private void resetAddPanel() 
    {
        jAddFieldID.setText("");
        jAddFieldName.setText("");
        jAddFieldExpenseTitle.setText("");
        jAddFieldAmount.setText("");
        jAddFieldContact.setText("");
        jAddFieldDate.setText("");
        jComboBoxCategory.setSelectedIndex(0);
    }
    
    private void resetDeletePanel() 
    {
        jTextFieldDeleteID.setText("");
        jComboBoxDeleteCategory.setSelectedIndex(0);
    }

    private void resetUpdateSearchPanel() {
        jUpdateTextFieldIDAdmin.setText("");
        jComboBoxUpdateCategory.setSelectedIndex(0);
    }
    
    private void resetActualUpdatePanel() {
        jUpdateFieldID.setText("");
        jUpdateFieldName.setText("");
        jUpdateFieldExpenseTitle.setText("");
        jUpdateFieldAmount.setText("");
        jUpdateFieldContact.setText("");
        jUpdateFieldDate.setText("");
        jComboBoxUpdateCategory2.setSelectedIndex(0);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new GradientPanel() ;
        jPanel2 = new GradientPanel() ;
        jLabel1 = new javax.swing.JLabel();
        jDashboardButtonAdmin = new RoundedButton("Button Text")
        ;
        jDashboardButtonAdmin2 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin6 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin7 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin4 = new RoundedButton("Button Text") ;
        jPanel3 = new GradientPanelDark() ;
        jParentPanel = new javax.swing.JPanel();
        jDashboardPanelAdmin = new GradientPanelDark() ;
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jUndoButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jDashboardTableAdmin = new javax.swing.JTable();
        jAddRecordPanelAdmin = new GradientPanelDark();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new GradientPanelSoft();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jAddFieldID = new javax.swing.JTextField();
        jAddFieldName = new javax.swing.JTextField();
        jAddFieldExpenseTitle = new javax.swing.JTextField();
        jComboBoxCategory = new javax.swing.JComboBox<>();
        jAddFieldAmount = new javax.swing.JTextField();
        jAddFieldContact = new javax.swing.JTextField();
        jAddFieldDate = new javax.swing.JTextField();
        jDashboardButtonAdmin3 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin8 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin16 = new RoundedButton("Button Text") ;
        jDeleteRecordPanelAdmin = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel5 = new GradientPanelSoft();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldDeleteID = new javax.swing.JTextField();
        jComboBoxDeleteCategory = new javax.swing.JComboBox<>();
        jDeleteButton = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin9 = new RoundedButton("Button Text") ;
        jDeleteButton1 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin17 = new RoundedButton("Button Text") ;
        jUpdateRecordPanelAdmin = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel10 = new GradientPanelSoft();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jUpdateTextFieldIDAdmin = new javax.swing.JTextField();
        jComboBoxUpdateCategory = new javax.swing.JComboBox<>();
        jDashboardButtonAdmin5 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin10 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin11 = new RoundedButton("Button Text") ;
        jActualUpdateRecordPanelAdmin = new GradientPanelDark();
        jLabel16 = new javax.swing.JLabel();
        jPanel11 = new GradientPanelSoft();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jUpdateFieldID = new javax.swing.JTextField();
        jUpdateFieldName = new javax.swing.JTextField();
        jUpdateFieldExpenseTitle = new javax.swing.JTextField();
        jComboBoxUpdateCategory2 = new javax.swing.JComboBox<>();
        jUpdateFieldAmount = new javax.swing.JTextField();
        jUpdateFieldContact = new javax.swing.JTextField();
        jUpdateFieldDate = new javax.swing.JTextField();
        jDashboardButtonAdmin12 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin13 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin14 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin15 = new RoundedButton("Button Text") ;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(21, 20, 20));

        jPanel1.setBackground(new java.awt.Color(53, 54, 55));

        jPanel2.setBackground(new java.awt.Color(53, 54, 55));

        jLabel1.setFont(new java.awt.Font("Iceberg", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(170, 170, 170));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("   Expense Tracker");

        jDashboardButtonAdmin.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin.setText("      Dashboard");
        jDashboardButtonAdmin.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin.addActionListener(this::jDashboardButtonAdminActionPerformed);

        jDashboardButtonAdmin2.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin2.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin2.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin2.setText("      Add Record");
        jDashboardButtonAdmin2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin2.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin2.addActionListener(this::jDashboardButtonAdmin2ActionPerformed);

        jDashboardButtonAdmin6.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin6.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin6.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin6.setText("      Update Record");
        jDashboardButtonAdmin6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin6.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin6.addActionListener(this::jDashboardButtonAdmin6ActionPerformed);

        jDashboardButtonAdmin7.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin7.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin7.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin7.setText("      Profile");
        jDashboardButtonAdmin7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin7.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin7.addActionListener(this::jDashboardButtonAdmin7ActionPerformed);

        jDashboardButtonAdmin4.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin4.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin4.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin4.setText("      Delete Record");
        jDashboardButtonAdmin4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin4.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin4.addActionListener(this::jDashboardButtonAdmin4ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 6, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDashboardButtonAdmin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDashboardButtonAdmin2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDashboardButtonAdmin6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jDashboardButtonAdmin7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jDashboardButtonAdmin4, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDashboardButtonAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDashboardButtonAdmin2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDashboardButtonAdmin4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDashboardButtonAdmin6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(331, 331, 331)
                .addComponent(jDashboardButtonAdmin7, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(21, 20, 20));

        jParentPanel.setBackground(new java.awt.Color(21, 20, 20));
        jParentPanel.setLayout(new java.awt.CardLayout());

        jDashboardPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel4.setBackground(new java.awt.Color(21, 20, 20));
        jLabel4.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(170, 170, 170));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Welcome to our Dashboard!");

        jButton4.setText("Add Record");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addComponent(jButton4)
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 287, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jUndoButton.setText("Undo Delete");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jUndoButton)
                .addContainerGap(107, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(jUndoButton)
                .addGap(35, 35, 35))
        );

        jPanel9.setPreferredSize(new java.awt.Dimension(1005, 70));

        jButton1.setText("Sort By Name");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setText("Sort By Amount");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jButton3.setText("Sort By ID");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(222, 222, 222)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jDashboardTableAdmin.setBackground(new java.awt.Color(240, 240, 240));
        jDashboardTableAdmin.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jDashboardTableAdmin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "NAME", "EXPENSE TITLE", "CATEGORY", "AMOUNT", "CONTACT", "DATE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jDashboardTableAdmin);

        javax.swing.GroupLayout jDashboardPanelAdminLayout = new javax.swing.GroupLayout(jDashboardPanelAdmin);
        jDashboardPanelAdmin.setLayout(jDashboardPanelAdminLayout);
        jDashboardPanelAdminLayout.setHorizontalGroup(
            jDashboardPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDashboardPanelAdminLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jDashboardPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1006, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jDashboardPanelAdminLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );
        jDashboardPanelAdminLayout.setVerticalGroup(
            jDashboardPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDashboardPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDashboardPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDashboardPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );

        jParentPanel.add(jDashboardPanelAdmin, "card9");

        jAddRecordPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel6.setBackground(new java.awt.Color(21, 20, 20));
        jLabel6.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(170, 170, 170));
        jLabel6.setText("           Input New Expense Details (Admin Only)");

        jPanel4.setBackground(new java.awt.Color(21, 20, 20));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setPreferredSize(new java.awt.Dimension(965, 575));

        jLabel5.setBackground(new java.awt.Color(21, 20, 20));
        jLabel5.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(170, 170, 170));
        jLabel5.setText("Expense Title:");
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel7.setBackground(new java.awt.Color(21, 20, 20));
        jLabel7.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(170, 170, 170));
        jLabel7.setText("Category:");
        jLabel7.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel8.setBackground(new java.awt.Color(21, 20, 20));
        jLabel8.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(170, 170, 170));
        jLabel8.setText("Amount(Rs.):");
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel9.setBackground(new java.awt.Color(21, 20, 20));
        jLabel9.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(170, 170, 170));
        jLabel9.setText("Name:");
        jLabel9.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel10.setBackground(new java.awt.Color(21, 20, 20));
        jLabel10.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(170, 170, 170));
        jLabel10.setText("ID:");
        jLabel10.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel11.setBackground(new java.awt.Color(21, 20, 20));
        jLabel11.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(170, 170, 170));
        jLabel11.setText("Contact:");
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel12.setBackground(new java.awt.Color(21, 20, 20));
        jLabel12.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(170, 170, 170));
        jLabel12.setText("Date:");
        jLabel12.setPreferredSize(new java.awt.Dimension(100, 100));

        jAddFieldID.setBackground(new java.awt.Color(21, 20, 20));
        jAddFieldID.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddFieldID.setForeground(new java.awt.Color(170, 170, 170));
        jAddFieldID.setBorder(null);
        jAddFieldID.setSelectionColor(new java.awt.Color(170, 170, 170));
        jAddFieldID.addActionListener(this::jAddFieldIDActionPerformed);

        jAddFieldName.setBackground(new java.awt.Color(21, 20, 20));
        jAddFieldName.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddFieldName.setForeground(new java.awt.Color(170, 170, 170));
        jAddFieldName.setBorder(null);
        jAddFieldName.setSelectionColor(new java.awt.Color(170, 170, 170));

        jAddFieldExpenseTitle.setBackground(new java.awt.Color(21, 20, 20));
        jAddFieldExpenseTitle.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddFieldExpenseTitle.setForeground(new java.awt.Color(170, 170, 170));
        jAddFieldExpenseTitle.setBorder(null);
        jAddFieldExpenseTitle.setSelectionColor(new java.awt.Color(170, 170, 170));

        jComboBoxCategory.setBackground(new java.awt.Color(21, 20, 20));
        jComboBoxCategory.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jComboBoxCategory.setForeground(new java.awt.Color(170, 170, 170));
        jComboBoxCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Food & Groceries", "Transport / Travel", "Bills & Utilities", "Entertainment / Leisure", "Health & Personal Care" }));
        jComboBoxCategory.setBorder(null);

        jAddFieldAmount.setBackground(new java.awt.Color(21, 20, 20));
        jAddFieldAmount.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddFieldAmount.setForeground(new java.awt.Color(170, 170, 170));
        jAddFieldAmount.setBorder(null);
        jAddFieldAmount.setSelectionColor(new java.awt.Color(170, 170, 170));

        jAddFieldContact.setBackground(new java.awt.Color(21, 20, 20));
        jAddFieldContact.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddFieldContact.setForeground(new java.awt.Color(170, 170, 170));
        jAddFieldContact.setBorder(null);
        jAddFieldContact.setSelectionColor(new java.awt.Color(170, 170, 170));

        jAddFieldDate.setBackground(new java.awt.Color(21, 20, 20));
        jAddFieldDate.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddFieldDate.setForeground(new java.awt.Color(170, 170, 170));
        jAddFieldDate.setBorder(null);
        jAddFieldDate.setSelectionColor(new java.awt.Color(170, 170, 170));

        jDashboardButtonAdmin3.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin3.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin3.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin3.setText("Add Record");
        jDashboardButtonAdmin3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin3.addActionListener(this::jDashboardButtonAdmin3ActionPerformed);

        jDashboardButtonAdmin8.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin8.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin8.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin8.setText("Dashboard");
        jDashboardButtonAdmin8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin8.addActionListener(this::jDashboardButtonAdmin8ActionPerformed);

        jDashboardButtonAdmin16.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin16.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin16.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin16.setText("Reset");
        jDashboardButtonAdmin16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin16.addActionListener(this::jDashboardButtonAdmin16ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jDashboardButtonAdmin3, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(jDashboardButtonAdmin8, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDashboardButtonAdmin16, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jAddFieldID, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                .addComponent(jAddFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                .addComponent(jAddFieldExpenseTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                .addComponent(jComboBoxCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jAddFieldAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                .addComponent(jAddFieldContact, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                .addComponent(jAddFieldDate, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)))))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jAddFieldID, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAddFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAddFieldExpenseTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAddFieldAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAddFieldContact, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAddFieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jDashboardButtonAdmin16, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDashboardButtonAdmin3, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addComponent(jDashboardButtonAdmin8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jAddRecordPanelAdminLayout = new javax.swing.GroupLayout(jAddRecordPanelAdmin);
        jAddRecordPanelAdmin.setLayout(jAddRecordPanelAdminLayout);
        jAddRecordPanelAdminLayout.setHorizontalGroup(
            jAddRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRecordPanelAdminLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jAddRecordPanelAdminLayout.setVerticalGroup(
            jAddRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 608, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jParentPanel.add(jAddRecordPanelAdmin, "card7");

        jDeleteRecordPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel13.setBackground(new java.awt.Color(21, 20, 20));
        jLabel13.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(170, 170, 170));
        jLabel13.setText("           Delete  Expense Details (Admin Only)");

        jPanel5.setBackground(new java.awt.Color(21, 20, 20));
        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel5.setPreferredSize(new java.awt.Dimension(965, 575));

        jLabel15.setBackground(new java.awt.Color(21, 20, 20));
        jLabel15.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(170, 170, 170));
        jLabel15.setText("Category:");
        jLabel15.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel18.setBackground(new java.awt.Color(21, 20, 20));
        jLabel18.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(170, 170, 170));
        jLabel18.setText("ID:");
        jLabel18.setPreferredSize(new java.awt.Dimension(100, 100));

        jTextFieldDeleteID.setBackground(new java.awt.Color(21, 20, 20));
        jTextFieldDeleteID.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jTextFieldDeleteID.setForeground(new java.awt.Color(170, 170, 170));
        jTextFieldDeleteID.setBorder(null);
        jTextFieldDeleteID.setSelectionColor(new java.awt.Color(170, 170, 170));
        jTextFieldDeleteID.addActionListener(this::jTextFieldDeleteIDActionPerformed);

        jComboBoxDeleteCategory.setBackground(new java.awt.Color(21, 20, 20));
        jComboBoxDeleteCategory.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jComboBoxDeleteCategory.setForeground(new java.awt.Color(170, 170, 170));
        jComboBoxDeleteCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Food & Groceries", "Transport / Travel", "Bills & Utilities", "Entertainment / Leisure", "Health & Personal Care" }));
        jComboBoxDeleteCategory.setBorder(null);

        jDeleteButton.setBackground(new java.awt.Color(53, 54, 55));
        jDeleteButton.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDeleteButton.setForeground(new java.awt.Color(170, 170, 170));
        jDeleteButton.setText("Delete Record");
        jDeleteButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDeleteButton.addActionListener(this::jDeleteButtonActionPerformed);

        jDashboardButtonAdmin9.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin9.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin9.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin9.setText("Undo ");
        jDashboardButtonAdmin9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin9.addActionListener(this::jDashboardButtonAdmin9ActionPerformed);

        jDeleteButton1.setBackground(new java.awt.Color(53, 54, 55));
        jDeleteButton1.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDeleteButton1.setForeground(new java.awt.Color(170, 170, 170));
        jDeleteButton1.setText("Dashboard");
        jDeleteButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDeleteButton1.addActionListener(this::jDeleteButton1ActionPerformed);

        jDashboardButtonAdmin17.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin17.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin17.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin17.setText("Reset");
        jDashboardButtonAdmin17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin17.addActionListener(this::jDashboardButtonAdmin17ActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jDashboardButtonAdmin9, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(jDashboardButtonAdmin17, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDeleteButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldDeleteID, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jComboBoxDeleteCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDeleteID, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxDeleteCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(237, 237, 237)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDashboardButtonAdmin9, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(jDashboardButtonAdmin17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(jDeleteButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout jDeleteRecordPanelAdminLayout = new javax.swing.GroupLayout(jDeleteRecordPanelAdmin);
        jDeleteRecordPanelAdmin.setLayout(jDeleteRecordPanelAdminLayout);
        jDeleteRecordPanelAdminLayout.setHorizontalGroup(
            jDeleteRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jDeleteRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDeleteRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(45, Short.MAX_VALUE)))
        );
        jDeleteRecordPanelAdminLayout.setVerticalGroup(
            jDeleteRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDeleteRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 659, Short.MAX_VALUE))
            .addGroup(jDeleteRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDeleteRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(81, 81, 81)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(81, Short.MAX_VALUE)))
        );

        jParentPanel.add(jDeleteRecordPanelAdmin, "card2");

        jUpdateRecordPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel14.setBackground(new java.awt.Color(21, 20, 20));
        jLabel14.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(170, 170, 170));
        jLabel14.setText("           Choose ID & Category To Update Expense Details (Admin Only)");

        jPanel10.setBackground(new java.awt.Color(21, 20, 20));
        jPanel10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel10.setPreferredSize(new java.awt.Dimension(965, 575));

        jLabel17.setBackground(new java.awt.Color(21, 20, 20));
        jLabel17.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(170, 170, 170));
        jLabel17.setText("Category:");
        jLabel17.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel21.setBackground(new java.awt.Color(21, 20, 20));
        jLabel21.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(170, 170, 170));
        jLabel21.setText("ID:");
        jLabel21.setPreferredSize(new java.awt.Dimension(100, 100));

        jUpdateTextFieldIDAdmin.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateTextFieldIDAdmin.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateTextFieldIDAdmin.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateTextFieldIDAdmin.setBorder(null);
        jUpdateTextFieldIDAdmin.setSelectionColor(new java.awt.Color(170, 170, 170));
        jUpdateTextFieldIDAdmin.addActionListener(this::jUpdateTextFieldIDAdminActionPerformed);

        jComboBoxUpdateCategory.setBackground(new java.awt.Color(21, 20, 20));
        jComboBoxUpdateCategory.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jComboBoxUpdateCategory.setForeground(new java.awt.Color(170, 170, 170));
        jComboBoxUpdateCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Food & Groceries", "Transport / Travel", "Bills & Utilities", "Entertainment / Leisure", "Health & Personal Care" }));
        jComboBoxUpdateCategory.setBorder(null);

        jDashboardButtonAdmin5.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin5.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin5.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin5.setText("Update Record");
        jDashboardButtonAdmin5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin5.addActionListener(this::jDashboardButtonAdmin5ActionPerformed);

        jDashboardButtonAdmin10.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin10.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin10.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin10.setText("Dashboard");
        jDashboardButtonAdmin10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin10.addActionListener(this::jDashboardButtonAdmin10ActionPerformed);

        jDashboardButtonAdmin11.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin11.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin11.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin11.setText("Reset");
        jDashboardButtonAdmin11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin11.addActionListener(this::jDashboardButtonAdmin11ActionPerformed);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jDashboardButtonAdmin5, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(jDashboardButtonAdmin10, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDashboardButtonAdmin11, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jUpdateTextFieldIDAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                .addComponent(jComboBoxUpdateCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jUpdateTextFieldIDAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxUpdateCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(237, 237, 237)
                .addComponent(jDashboardButtonAdmin11, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDashboardButtonAdmin5, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(jDashboardButtonAdmin10, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout jUpdateRecordPanelAdminLayout = new javax.swing.GroupLayout(jUpdateRecordPanelAdmin);
        jUpdateRecordPanelAdmin.setLayout(jUpdateRecordPanelAdminLayout);
        jUpdateRecordPanelAdminLayout.setHorizontalGroup(
            jUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jUpdateRecordPanelAdminLayout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jUpdateRecordPanelAdminLayout.setVerticalGroup(
            jUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdateRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 72, Short.MAX_VALUE))
        );

        jParentPanel.add(jUpdateRecordPanelAdmin, "card3");

        jActualUpdateRecordPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel16.setBackground(new java.awt.Color(21, 20, 20));
        jLabel16.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(170, 170, 170));
        jLabel16.setText("           Update Expense Details (Admin Only)");

        jPanel11.setBackground(new java.awt.Color(21, 20, 20));
        jPanel11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel11.setPreferredSize(new java.awt.Dimension(965, 575));

        jLabel19.setBackground(new java.awt.Color(21, 20, 20));
        jLabel19.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(170, 170, 170));
        jLabel19.setText("Expense Title:");
        jLabel19.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel20.setBackground(new java.awt.Color(21, 20, 20));
        jLabel20.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(170, 170, 170));
        jLabel20.setText("Category:");
        jLabel20.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel22.setBackground(new java.awt.Color(21, 20, 20));
        jLabel22.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(170, 170, 170));
        jLabel22.setText("Amount(Rs.):");
        jLabel22.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel23.setBackground(new java.awt.Color(21, 20, 20));
        jLabel23.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(170, 170, 170));
        jLabel23.setText("Name:");
        jLabel23.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel24.setBackground(new java.awt.Color(21, 20, 20));
        jLabel24.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(170, 170, 170));
        jLabel24.setText("ID:");
        jLabel24.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel25.setBackground(new java.awt.Color(21, 20, 20));
        jLabel25.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(170, 170, 170));
        jLabel25.setText("Contact:");
        jLabel25.setPreferredSize(new java.awt.Dimension(100, 100));

        jLabel26.setBackground(new java.awt.Color(21, 20, 20));
        jLabel26.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(170, 170, 170));
        jLabel26.setText("Date:");
        jLabel26.setPreferredSize(new java.awt.Dimension(100, 100));

        jUpdateFieldID.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateFieldID.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateFieldID.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateFieldID.setBorder(null);
        jUpdateFieldID.setSelectionColor(new java.awt.Color(170, 170, 170));
        jUpdateFieldID.addActionListener(this::jUpdateFieldIDActionPerformed);

        jUpdateFieldName.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateFieldName.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateFieldName.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateFieldName.setBorder(null);
        jUpdateFieldName.setSelectionColor(new java.awt.Color(170, 170, 170));

        jUpdateFieldExpenseTitle.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateFieldExpenseTitle.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateFieldExpenseTitle.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateFieldExpenseTitle.setBorder(null);
        jUpdateFieldExpenseTitle.setSelectionColor(new java.awt.Color(170, 170, 170));

        jComboBoxUpdateCategory2.setBackground(new java.awt.Color(21, 20, 20));
        jComboBoxUpdateCategory2.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jComboBoxUpdateCategory2.setForeground(new java.awt.Color(170, 170, 170));
        jComboBoxUpdateCategory2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Food & Groceries", "Transport / Travel", "Bills & Utilities", "Entertainment / Leisure", "Health & Personal Care" }));
        jComboBoxUpdateCategory2.setBorder(null);

        jUpdateFieldAmount.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateFieldAmount.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateFieldAmount.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateFieldAmount.setBorder(null);
        jUpdateFieldAmount.setSelectionColor(new java.awt.Color(170, 170, 170));

        jUpdateFieldContact.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateFieldContact.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateFieldContact.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateFieldContact.setBorder(null);
        jUpdateFieldContact.setSelectionColor(new java.awt.Color(170, 170, 170));

        jUpdateFieldDate.setBackground(new java.awt.Color(21, 20, 20));
        jUpdateFieldDate.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateFieldDate.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateFieldDate.setBorder(null);
        jUpdateFieldDate.setSelectionColor(new java.awt.Color(170, 170, 170));

        jDashboardButtonAdmin12.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin12.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin12.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin12.setText("Update Record");
        jDashboardButtonAdmin12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin12.addActionListener(this::jDashboardButtonAdmin12ActionPerformed);

        jDashboardButtonAdmin13.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin13.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin13.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin13.setText("Dashboard");
        jDashboardButtonAdmin13.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin13.addActionListener(this::jDashboardButtonAdmin13ActionPerformed);

        jDashboardButtonAdmin14.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin14.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin14.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin14.setText("Undo");
        jDashboardButtonAdmin14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin14.addActionListener(this::jDashboardButtonAdmin14ActionPerformed);

        jDashboardButtonAdmin15.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin15.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin15.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin15.setText("Reset");
        jDashboardButtonAdmin15.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin15.addActionListener(this::jDashboardButtonAdmin15ActionPerformed);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jDashboardButtonAdmin14, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDashboardButtonAdmin15, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jDashboardButtonAdmin12, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(jDashboardButtonAdmin13, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jUpdateFieldID, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jUpdateFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jUpdateFieldExpenseTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jComboBoxUpdateCategory2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jUpdateFieldAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jUpdateFieldContact, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jUpdateFieldDate, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jUpdateFieldID, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUpdateFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUpdateFieldExpenseTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxUpdateCategory2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUpdateFieldAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jUpdateFieldContact, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUpdateFieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDashboardButtonAdmin14, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(jDashboardButtonAdmin15, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDashboardButtonAdmin12, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(jDashboardButtonAdmin13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jActualUpdateRecordPanelAdminLayout = new javax.swing.GroupLayout(jActualUpdateRecordPanelAdmin);
        jActualUpdateRecordPanelAdmin.setLayout(jActualUpdateRecordPanelAdminLayout);
        jActualUpdateRecordPanelAdminLayout.setHorizontalGroup(
            jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 1054, Short.MAX_VALUE)
            .addGroup(jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jActualUpdateRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(45, Short.MAX_VALUE)))
        );
        jActualUpdateRecordPanelAdminLayout.setVerticalGroup(
            jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jActualUpdateRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 659, Short.MAX_VALUE))
            .addGroup(jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jActualUpdateRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(81, 81, 81)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(34, Short.MAX_VALUE)))
        );

        jParentPanel.add(jActualUpdateRecordPanelAdmin, "card6");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jParentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void jDashboardButtonAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdminActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDashboardPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdminActionPerformed

    private void jDashboardButtonAdmin2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin2ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jAddRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin2ActionPerformed

    private void jDashboardButtonAdmin6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin6ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jUpdateRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin6ActionPerformed

    private void jDashboardButtonAdmin7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin7ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jUpdateRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin7ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ExpenseController.SelectionSortByName(ExpenseList);
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ExpenseController.InsertionSortByAmount(ExpenseList);
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        ExpenseController.BubbleSortById(ExpenseList);
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jAddFieldIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddFieldIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jAddFieldIDActionPerformed

    private void jDashboardButtonAdmin3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin3ActionPerformed
            addRecord();
    }//GEN-LAST:event_jDashboardButtonAdmin3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jAddRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jDashboardButtonAdmin4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin4ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDeleteRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin4ActionPerformed

    private void jTextFieldDeleteIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDeleteIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDeleteIDActionPerformed

    private void jDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jDeleteButtonActionPerformed

    private void jDashboardButtonAdmin8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin8ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDashboardPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin8ActionPerformed

    private void jDashboardButtonAdmin9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin9ActionPerformed
        undoDelete();
    }//GEN-LAST:event_jDashboardButtonAdmin9ActionPerformed

    private void jDeleteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteButton1ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDashboardPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDeleteButton1ActionPerformed

    private void jUpdateTextFieldIDAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUpdateTextFieldIDAdminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUpdateTextFieldIDAdminActionPerformed

    private void jDashboardButtonAdmin5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin5ActionPerformed
        String idText = jUpdateTextFieldIDAdmin.getText().trim();
        String category = jComboBoxUpdateCategory.getSelectedItem().toString().trim();

        if(idText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter Expense ID.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "ID must be a valid number.",
                "Invalid ID",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ExpenseModel target = null;
        for(ExpenseModel e : ExpenseList){
            if(e.getId() == id && e.getCategory().equalsIgnoreCase(category)){
                target = e;
                break;
            }
        }

        if(target == null){
            JOptionPane.showMessageDialog(
                this,
                "No record found with matching ID and Category.",
                "Record Not Found",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        loadDataToUpdatePanel(target);

        jParentPanel.removeAll();
        jParentPanel.add(jActualUpdateRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin5ActionPerformed

    private void jDashboardButtonAdmin10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin10ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDashboardPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin10ActionPerformed

    private void jDashboardButtonAdmin11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin11ActionPerformed
        resetUpdateSearchPanel();
    }//GEN-LAST:event_jDashboardButtonAdmin11ActionPerformed

    private void jUpdateFieldIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUpdateFieldIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUpdateFieldIDActionPerformed

    private void jDashboardButtonAdmin12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin12ActionPerformed
        updateRecord();
    }//GEN-LAST:event_jDashboardButtonAdmin12ActionPerformed

    private void jDashboardButtonAdmin13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin13ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDashboardPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin13ActionPerformed

    private void jDashboardButtonAdmin14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin14ActionPerformed
        if(lastUpdateFormState == null){
            JOptionPane.showMessageDialog(
                this,
                "There is no previous data to restore.",
                "Undo Unavailable",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Restore values
        jUpdateFieldID.setText(String.valueOf(lastUpdateFormState.getId()));
        jUpdateFieldName.setText(lastUpdateFormState.getName());
        jUpdateFieldExpenseTitle.setText(lastUpdateFormState.getExpenseTitle());
        jComboBoxUpdateCategory2.setSelectedItem(lastUpdateFormState.getCategory());
        jUpdateFieldAmount.setText(String.valueOf(lastUpdateFormState.getAmount()));
        jUpdateFieldContact.setText(lastUpdateFormState.getContact());
        jUpdateFieldDate.setText(lastUpdateFormState.getDate());

        JOptionPane.showMessageDialog(
            this,
            "Previous values successfully restored.",
            "Undo Successful",
            JOptionPane.INFORMATION_MESSAGE
        );
    }//GEN-LAST:event_jDashboardButtonAdmin14ActionPerformed

    private void jDashboardButtonAdmin15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin15ActionPerformed
        // Save current values BEFORE clearing
            try {
                lastUpdateFormState = new ExpenseModel(
                    Integer.parseInt(jUpdateFieldID.getText().trim()),
                    jUpdateFieldName.getText().trim(),
                    jUpdateFieldExpenseTitle.getText().trim(),
                    jComboBoxUpdateCategory2.getSelectedItem().toString(),
                    Double.parseDouble(jUpdateFieldAmount.getText().trim()),
                    jUpdateFieldContact.getText().trim(),
                    jUpdateFieldDate.getText().trim()
                );
            } catch (Exception ex) {
                lastUpdateFormState = null; // nothing valid to save
            }

            // Clear fields
            jUpdateFieldID.setText("");
            jUpdateFieldName.setText("");
            jUpdateFieldExpenseTitle.setText("");
            jComboBoxUpdateCategory2.setSelectedIndex(0);
            jUpdateFieldAmount.setText("");
            jUpdateFieldContact.setText("");
            jUpdateFieldDate.setText("");

            JOptionPane.showMessageDialog(
                this,
                "Fields cleared successfully. You can undo this action.",
                "Reset Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
    }//GEN-LAST:event_jDashboardButtonAdmin15ActionPerformed

    private void jDashboardButtonAdmin16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin16ActionPerformed
        resetAddPanel();
    }//GEN-LAST:event_jDashboardButtonAdmin16ActionPerformed

    private void jDashboardButtonAdmin17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin17ActionPerformed
        resetDeletePanel();
    }//GEN-LAST:event_jDashboardButtonAdmin17ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AdminInterface().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jActualUpdateRecordPanelAdmin;
    private javax.swing.JTextField jAddFieldAmount;
    private javax.swing.JTextField jAddFieldContact;
    private javax.swing.JTextField jAddFieldDate;
    private javax.swing.JTextField jAddFieldExpenseTitle;
    private javax.swing.JTextField jAddFieldID;
    private javax.swing.JTextField jAddFieldName;
    private javax.swing.JPanel jAddRecordPanelAdmin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBoxCategory;
    private javax.swing.JComboBox<String> jComboBoxDeleteCategory;
    private javax.swing.JComboBox<String> jComboBoxUpdateCategory;
    private javax.swing.JComboBox<String> jComboBoxUpdateCategory2;
    private javax.swing.JButton jDashboardButtonAdmin;
    private javax.swing.JButton jDashboardButtonAdmin10;
    private javax.swing.JButton jDashboardButtonAdmin11;
    private javax.swing.JButton jDashboardButtonAdmin12;
    private javax.swing.JButton jDashboardButtonAdmin13;
    private javax.swing.JButton jDashboardButtonAdmin14;
    private javax.swing.JButton jDashboardButtonAdmin15;
    private javax.swing.JButton jDashboardButtonAdmin16;
    private javax.swing.JButton jDashboardButtonAdmin17;
    private javax.swing.JButton jDashboardButtonAdmin2;
    private javax.swing.JButton jDashboardButtonAdmin3;
    private javax.swing.JButton jDashboardButtonAdmin4;
    private javax.swing.JButton jDashboardButtonAdmin5;
    private javax.swing.JButton jDashboardButtonAdmin6;
    private javax.swing.JButton jDashboardButtonAdmin7;
    private javax.swing.JButton jDashboardButtonAdmin8;
    private javax.swing.JButton jDashboardButtonAdmin9;
    private javax.swing.JPanel jDashboardPanelAdmin;
    private javax.swing.JTable jDashboardTableAdmin;
    private javax.swing.JButton jDeleteButton;
    private javax.swing.JButton jDeleteButton1;
    private javax.swing.JPanel jDeleteRecordPanelAdmin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jParentPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldDeleteID;
    private javax.swing.JButton jUndoButton;
    private javax.swing.JTextField jUpdateFieldAmount;
    private javax.swing.JTextField jUpdateFieldContact;
    private javax.swing.JTextField jUpdateFieldDate;
    private javax.swing.JTextField jUpdateFieldExpenseTitle;
    private javax.swing.JTextField jUpdateFieldID;
    private javax.swing.JTextField jUpdateFieldName;
    private javax.swing.JPanel jUpdateRecordPanelAdmin;
    private javax.swing.JTextField jUpdateTextFieldIDAdmin;
    // End of variables declaration//GEN-END:variables
}