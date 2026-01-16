/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;
import Model.ExpenseModel;
import Model.CurrentUser;
import Controller.AuthController;
import Model.User;
import Controller.ExpenseController;
import Controller.ExpenseSortController;
import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Collections;
import javax.swing.JOptionPane;

public class AdminInterface extends javax.swing.JFrame 
{
    Queue<ExpenseModel> addQueue = new LinkedList<>();
    Stack<ExpenseModel> deleteStack = new Stack<>();
    private ExpenseModel updatingRecord;
    
    // Stores last state of Update Panel fields for Undo
    private ExpenseModel lastUpdateFormState = null;

    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminInterface.class.getName());

    public AdminInterface() 
    {
        initComponents();
        applyDashboardTableTheme(jDashboardTableAdmin, jScrollPane1);
        applyDashboardTableTheme(jUserRecordTable, jScrollPane5);

        // 🔐 ADMIN ACCESS CHECK (PASTE HERE — IMMEDIATELY AFTER initComponents)
        if (CurrentUser.get() == null ||
            !CurrentUser.get().getRole().equalsIgnoreCase("Admin")) {

            JOptionPane.showMessageDialog(
                this,
                "Unauthorized access!",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE
            );

            dispose();
            return;
        }
        
        loadProfilePanel();
        
        jAddNoteTextField.addFocusListener(new java.awt.event.FocusAdapter() {

        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (jAddNoteTextField.getText().equals("Add a note")) {
                jAddNoteTextField.setText("");
                jAddNoteTextField.setForeground(new Color(170, 170, 170));
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (jAddNoteTextField.getText().trim().isEmpty()) {
                jAddNoteTextField.setText("Add a note");
                jAddNoteTextField.setForeground(new Color(140, 140, 140));
            }
        }
    });
        
        jAddNoteTextField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void save() {
                    User user = CurrentUser.get();
                    if (user == null) return;

                    user.setNote(jAddNoteTextField.getText());
                    AuthController.saveUsers(); // persist immediately
                }

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    save();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    save();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    save();
                }
            }
        );
        
        jAddNoteTextField.addCaretListener(e -> 
        {
            User user = CurrentUser.get();
            if (user != null) {
                user.setNote(jAddNoteTextField.getText());
            }
        });

        jDashboardTableAdmin.setFillsViewportHeight(true);
        jScrollPane1.getViewport().setBackground(new Color(43,46,51));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(33, 34, 35));
        headerRenderer.setForeground(new Color(170, 170, 170));
        headerRenderer.setFont(new Font("Iceberg", Font.BOLD, 20));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jDashboardTableAdmin.getColumnModel().getColumnCount(); i++) {
            jDashboardTableAdmin.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        jDashboardTableAdmin.getTableHeader().setOpaque(false);
        jDashboardTableAdmin.getTableHeader().setReorderingAllowed(false);

        // Loading table
        ExpenseController.loadData();
        loadStudentListToTable(jDashboardTableAdmin, ExpenseController.expenses);
        // Load table
        loadStudentListToTable(
            jDashboardTableAdmin,
            ExpenseController.expenses
        );

        setLocationRelativeTo(null);
        loadUsersToUserRecordTable();
        updateUserCardCountsFromTable();


        jDeleteButton.addActionListener(e -> deleteSelectedRecord());
        
        //Sorting Buttons & Combobox
        jSortButtonAdmin.addActionListener(e -> {

        String sortType = jSortComboBoxAdmin.getSelectedItem().toString();
        String orderType = jSearchComboboxInterface.getSelectedItem().toString();

        // ---- VALIDATION ----
        if (sortType.equals("Default") || orderType.equals("Default")) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a sorting criterion (Name, ID, or Amount) and choose Ascending or Descending before clicking Sort.",
                "Sorting Required",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // ---- APPLY SORT (ASCENDING FIRST) ----
        switch (sortType) {

            case "Sort By Name":
                ExpenseSortController.SelectionSortByName(
                    ExpenseController.expenses
                );
                break;

            case "Sort By ID":
                ExpenseSortController.BubbleSortById(
                    ExpenseController.expenses
                );
                break;

            case "Sort By Amount":
                ExpenseSortController.InsertionSortByAmount(
                    ExpenseController.expenses
                );
                break;
        }

        // ---- DESCENDING HANDLING ----
        if (orderType.equals("Descending")) 
        {
                Collections.reverse(ExpenseController.expenses);
            }

            // ---- REFRESH TABLE ----
            loadStudentListToTable(
                jDashboardTableAdmin,
                ExpenseController.expenses
            );

            jSearchTextFieldInterface.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void search() {
                    performSearch();
                }

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    search();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    search();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    search();
                }
            }
        );

            loadProfilePanel();
        });
    }

    private void loadProfilePanel() 
    {
        User user = CurrentUser.get();

        // SAFETY CHECK
        if (user == null) return;

        // Username
        jUserNameTextField.setText(user.getUsername());

        // Full name + role
        jNameRoleTextField.setText(
            user.getFirstName() + " " +
            user.getLastName() + " (" +
            user.getRole() + ")"
        );

        // Date joined
        jDateJoinedTextField.setText(
            user.getDateJoined().toString()
        );

        // Note
        jAddNoteTextField.setText(user.getNote());

        // PROFILE IMAGE (important)
        ImageIcon icon = user.getProfileImage();

        if (icon == null) {
            // fallback if image was not serialized
            String path = user.getRole().equalsIgnoreCase("Admin")
                    ? "/logo/AdminLogo.png"
                    : "/logo/UserLogo.png";

            icon = new ImageIcon(getClass().getResource(path));
        }

        jProfileImageLabel.setIcon(icon);
        
        // NOTE PLACEHOLDER
        if (jAddNoteTextField.getText() == null || jAddNoteTextField.getText().trim().isEmpty()) {
            jAddNoteTextField.setText("Add a note");
            jAddNoteTextField.setForeground(new Color(140, 140, 140));
        } else {
            jAddNoteTextField.setForeground(new Color(170, 170, 170));
        }
    }

    private void performSearch() 
    {
        System.out.println("Total expenses = " + ExpenseController.expenses.size());

        String query = jSearchTextFieldInterface.getText().trim();
        String option = jAscendDesendComboBoxAdmin1.getSelectedItem().toString();

        if (query.isEmpty() || option.equals("None")) {
            refreshDashboardTable(ExpenseController.expenses);
            return;
        }

        java.util.List<ExpenseModel> result = new java.util.ArrayList<>();

        if (option.equals("Search by ID")) 
        {

            // allow partial ID search
            for (ExpenseModel exp : ExpenseController.expenses) {
                if (String.valueOf(exp.getId()).contains(query)) {
                    result.add(exp);
                }
            }
        }


        else if (option.equals("Search by Name")) {

            ExpenseSortController.SelectionSortByName(
                ExpenseController.expenses
            );

            for (ExpenseModel exp : ExpenseController.expenses) 
            {
                if (exp.getName().toLowerCase().contains(query.toLowerCase())) {
                    result.add(exp);
                }
            }
        }
        System.out.println("Result size = " + result.size());
        System.out.println("Search option = " + option);
        refreshDashboardTable(result);

    }

    public static void loadStudentListToTable(javax.swing.JTable table, java.util.Collection<ExpenseModel> list)
    {
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
    
    private void refreshDashboardTable(java.util.Collection<ExpenseModel> list) 
    {
        DefaultTableModel model =
            (DefaultTableModel) jDashboardTableAdmin.getModel();
        model.setRowCount(0);

        for (ExpenseModel exp : list) {
            model.addRow(new Object[]{
                exp.getId(),
                exp.getName(),
                exp.getExpenseTitle(),
                exp.getCategory(),
                exp.getAmount(),
                exp.getContact(),
                exp.getDate()
            });
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

        ExpenseController.expenses.add(exp);
        
        addQueue.offer(exp);  

        loadStudentListToTable(jDashboardTableAdmin, ExpenseController.expenses);

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

        for(ExpenseModel e : ExpenseController.expenses){
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

        // CONFIRMATION BOX
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

        ExpenseController.expenses.remove(target);

        loadStudentListToTable(jDashboardTableAdmin, ExpenseController.expenses);

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
        for (ExpenseModel e : ExpenseController.expenses) {
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

        ExpenseController.expenses.add(restored);

        ExpenseController.expenses.sort(
            (a, b) -> Integer.compare(a.getId(), b.getId())
        );

        loadStudentListToTable(jDashboardTableAdmin, ExpenseController.expenses);

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

        for(ExpenseModel e : ExpenseController.expenses){
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
        
        loadStudentListToTable(jDashboardTableAdmin, ExpenseController.expenses);

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
    
    private void updateUserCardCountsFromTable() 
    {

        DefaultTableModel model =
            (DefaultTableModel) jUserRecordTable.getModel();

        int totalUsers = 0;
        int totalAdmins = 0;
        int totalMales = 0;
        int totalFemales = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object roleObj = model.getValueAt(i, 1);
            Object genderObj = model.getValueAt(i, 3);

            if (roleObj == null || genderObj == null) continue;

            String role = roleObj.toString();
            String gender = genderObj.toString();

            if (role.equalsIgnoreCase("User")) totalUsers++;
            if (role.equalsIgnoreCase("Admin")) totalAdmins++;

            if (gender.equalsIgnoreCase("Male")) totalMales++;
            if (gender.equalsIgnoreCase("Female")) totalFemales++;
        }

        jTotalUsersCardLabel.setText(String.valueOf(totalUsers));
        jTotalAdminsCardLabel.setText(String.valueOf(totalAdmins));
        jTotalMalesCardLabel.setText(String.valueOf(totalMales));
        jTotalFemalesCardLabel.setText(String.valueOf(totalFemales));
    }
    private void loadUsersToUserRecordTable() {

        DefaultTableModel model =
            (DefaultTableModel) jUserRecordTable.getModel();

        model.setRowCount(0); // clear table

        for (User u : AuthController.getAllUsers()) {

            Object[] row = {
            u.getUsername(),                              // ID
            u.getRole(),                                  // Role
            u.getFirstName() + " " + u.getLastName(),     // Name
            u.getGender(),                                // Gender
            "-",                                          // Contact (NOT AVAILABLE)
            0,                                            // Total Expenses (NOT AVAILABLE)
            0.0,                                          // Total Amount (NOT AVAILABLE)
            u.getDateJoined()                             // Member Since
        };
            model.addRow(row);
        }
    }
    
    private void applyDashboardTableTheme(JTable table, JScrollPane scrollPane) 
    {

        table.setFillsViewportHeight(true);
        table.setBackground(new Color(43,46,51));
        table.setForeground(new Color(170,170,170));
        table.setFont(new Font("Iceberg", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(60,60,60));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        scrollPane.getViewport().setBackground(new Color(43,46,51));
        scrollPane.setBorder(null);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(33,34,35));
        headerRenderer.setForeground(new Color(170,170,170));
        headerRenderer.setFont(new Font("Iceberg", Font.BOLD, 20));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setReorderingAllowed(false);
    }
    
    private void deleteSelectedUser() 
    {

        int selectedRow = jUserRecordTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a user to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String username =
            jUserRecordTable.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete user:\n\n" + username,
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // ❌ Prevent deleting logged-in admin
        if (CurrentUser.get() != null &&
            CurrentUser.get().getUsername().equalsIgnoreCase(username)) {

            JOptionPane.showMessageDialog(
                this,
                "You cannot delete the currently logged-in account.",
                "Operation Denied",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        AuthController.deleteUserByUsername(username);

        loadUsersToUserRecordTable();
        updateUserCardCountsFromTable();

        JOptionPane.showMessageDialog(
            this,
            "User deleted successfully.",
            "Deleted",
            JOptionPane.INFORMATION_MESSAGE
        );
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new GradientPanel() ;
        jPanel2 = new GradientPanel() ;
        jLabel1 = new javax.swing.JLabel();
        jDashboardButtonAdmin = new RoundedButton("Button Text")
        ;
        jDashboardButtonAdmin7 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin1 = new RoundedButton("Button Text")
        ;
        jDashboardButtonAdmin2 = new RoundedButton("Button Text")
        ;
        jPanel3 = new GradientPanelDark() ;
        jParentPanel = new javax.swing.JPanel();
        jDashboardPanel = new GradientPanelDark() ;
        jLabel27 = new javax.swing.JLabel();
        jUsersCardPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTotalUsersCardLabel = new javax.swing.JLabel();
        jUsersCardPanel2 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jTotalAdminsCardLabel = new javax.swing.JLabel();
        jUsersCardPanel3 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jTotalMalesCardLabel = new javax.swing.JLabel();
        jUsersCardPanel4 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jTotalFemalesCardLabel = new javax.swing.JLabel();
        jUsersCardPanel5 = new javax.swing.JPanel();
        jTotalUsersCardLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jUsersCardPanel6 = new javax.swing.JPanel();
        jTotalUsersCardLabel2 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jUsersCardPanel7 = new javax.swing.JPanel();
        jTotalUsersCardLabel3 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jRecordPanelAdmin = new GradientPanelDark() ;
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jDashboardTableAdmin = new javax.swing.JTable();
        jDashboardTableAdmin.setFillsViewportHeight(true);
        jAddRecordButton = new RoundedButton("Button Text")
        ;
        jDeleteRecordButton = new RoundedButton("Button Text")
        ;
        jUpdateRecord = new RoundedButton("Button Text")
        ;
        jSearchComboboxInterface = new javax.swing.JComboBox<>();
        jSearchTextFieldInterface = new javax.swing.JTextField();
        jAscendDesendComboBoxAdmin1 = new javax.swing.JComboBox<>();
        jSortComboBoxAdmin = new javax.swing.JComboBox<>();
        jSearchButtonInterface = new javax.swing.JButton();
        jSortButtonAdmin = new javax.swing.JButton();
        jUserRecordPanel = new GradientPanelDark() ;
        jLabel28 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jUserRecordTable = new javax.swing.JTable();
        jDeleteUserButton = new RoundedButton("Button Text")
        ;
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
        jProfilePanel = new javax.swing.JPanel();
        jPanel8 = new RoundedPanel(30);
        jProfileImageLabel = new javax.swing.JLabel();
        jUserNameTextField = new javax.swing.JTextField();
        jNameRoleTextField = new javax.swing.JTextField();
        jEditProfileButton = new RoundedButton("Button Text") ;
        jTextField4 = new javax.swing.JTextField();
        jDateJoinedTextField = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jAddNoteTextField = new javax.swing.JTextArea();
        jLogoutButton = new RoundedButton("Button Text") ;
        jPanel6 = new javax.swing.JPanel();
        jRegistrationButton1 = new RoundedButton("Button Text") ;
        jRegistrationButton2 = new RoundedButton("Button Text") ;
        jParentProfilePanel = new javax.swing.JPanel();
        jActivityPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLogsPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();

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
        jDashboardButtonAdmin.setText("      Records");
        jDashboardButtonAdmin.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin.addActionListener(this::jDashboardButtonAdminActionPerformed);

        jDashboardButtonAdmin7.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin7.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin7.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin7.setText("      Profile");
        jDashboardButtonAdmin7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin7.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin7.addActionListener(this::jDashboardButtonAdmin7ActionPerformed);

        jDashboardButtonAdmin1.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin1.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin1.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin1.setText("      Dashboard");
        jDashboardButtonAdmin1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin1.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin1.addActionListener(this::jDashboardButtonAdmin1ActionPerformed);

        jDashboardButtonAdmin2.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin2.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDashboardButtonAdmin2.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin2.setText("      Members");
        jDashboardButtonAdmin2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin2.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jDashboardButtonAdmin2.addActionListener(this::jDashboardButtonAdmin2ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jDashboardButtonAdmin7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 12, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDashboardButtonAdmin1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDashboardButtonAdmin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDashboardButtonAdmin2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jDashboardButtonAdmin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDashboardButtonAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDashboardButtonAdmin2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(400, 400, 400)
                .addComponent(jDashboardButtonAdmin7, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(21, 20, 20));

        jParentPanel.setBackground(new java.awt.Color(21, 20, 20));
        jParentPanel.setLayout(new java.awt.CardLayout());

        jDashboardPanel.setBackground(new java.awt.Color(21, 20, 20));

        jLabel27.setBackground(new java.awt.Color(21, 20, 20));
        jLabel27.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(170, 170, 170));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("DashBoard");

        jUsersCardPanel.setBackground(new java.awt.Color(21, 20, 20));

        jLabel2.setBackground(new java.awt.Color(21, 20, 20));
        jLabel2.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(170, 170, 170));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Total Users");

        jTotalUsersCardLabel.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalUsersCardLabel.setForeground(new java.awt.Color(170, 170, 170));
        jTotalUsersCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalUsersCardLabel.setText("Count");

        javax.swing.GroupLayout jUsersCardPanelLayout = new javax.swing.GroupLayout(jUsersCardPanel);
        jUsersCardPanel.setLayout(jUsersCardPanelLayout);
        jUsersCardPanelLayout.setHorizontalGroup(
            jUsersCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jTotalUsersCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanelLayout.setVerticalGroup(
            jUsersCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanelLayout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTotalUsersCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        jUsersCardPanel2.setBackground(new java.awt.Color(21, 20, 20));

        jLabel32.setBackground(new java.awt.Color(21, 20, 20));
        jLabel32.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(170, 170, 170));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Admins");

        jTotalAdminsCardLabel.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalAdminsCardLabel.setForeground(new java.awt.Color(170, 170, 170));
        jTotalAdminsCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalAdminsCardLabel.setText("Count");

        javax.swing.GroupLayout jUsersCardPanel2Layout = new javax.swing.GroupLayout(jUsersCardPanel2);
        jUsersCardPanel2.setLayout(jUsersCardPanel2Layout);
        jUsersCardPanel2Layout.setHorizontalGroup(
            jUsersCardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jTotalAdminsCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanel2Layout.setVerticalGroup(
            jUsersCardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel2Layout.createSequentialGroup()
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTotalAdminsCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        jUsersCardPanel3.setBackground(new java.awt.Color(21, 20, 20));

        jLabel34.setBackground(new java.awt.Color(21, 20, 20));
        jLabel34.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(170, 170, 170));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("Male");

        jTotalMalesCardLabel.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalMalesCardLabel.setForeground(new java.awt.Color(170, 170, 170));
        jTotalMalesCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalMalesCardLabel.setText("Count");

        javax.swing.GroupLayout jUsersCardPanel3Layout = new javax.swing.GroupLayout(jUsersCardPanel3);
        jUsersCardPanel3.setLayout(jUsersCardPanel3Layout);
        jUsersCardPanel3Layout.setHorizontalGroup(
            jUsersCardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jTotalMalesCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanel3Layout.setVerticalGroup(
            jUsersCardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel3Layout.createSequentialGroup()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTotalMalesCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        jUsersCardPanel4.setBackground(new java.awt.Color(21, 20, 20));

        jLabel36.setBackground(new java.awt.Color(21, 20, 20));
        jLabel36.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(170, 170, 170));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Female");

        jTotalFemalesCardLabel.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalFemalesCardLabel.setForeground(new java.awt.Color(170, 170, 170));
        jTotalFemalesCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalFemalesCardLabel.setText("Count");

        javax.swing.GroupLayout jUsersCardPanel4Layout = new javax.swing.GroupLayout(jUsersCardPanel4);
        jUsersCardPanel4.setLayout(jUsersCardPanel4Layout);
        jUsersCardPanel4Layout.setHorizontalGroup(
            jUsersCardPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jTotalFemalesCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanel4Layout.setVerticalGroup(
            jUsersCardPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel4Layout.createSequentialGroup()
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTotalFemalesCardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        jUsersCardPanel5.setBackground(new java.awt.Color(21, 20, 20));

        jTotalUsersCardLabel1.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalUsersCardLabel1.setForeground(new java.awt.Color(170, 170, 170));
        jTotalUsersCardLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalUsersCardLabel1.setText("Image1");

        jLabel3.setBackground(new java.awt.Color(21, 20, 20));
        jLabel3.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(170, 170, 170));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Members");

        javax.swing.GroupLayout jUsersCardPanel5Layout = new javax.swing.GroupLayout(jUsersCardPanel5);
        jUsersCardPanel5.setLayout(jUsersCardPanel5Layout);
        jUsersCardPanel5Layout.setHorizontalGroup(
            jUsersCardPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(jTotalUsersCardLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanel5Layout.setVerticalGroup(
            jUsersCardPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTotalUsersCardLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        jUsersCardPanel6.setBackground(new java.awt.Color(21, 20, 20));

        jTotalUsersCardLabel2.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalUsersCardLabel2.setForeground(new java.awt.Color(170, 170, 170));
        jTotalUsersCardLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalUsersCardLabel2.setText("Image2");

        jLabel31.setBackground(new java.awt.Color(21, 20, 20));
        jLabel31.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(170, 170, 170));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Activity");

        javax.swing.GroupLayout jUsersCardPanel6Layout = new javax.swing.GroupLayout(jUsersCardPanel6);
        jUsersCardPanel6.setLayout(jUsersCardPanel6Layout);
        jUsersCardPanel6Layout.setHorizontalGroup(
            jUsersCardPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTotalUsersCardLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanel6Layout.setVerticalGroup(
            jUsersCardPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTotalUsersCardLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addContainerGap())
        );

        jUsersCardPanel7.setBackground(new java.awt.Color(21, 20, 20));

        jTotalUsersCardLabel3.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTotalUsersCardLabel3.setForeground(new java.awt.Color(170, 170, 170));
        jTotalUsersCardLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTotalUsersCardLabel3.setText("Image3");

        jLabel33.setBackground(new java.awt.Color(21, 20, 20));
        jLabel33.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(170, 170, 170));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Record");

        javax.swing.GroupLayout jUsersCardPanel7Layout = new javax.swing.GroupLayout(jUsersCardPanel7);
        jUsersCardPanel7.setLayout(jUsersCardPanel7Layout);
        jUsersCardPanel7Layout.setHorizontalGroup(
            jUsersCardPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUsersCardPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTotalUsersCardLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                .addContainerGap())
        );
        jUsersCardPanel7Layout.setVerticalGroup(
            jUsersCardPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUsersCardPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTotalUsersCardLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addContainerGap())
        );

        javax.swing.GroupLayout jDashboardPanelLayout = new javax.swing.GroupLayout(jDashboardPanel);
        jDashboardPanel.setLayout(jDashboardPanelLayout);
        jDashboardPanelLayout.setHorizontalGroup(
            jDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
            .addGroup(jDashboardPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDashboardPanelLayout.createSequentialGroup()
                        .addComponent(jUsersCardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jUsersCardPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jUsersCardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(jUsersCardPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDashboardPanelLayout.createSequentialGroup()
                        .addComponent(jUsersCardPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jUsersCardPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jUsersCardPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDashboardPanelLayout.setVerticalGroup(
            jDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDashboardPanelLayout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jUsersCardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUsersCardPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUsersCardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUsersCardPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addGroup(jDashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jUsersCardPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUsersCardPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUsersCardPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39))
        );

        jParentPanel.add(jDashboardPanel, "card9");

        jRecordPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel4.setBackground(new java.awt.Color(21, 20, 20));
        jLabel4.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(170, 170, 170));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Records");

        jScrollPane1.setBackground(new java.awt.Color(21, 20, 20));
        jScrollPane1.setBorder(null);
        jScrollPane1.setForeground(new java.awt.Color(170, 170, 170));

        jDashboardTableAdmin.setBackground(new java.awt.Color(43, 46, 51));
        jDashboardTableAdmin.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jDashboardTableAdmin.setForeground(new java.awt.Color(170, 170, 170));
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

        jAddRecordButton.setBackground(new java.awt.Color(53, 54, 55));
        jAddRecordButton.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jAddRecordButton.setForeground(new java.awt.Color(170, 170, 170));
        jAddRecordButton.setText("Add Record");
        jAddRecordButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jAddRecordButton.addActionListener(this::jAddRecordButtonActionPerformed);

        jDeleteRecordButton.setBackground(new java.awt.Color(53, 54, 55));
        jDeleteRecordButton.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDeleteRecordButton.setForeground(new java.awt.Color(170, 170, 170));
        jDeleteRecordButton.setText("Delete Record");
        jDeleteRecordButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDeleteRecordButton.addActionListener(this::jDeleteRecordButtonActionPerformed);

        jUpdateRecord.setBackground(new java.awt.Color(53, 54, 55));
        jUpdateRecord.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jUpdateRecord.setForeground(new java.awt.Color(170, 170, 170));
        jUpdateRecord.setText("Update Record");
        jUpdateRecord.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jUpdateRecord.addActionListener(this::jUpdateRecordActionPerformed);

        jSearchComboboxInterface.setBackground(new java.awt.Color(53, 54, 55));
        jSearchComboboxInterface.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jSearchComboboxInterface.setForeground(new java.awt.Color(170, 170, 170));
        jSearchComboboxInterface.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Asending", "Descending" }));
        jSearchComboboxInterface.setBorder(null);
        jSearchComboboxInterface.addActionListener(this::jSearchComboboxInterfaceActionPerformed);

        jSearchTextFieldInterface.setBackground(new java.awt.Color(53, 54, 55));
        jSearchTextFieldInterface.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jSearchTextFieldInterface.setForeground(new java.awt.Color(170, 170, 170));
        jSearchTextFieldInterface.setBorder(null);

        jAscendDesendComboBoxAdmin1.setBackground(new java.awt.Color(53, 54, 55));
        jAscendDesendComboBoxAdmin1.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jAscendDesendComboBoxAdmin1.setForeground(new java.awt.Color(170, 170, 170));
        jAscendDesendComboBoxAdmin1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Search by Name", "Search by ID" }));
        jAscendDesendComboBoxAdmin1.setBorder(null);

        jSortComboBoxAdmin.setBackground(new java.awt.Color(53, 54, 55));
        jSortComboBoxAdmin.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jSortComboBoxAdmin.setForeground(new java.awt.Color(170, 170, 170));
        jSortComboBoxAdmin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None Sort", "Sort By Name", "Sort By ID", "Sort By Amount" }));
        jSortComboBoxAdmin.setBorder(null);
        jSortComboBoxAdmin.addActionListener(this::jSortComboBoxAdminActionPerformed);

        jSearchButtonInterface.setBackground(new java.awt.Color(53, 54, 55));
        jSearchButtonInterface.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jSearchButtonInterface.setForeground(new java.awt.Color(170, 170, 170));
        jSearchButtonInterface.setText("Search");
        jSearchButtonInterface.setBorder(null);
        jSearchButtonInterface.addActionListener(this::jSearchButtonInterfaceActionPerformed);

        jSortButtonAdmin.setBackground(new java.awt.Color(53, 54, 55));
        jSortButtonAdmin.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jSortButtonAdmin.setForeground(new java.awt.Color(170, 170, 170));
        jSortButtonAdmin.setText("Sort");
        jSortButtonAdmin.setBorder(null);

        javax.swing.GroupLayout jRecordPanelAdminLayout = new javax.swing.GroupLayout(jRecordPanelAdmin);
        jRecordPanelAdmin.setLayout(jRecordPanelAdminLayout);
        jRecordPanelAdminLayout.setHorizontalGroup(
            jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jRecordPanelAdminLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jRecordPanelAdminLayout.createSequentialGroup()
                        .addComponent(jSearchComboboxInterface, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSortComboBoxAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSortButtonAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSearchTextFieldInterface, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jAscendDesendComboBoxAdmin1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSearchButtonInterface, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jRecordPanelAdminLayout.createSequentialGroup()
                        .addComponent(jAddRecordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jUpdateRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(jDeleteRecordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );
        jRecordPanelAdminLayout.setVerticalGroup(
            jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSearchButtonInterface, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jAscendDesendComboBoxAdmin1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSearchTextFieldInterface, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jRecordPanelAdminLayout.createSequentialGroup()
                        .addGroup(jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSortComboBoxAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSortButtonAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSearchComboboxInterface, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)))
                .addGap(43, 43, 43)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(jRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAddRecordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDeleteRecordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUpdateRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        jParentPanel.add(jRecordPanelAdmin, "card9");

        jUserRecordPanel.setBackground(new java.awt.Color(21, 20, 20));

        jLabel28.setBackground(new java.awt.Color(21, 20, 20));
        jLabel28.setFont(new java.awt.Font("Iceberg", 0, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(170, 170, 170));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Members");

        jScrollPane5.setBackground(new java.awt.Color(21, 20, 20));
        jScrollPane5.setBorder(null);
        jScrollPane5.setForeground(new java.awt.Color(170, 170, 170));

        jUserRecordTable.setBackground(new java.awt.Color(21, 20, 20));
        jUserRecordTable.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jUserRecordTable.setForeground(new java.awt.Color(170, 170, 170));
        jUserRecordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Role", "Name", "Gender", "Member Since"
            }
        ));
        jScrollPane5.setViewportView(jUserRecordTable);

        jDeleteUserButton.setBackground(new java.awt.Color(53, 54, 55));
        jDeleteUserButton.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jDeleteUserButton.setForeground(new java.awt.Color(170, 170, 170));
        jDeleteUserButton.setText("Delete User");
        jDeleteUserButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDeleteUserButton.addActionListener(this::jDeleteUserButtonActionPerformed);

        javax.swing.GroupLayout jUserRecordPanelLayout = new javax.swing.GroupLayout(jUserRecordPanel);
        jUserRecordPanel.setLayout(jUserRecordPanelLayout);
        jUserRecordPanelLayout.setHorizontalGroup(
            jUserRecordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
            .addGroup(jUserRecordPanelLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 971, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jUserRecordPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jDeleteUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(368, 368, 368))
        );
        jUserRecordPanelLayout.setVerticalGroup(
            jUserRecordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUserRecordPanelLayout.createSequentialGroup()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jDeleteUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        jParentPanel.add(jUserRecordPanel, "card9");

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
        jDashboardButtonAdmin8.setText("Back");
        jDashboardButtonAdmin8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin8.addActionListener(this::jDashboardButtonAdmin8ActionPerformed);

        jDashboardButtonAdmin16.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin16.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin16.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin16.setText("Clear");
        jDashboardButtonAdmin16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDashboardButtonAdmin16.addActionListener(this::jDashboardButtonAdmin16ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDashboardButtonAdmin8, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jDashboardButtonAdmin3, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jAddFieldID, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                    .addComponent(jAddFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                    .addComponent(jAddFieldExpenseTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                    .addComponent(jComboBoxCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jAddFieldAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                    .addComponent(jAddFieldContact, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                    .addComponent(jAddFieldDate, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))))))
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
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jDashboardButtonAdmin8, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDashboardButtonAdmin3, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout jAddRecordPanelAdminLayout = new javax.swing.GroupLayout(jAddRecordPanelAdmin);
        jAddRecordPanelAdmin.setLayout(jAddRecordPanelAdminLayout);
        jAddRecordPanelAdminLayout.setHorizontalGroup(
            jAddRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRecordPanelAdminLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jAddRecordPanelAdminLayout.setVerticalGroup(
            jAddRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 608, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(98, Short.MAX_VALUE))
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
        jDeleteButton1.setText("Back");
        jDeleteButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jDeleteButton1.addActionListener(this::jDeleteButton1ActionPerformed);

        jDashboardButtonAdmin17.setBackground(new java.awt.Color(53, 54, 55));
        jDashboardButtonAdmin17.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jDashboardButtonAdmin17.setForeground(new java.awt.Color(170, 170, 170));
        jDashboardButtonAdmin17.setText("Clear");
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
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldDeleteID, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(jComboBoxDeleteCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jDeleteButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addContainerGap(49, Short.MAX_VALUE)))
        );
        jDeleteRecordPanelAdminLayout.setVerticalGroup(
            jDeleteRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDeleteRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 712, Short.MAX_VALUE))
            .addGroup(jDeleteRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDeleteRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(81, 81, 81)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(134, Short.MAX_VALUE)))
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
        jDashboardButtonAdmin10.setText("Back");
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
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDashboardButtonAdmin10, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDashboardButtonAdmin11, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jDashboardButtonAdmin5, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jUpdateTextFieldIDAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                                    .addComponent(jComboBoxUpdateCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
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
                    .addComponent(jDashboardButtonAdmin10, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(jDashboardButtonAdmin5, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout jUpdateRecordPanelAdminLayout = new javax.swing.GroupLayout(jUpdateRecordPanelAdmin);
        jUpdateRecordPanelAdmin.setLayout(jUpdateRecordPanelAdminLayout);
        jUpdateRecordPanelAdminLayout.setHorizontalGroup(
            jUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jUpdateRecordPanelAdminLayout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
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
                .addGap(0, 125, Short.MAX_VALUE))
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
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
            .addGroup(jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jActualUpdateRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(49, Short.MAX_VALUE)))
        );
        jActualUpdateRecordPanelAdminLayout.setVerticalGroup(
            jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jActualUpdateRecordPanelAdminLayout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 712, Short.MAX_VALUE))
            .addGroup(jActualUpdateRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jActualUpdateRecordPanelAdminLayout.createSequentialGroup()
                    .addGap(81, 81, 81)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(87, Short.MAX_VALUE)))
        );

        jParentPanel.add(jActualUpdateRecordPanelAdmin, "card6");

        jProfilePanel.setBackground(new java.awt.Color(21, 20, 20));

        jPanel8.setBackground(new java.awt.Color(53, 54, 55));

        jProfileImageLabel.setBackground(new java.awt.Color(21, 20, 20));
        jProfileImageLabel.setFont(new java.awt.Font("Iceberg", 1, 12)); // NOI18N
        jProfileImageLabel.setForeground(new java.awt.Color(170, 170, 170));
        jProfileImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jProfileImageLabel.setPreferredSize(new java.awt.Dimension(120, 120));

        jUserNameTextField.setBackground(new java.awt.Color(53, 54, 55));
        jUserNameTextField.setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N
        jUserNameTextField.setForeground(new java.awt.Color(170, 170, 170));
        jUserNameTextField.setText("User Name");
        jUserNameTextField.setBorder(null);

        jNameRoleTextField.setBackground(new java.awt.Color(53, 54, 55));
        jNameRoleTextField.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jNameRoleTextField.setForeground(new java.awt.Color(170, 170, 170));
        jNameRoleTextField.setText("Name");
        jNameRoleTextField.setBorder(null);
        jNameRoleTextField.addActionListener(this::jNameRoleTextFieldActionPerformed);

        jEditProfileButton.setBackground(new java.awt.Color(71, 71, 71));
        jEditProfileButton.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jEditProfileButton.setForeground(new java.awt.Color(170, 170, 170));
        jEditProfileButton.setText("Edit Profile");
        jEditProfileButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jEditProfileButton.addActionListener(this::jEditProfileButtonActionPerformed);

        jTextField4.setBackground(new java.awt.Color(53, 54, 55));
        jTextField4.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(170, 170, 170));
        jTextField4.setText("Member Since");
        jTextField4.setBorder(null);
        jTextField4.addActionListener(this::jTextField4ActionPerformed);

        jDateJoinedTextField.setBackground(new java.awt.Color(53, 54, 55));
        jDateJoinedTextField.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jDateJoinedTextField.setForeground(new java.awt.Color(170, 170, 170));
        jDateJoinedTextField.setText("Mar 10, 2023");
        jDateJoinedTextField.setBorder(null);
        jDateJoinedTextField.addActionListener(this::jDateJoinedTextFieldActionPerformed);

        jTextField6.setBackground(new java.awt.Color(53, 54, 55));
        jTextField6.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTextField6.setForeground(new java.awt.Color(170, 170, 170));
        jTextField6.setText("Note (Only visible to you)");
        jTextField6.setBorder(null);
        jTextField6.addActionListener(this::jTextField6ActionPerformed);

        jScrollPane2.setBorder(null);

        jAddNoteTextField.setBackground(new java.awt.Color(53, 54, 55));
        jAddNoteTextField.setColumns(20);
        jAddNoteTextField.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jAddNoteTextField.setForeground(new java.awt.Color(140, 140, 140));
        jAddNoteTextField.setRows(5);
        jAddNoteTextField.setText("Click to add a note");
        jAddNoteTextField.setBorder(null);
        jScrollPane2.setViewportView(jAddNoteTextField);

        jLogoutButton.setBackground(new java.awt.Color(71, 71, 71));
        jLogoutButton.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jLogoutButton.setForeground(new java.awt.Color(170, 170, 170));
        jLogoutButton.setText("Log Out");
        jLogoutButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLogoutButton.addActionListener(this::jLogoutButtonActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jProfileImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDateJoinedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jEditProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jNameRoleTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jUserNameTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jProfileImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jUserNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNameRoleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jEditProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateJoinedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(21, 20, 20));

        jRegistrationButton1.setBackground(new java.awt.Color(21, 20, 20));
        jRegistrationButton1.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jRegistrationButton1.setForeground(new java.awt.Color(170, 170, 170));
        jRegistrationButton1.setText("Activity");
        jRegistrationButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jRegistrationButton1.addActionListener(this::jRegistrationButton1ActionPerformed);

        jRegistrationButton2.setBackground(new java.awt.Color(21, 20, 20));
        jRegistrationButton2.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jRegistrationButton2.setForeground(new java.awt.Color(170, 170, 170));
        jRegistrationButton2.setText("Logs");
        jRegistrationButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jRegistrationButton2.addActionListener(this::jRegistrationButton2ActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRegistrationButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRegistrationButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(349, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jRegistrationButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jRegistrationButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jParentProfilePanel.setBackground(new java.awt.Color(21, 20, 20));
        jParentProfilePanel.setForeground(new java.awt.Color(170, 170, 170));
        jParentProfilePanel.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jParentProfilePanel.setLayout(new java.awt.CardLayout());

        jActivityPanel.setBackground(new java.awt.Color(21, 20, 20));
        jActivityPanel.setForeground(new java.awt.Color(170, 170, 170));
        jActivityPanel.setPreferredSize(new java.awt.Dimension(513, 642));

        jScrollPane3.setBorder(null);

        jTextArea2.setBackground(new java.awt.Color(21, 20, 20));
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTextArea2.setForeground(new java.awt.Color(170, 170, 170));
        jTextArea2.setRows(5);
        jTextArea2.setText("Activities\n");
        jTextArea2.setBorder(null);
        jScrollPane3.setViewportView(jTextArea2);

        javax.swing.GroupLayout jActivityPanelLayout = new javax.swing.GroupLayout(jActivityPanel);
        jActivityPanel.setLayout(jActivityPanelLayout);
        jActivityPanelLayout.setHorizontalGroup(
            jActivityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
        jActivityPanelLayout.setVerticalGroup(
            jActivityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
        );

        jParentProfilePanel.add(jActivityPanel, "card2");

        jLogsPanel.setBackground(new java.awt.Color(21, 20, 20));
        jLogsPanel.setForeground(new java.awt.Color(170, 170, 170));

        jScrollPane4.setBorder(null);

        jTextArea3.setBackground(new java.awt.Color(21, 20, 20));
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Iceberg", 0, 14)); // NOI18N
        jTextArea3.setForeground(new java.awt.Color(170, 170, 170));
        jTextArea3.setRows(5);
        jTextArea3.setText("Logins and Logouts\n");
        jTextArea3.setBorder(null);
        jScrollPane4.setViewportView(jTextArea3);

        javax.swing.GroupLayout jLogsPanelLayout = new javax.swing.GroupLayout(jLogsPanel);
        jLogsPanel.setLayout(jLogsPanelLayout);
        jLogsPanelLayout.setHorizontalGroup(
            jLogsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
        jLogsPanelLayout.setVerticalGroup(
            jLogsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
        );

        jParentProfilePanel.add(jLogsPanel, "card3");

        jSeparator1.setBackground(new java.awt.Color(21, 20, 20));
        jSeparator1.setForeground(new java.awt.Color(170, 170, 170));

        javax.swing.GroupLayout jProfilePanelLayout = new javax.swing.GroupLayout(jProfilePanel);
        jProfilePanel.setLayout(jProfilePanelLayout);
        jProfilePanelLayout.setHorizontalGroup(
            jProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProfilePanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jParentProfilePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jProfilePanelLayout.setVerticalGroup(
            jProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProfilePanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jProfilePanelLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jParentProfilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 47, Short.MAX_VALUE))
        );

        jParentPanel.add(jProfilePanel, "card6");

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
        jParentPanel.add(jRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdminActionPerformed

    private void jDashboardButtonAdmin7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin7ActionPerformed
        loadProfilePanel();
        jParentPanel.removeAll();
        jParentPanel.add(jProfilePanel);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin7ActionPerformed

    private void jAddFieldIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddFieldIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jAddFieldIDActionPerformed

    private void jDashboardButtonAdmin3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin3ActionPerformed
            addRecord();
    }//GEN-LAST:event_jDashboardButtonAdmin3ActionPerformed

    private void jTextFieldDeleteIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDeleteIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDeleteIDActionPerformed

    private void jDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jDeleteButtonActionPerformed

    private void jDashboardButtonAdmin8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin8ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin8ActionPerformed

    private void jDashboardButtonAdmin9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin9ActionPerformed
        undoDelete();
    }//GEN-LAST:event_jDashboardButtonAdmin9ActionPerformed

    private void jDeleteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteButton1ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jRecordPanelAdmin);
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
        for(ExpenseModel e : ExpenseController.expenses){
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
        jParentPanel.add(jRecordPanelAdmin);
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
        jParentPanel.add(jRecordPanelAdmin);
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

    private void jAddRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddRecordButtonActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jAddRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jAddRecordButtonActionPerformed

    private void jDeleteRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteRecordButtonActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDeleteRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDeleteRecordButtonActionPerformed

    private void jUpdateRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUpdateRecordActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jUpdateRecordPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jUpdateRecordActionPerformed

    private void jSortComboBoxAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSortComboBoxAdminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jSortComboBoxAdminActionPerformed

    private void jNameRoleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNameRoleTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNameRoleTextFieldActionPerformed

    private void jEditProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditProfileButtonActionPerformed
        User current = CurrentUser.get();
        if (current == null) return;

        // 🔥 GET THE REAL STORED USER (THIS IS THE FIX)
        User storedUser = AuthController.getUserByUsername(current.getUsername());

        RegistrationInterface ri = new RegistrationInterface(storedUser);
        ri.setLocationRelativeTo(null);   // center window
        ri.setVisible(true);

        dispose();
    }//GEN-LAST:event_jEditProfileButtonActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jDateJoinedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDateJoinedTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jDateJoinedTextFieldActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jRegistrationButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRegistrationButton1ActionPerformed
        jParentProfilePanel.removeAll();
        jParentProfilePanel.add(jActivityPanel);
        jParentProfilePanel.repaint();
        jParentProfilePanel.revalidate();
    }//GEN-LAST:event_jRegistrationButton1ActionPerformed

    private void jRegistrationButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRegistrationButton2ActionPerformed
        jParentProfilePanel.removeAll();
        jParentProfilePanel.add(jLogsPanel);
        jParentProfilePanel.repaint();
        jParentProfilePanel.revalidate();
    }//GEN-LAST:event_jRegistrationButton2ActionPerformed

    private void jLogoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLogoutButtonActionPerformed
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        // CLEAR SESSION
        CurrentUser.clear();

        // BACK TO LOGIN
        new LoginRegistrationInterface().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLogoutButtonActionPerformed

    private void jSearchComboboxInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSearchComboboxInterfaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jSearchComboboxInterfaceActionPerformed

    private void jSearchButtonInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSearchButtonInterfaceActionPerformed
        performSearch();
    }//GEN-LAST:event_jSearchButtonInterfaceActionPerformed

    private void jDashboardButtonAdmin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin1ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jDashboardPanel);
        jParentPanel.repaint();
        jParentPanel.revalidate();
        updateUserCardCountsFromTable();
    }//GEN-LAST:event_jDashboardButtonAdmin1ActionPerformed

    private void jDashboardButtonAdmin2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin2ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jUserRecordPanel);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin2ActionPerformed

    private void jDeleteUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteUserButtonActionPerformed
        deleteSelectedUser();
    }//GEN-LAST:event_jDeleteUserButtonActionPerformed

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
    private javax.swing.JPanel jActivityPanel;
    private javax.swing.JPanel jActualUpdateRecordPanelAdmin;
    private javax.swing.JTextField jAddFieldAmount;
    private javax.swing.JTextField jAddFieldContact;
    private javax.swing.JTextField jAddFieldDate;
    private javax.swing.JTextField jAddFieldExpenseTitle;
    private javax.swing.JTextField jAddFieldID;
    private javax.swing.JTextField jAddFieldName;
    private javax.swing.JTextArea jAddNoteTextField;
    private javax.swing.JButton jAddRecordButton;
    private javax.swing.JPanel jAddRecordPanelAdmin;
    private javax.swing.JComboBox<String> jAscendDesendComboBoxAdmin1;
    private javax.swing.JComboBox<String> jComboBoxCategory;
    private javax.swing.JComboBox<String> jComboBoxDeleteCategory;
    private javax.swing.JComboBox<String> jComboBoxUpdateCategory;
    private javax.swing.JComboBox<String> jComboBoxUpdateCategory2;
    private javax.swing.JButton jDashboardButtonAdmin;
    private javax.swing.JButton jDashboardButtonAdmin1;
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
    private javax.swing.JButton jDashboardButtonAdmin5;
    private javax.swing.JButton jDashboardButtonAdmin7;
    private javax.swing.JButton jDashboardButtonAdmin8;
    private javax.swing.JButton jDashboardButtonAdmin9;
    private javax.swing.JPanel jDashboardPanel;
    private javax.swing.JTable jDashboardTableAdmin;
    private javax.swing.JTextField jDateJoinedTextField;
    private javax.swing.JButton jDeleteButton;
    private javax.swing.JButton jDeleteButton1;
    private javax.swing.JButton jDeleteRecordButton;
    private javax.swing.JPanel jDeleteRecordPanelAdmin;
    private javax.swing.JButton jDeleteUserButton;
    private javax.swing.JButton jEditProfileButton;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLogoutButton;
    private javax.swing.JPanel jLogsPanel;
    private javax.swing.JTextField jNameRoleTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jParentPanel;
    private javax.swing.JPanel jParentProfilePanel;
    private javax.swing.JLabel jProfileImageLabel;
    private javax.swing.JPanel jProfilePanel;
    private javax.swing.JPanel jRecordPanelAdmin;
    private javax.swing.JButton jRegistrationButton1;
    private javax.swing.JButton jRegistrationButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton jSearchButtonInterface;
    private javax.swing.JComboBox<String> jSearchComboboxInterface;
    private javax.swing.JTextField jSearchTextFieldInterface;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jSortButtonAdmin;
    private javax.swing.JComboBox<String> jSortComboBoxAdmin;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextFieldDeleteID;
    private javax.swing.JLabel jTotalAdminsCardLabel;
    private javax.swing.JLabel jTotalFemalesCardLabel;
    private javax.swing.JLabel jTotalMalesCardLabel;
    private javax.swing.JLabel jTotalUsersCardLabel;
    private javax.swing.JLabel jTotalUsersCardLabel1;
    private javax.swing.JLabel jTotalUsersCardLabel2;
    private javax.swing.JLabel jTotalUsersCardLabel3;
    private javax.swing.JTextField jUpdateFieldAmount;
    private javax.swing.JTextField jUpdateFieldContact;
    private javax.swing.JTextField jUpdateFieldDate;
    private javax.swing.JTextField jUpdateFieldExpenseTitle;
    private javax.swing.JTextField jUpdateFieldID;
    private javax.swing.JTextField jUpdateFieldName;
    private javax.swing.JButton jUpdateRecord;
    private javax.swing.JPanel jUpdateRecordPanelAdmin;
    private javax.swing.JTextField jUpdateTextFieldIDAdmin;
    private javax.swing.JTextField jUserNameTextField;
    private javax.swing.JPanel jUserRecordPanel;
    private javax.swing.JTable jUserRecordTable;
    private javax.swing.JPanel jUsersCardPanel;
    private javax.swing.JPanel jUsersCardPanel1;
    private javax.swing.JPanel jUsersCardPanel2;
    private javax.swing.JPanel jUsersCardPanel3;
    private javax.swing.JPanel jUsersCardPanel4;
    private javax.swing.JPanel jUsersCardPanel5;
    private javax.swing.JPanel jUsersCardPanel6;
    private javax.swing.JPanel jUsersCardPanel7;
    // End of variables declaration//GEN-END:variables
}