/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;
import Model.User;
import Model.CurrentUser;
import Controller.AuthController;
import java.time.LocalDate;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author erick
 */
public class RegistrationInterface extends javax.swing.JFrame 
{
    private User editingUser = null;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegistrationInterface.class.getName());
    
    private boolean editMode = false;
    
    
    // ===== FLAG =====
    private boolean isCustomImageSelected = false;

    public RegistrationInterface() 
    {
        initComponents();
        addHoverEffect();

        /* 1️⃣ Force dark UI + arrow FIRST */
        makeComboBoxDark(jRegistrationRoleComboBox);
        makeComboBoxDark(jMonthComboBox);
        makeComboBoxDark(jDayComboBox);
        makeComboBoxDark(jYearComboBox);
        makeComboBoxDark(jRegistrationGenderComboBox);

        /* 2️⃣ Renderer (text colors for collapsed + popup) */
        jRegistrationRoleComboBox.setRenderer(darkComboRenderer);
        jMonthComboBox.setRenderer(darkComboRenderer);
        jDayComboBox.setRenderer(darkComboRenderer);
        jYearComboBox.setRenderer(darkComboRenderer);
        jRegistrationGenderComboBox.setRenderer(darkComboRenderer);

        /* 3️⃣ Popup background ONLY */
        fixComboPopupColors(jRegistrationRoleComboBox);
        fixComboPopupColors(jMonthComboBox);
        fixComboPopupColors(jDayComboBox);
        fixComboPopupColors(jYearComboBox);
        fixComboPopupColors(jRegistrationGenderComboBox);
        
        /* CheckBox Color */
        makeCheckBoxDark(jFirstPasswordCheckBox);
        makeCheckBoxDark(jConfirmPasswordCheckBox);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        
        loadDefaultProfileImage();
        addListeners();
    }
    
    public RegistrationInterface(User user) 
    {
        initComponents();
        setLocationRelativeTo(null);
        this.editingUser = user;
        loadUserForEdit();
    }

    
    private void loadDefaultProfileImage() 
    {
        java.net.URL location = getClass().getResource("/logo/UserLogo.png");

        if (location == null) {
            JOptionPane.showMessageDialog(this, "UserLogo.png not found!");
            return;
        }

        ImageIcon icon = new ImageIcon(location);
        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        jProfileImageLabel.setIcon(new ImageIcon(img));
    }
    
    private void addListeners() 
    {
        // SHOW PASSWORD (MAIN)
        jFirstPasswordCheckBox.addActionListener(e ->
            jFirstPasswordTextField.setEchoChar(
                jFirstPasswordCheckBox.isSelected() ? (char) 0 : '•'
            )
        );

        // SHOW PASSWORD (CONFIRM)
        jConfirmPasswordCheckBox.addActionListener(e ->
            jConfirmPasswordTextField.setEchoChar(
                jConfirmPasswordCheckBox.isSelected() ? (char) 0 : '•'
            )
        );

        // ROLE CHANGE → PROFILE IMAGE (ONLY IF NO CUSTOM IMAGE)
        jRegistrationRoleComboBox.addActionListener(e -> {
            if (isCustomImageSelected) return;

            String role = jRegistrationRoleComboBox.getSelectedItem().toString();
            String imgPath = null;

            if (role.equals("Admin")) imgPath = "/logo/AdminLogo.png";
            else if (role.equals("User")) imgPath = "/logo/UserLogo.png";

            if (imgPath != null) {
                ImageIcon icon = new ImageIcon(getClass().getResource(imgPath));
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                jProfileImageLabel.setIcon(new ImageIcon(img));
            }
        });

        // PROFILE IMAGE UPLOAD
        jProfileImageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(
                    new FileNameExtensionFilter("Images", "jpg", "png")
                );

                if (chooser.showOpenDialog(RegistrationInterface.this)
                        == JFileChooser.APPROVE_OPTION) {

                    File file = chooser.getSelectedFile();
                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    jProfileImageLabel.setIcon(new ImageIcon(img));
                    isCustomImageSelected = true;
                }
            }
        });

        // REGISTER BUTTON
        jRegistrationButton.addActionListener(e -> confirmAndRegister());


        // BACK BUTTON
        jRegistrationBackButton.addActionListener(e -> 
        {

            if (hasUnsavedData()) {

                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to go back?",
                    "Confirm Navigation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    new LoginRegistrationInterface().setVisible(true);
                    dispose();
                }

            } else {
                new LoginRegistrationInterface().setVisible(true);
                dispose();
            }
        });

    }
    
    private Font loadIcebergFont(float size) 
    {
        try {
            Font font = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/fonts/Iceberg-Regular.ttf")
            );
            return font.deriveFont(size);
        } catch (Exception e) {
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }
    
    private boolean registerUser() 
    {

        StringBuilder missing = new StringBuilder();
        StringBuilder invalid = new StringBuilder();

        boolean hasMissing = false;
        boolean hasInvalid = false;

        /* ===== PERSONAL DETAILS ===== */
        String username = jUserNameTextField.getText().trim();

        // Empty check First
        if (username.isEmpty()) {
            missing.append("• Username\n");
            hasMissing = true;
        } 
        // Duplicate second 
        else if (editingUser == null && AuthController.usernameExists(username)) 
        {
            JOptionPane.showMessageDialog(
                this,
                "Username already exists. Please choose another one.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // First Name
        String firstName = jFirstNameTextField.getText().trim();

        if (firstName.isEmpty()) {
            missing.append("• First Name\n");
            hasMissing = true;
        } else {
            boolean isValid = true;

            if (firstName.length() < 2) {
                isValid = false;
            } else {
                for (int i = 0; i < firstName.length(); i++) {
                    char ch = firstName.charAt(i);
                    if (!Character.isLetter(ch) && ch != ' ') {
                        isValid = false;
                        break;
                    }
                }
            }

            if (!isValid) {
                invalid.append("• First Name should contain only alphabetic characters.\n");
                hasInvalid = true;
            }
        }

        // Last Name
        String lastName = jLastNameTextField.getText().trim();

        if (lastName.isEmpty()) {
            missing.append("• Last Name\n");
            hasMissing = true;
        } else {
            boolean isValid = true;

            if (lastName.length() < 2) {
                isValid = false;
            } else {
                for (int i = 0; i < lastName.length(); i++) {
                    char ch = lastName.charAt(i);
                    if (!Character.isLetter(ch) && ch != ' ') {
                        isValid = false;
                        break;
                    }
                }
            }

            if (!isValid) {
                invalid.append("• Last Name should contain only alphabetic characters.\n");
                hasInvalid = true;
            }
        }

        // Email
        String email = jEmailAddressTextField.getText().trim();

        if (email.isEmpty()) {
            missing.append("• Email Address\n");
            hasMissing = true;
        } else {
            boolean isValid = true;

            if (!email.contains("@") || !email.contains(".")) {
                isValid = false;
            } else if (email.startsWith("@") || email.endsWith("@")) {
                isValid = false;
            } else if (email.startsWith(".") || email.endsWith(".")) {
                isValid = false;
            } else if (email.indexOf("@") > email.lastIndexOf(".")) {
                isValid = false;
            }

            if (!isValid) {
                invalid.append("• Please enter a valid Email Address (e.g., username@gmail.com).\n");
                hasInvalid = true;
            }
        }

        // Contact Number
        String contact = jContactNumberTextField.getText().trim();

        if (contact.isEmpty()) {
            missing.append("• Contact Number\n");
            hasMissing = true;
        } else {
            boolean isValid = true;

            if (contact.length() != 10) {
                isValid = false;
            } else {
                for (int i = 0; i < contact.length(); i++) {
                    if (!Character.isDigit(contact.charAt(i))) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (!isValid) {
                invalid.append("• Contact Number must contain exactly 10 numeric digits.\n");
                hasInvalid = true;
            }
        }

        // Date of Birth
        String month = jMonthComboBox.getSelectedItem().toString();
        String day   = jDayComboBox.getSelectedItem().toString();
        String year  = jYearComboBox.getSelectedItem().toString();

        boolean monthSelected = !month.equals("Month");
        boolean daySelected   = !day.equals("Day");
        boolean yearSelected  = !year.equals("Year");

        if ((monthSelected || daySelected || yearSelected) &&
            !(monthSelected && daySelected && yearSelected)) {

            StringBuilder missingParts = new StringBuilder();

            if (!monthSelected) missingParts.append("Month, ");
            if (!daySelected)   missingParts.append("Day, ");
            if (!yearSelected)  missingParts.append("Year, ");

            // remove last ", "
            missingParts.setLength(missingParts.length() - 2);

            invalid.append("• Please select the complete Date of Birth (" 
                           + missingParts + ").\n");
            hasInvalid = true;
        }

        // Address
        jScrollPane1.setBorder(null);
        jAddressTextArea.setBorder(null);

        if (jAddressTextArea.getText().trim().isEmpty()) {
            missing.append("• Address\n");
            hasMissing = true;
        }

        /* ===== USER CREDENTIALS ===== */

        // Username
        if (jUserNameTextField.getText().trim().isEmpty()) {
            missing.append("• Username\n");
            hasMissing = true;
        }

        // Password
        if (jFirstPasswordTextField.getPassword().length == 0) {
            missing.append("• Password\n");
            hasMissing = true;
        }

        // Confirm Password
        if (jConfirmPasswordTextField.getPassword().length == 0) {
            missing.append("• Confirm Password\n");
            hasMissing = true;
        }

        // Password Match
        if (jFirstPasswordTextField.getPassword().length > 0 &&
            jConfirmPasswordTextField.getPassword().length > 0) {

            String p1 = new String(jFirstPasswordTextField.getPassword());
            String p2 = new String(jConfirmPasswordTextField.getPassword());

            if (!p1.equals(p2)) {
                invalid.append("• Password and Confirm Password do not match.\n");
                hasInvalid = true;
            }
        }
        
        // Gender
        if (jRegistrationGenderComboBox.getSelectedIndex() == 0) {
            missing.append("• Gender\n");
            hasMissing = true;
        }
        
        /* ===== FINAL DIALOG ===== */

        if (hasMissing || hasInvalid) {

        Font icebergFont = loadIcebergFont(14f);
        UIManager.put("OptionPane.messageFont", icebergFont);
        UIManager.put("OptionPane.buttonFont", icebergFont);

        StringBuilder message = new StringBuilder();
        message.append("<html>");
        message.append("<b>Please review the following issues:</b><br><br>");

        if (hasMissing) {
            message.append("<b>Missing information:</b><br>");
            message.append(missing.toString().replace("\n", "<br>"));
            message.append("<br><br>");
        }

        if (hasInvalid) {
            message.append("<b>Invalid information:</b><br>");
            message.append("<font color='red'>");
            message.append(invalid.toString().replace("\n", "<br>"));
            message.append("</font>");
        }

        message.append("</html>");

        JOptionPane.showMessageDialog(
            this,
            message.toString(),
            "Form Validation Error",
            JOptionPane.WARNING_MESSAGE
        );
        return false;
        }
        return true;
    }
    
    private void showRegistrationSuccess() 
    {

        String username = jUserNameTextField.getText().trim();

        JOptionPane.showMessageDialog(
            this,
            "Thank you for your Registration, " + username,
            "Registration Successful",
            JOptionPane.INFORMATION_MESSAGE
        );

        new LoginRegistrationInterface().setVisible(true);
        dispose();
    }

    
    //Register 
    private void confirmAndRegister() 
    {
        if (editingUser != null) {

        editingUser.setFirstName(jFirstNameTextField.getText().trim());
        editingUser.setLastName(jLastNameTextField.getText().trim());
        editingUser.setEmail(jEmailAddressTextField.getText().trim());
        editingUser.setContactNumber(jContactNumberTextField.getText().trim());
        editingUser.setAddress(jAddressTextArea.getText().trim());

        String dob =
            jMonthComboBox.getSelectedItem() + "-" +
            jDayComboBox.getSelectedItem() + "-" +
            jYearComboBox.getSelectedItem();

        editingUser.setDob(dob);

        if (jFirstPasswordTextField.getPassword().length > 0) {
            editingUser.setPassword(
                new String(jFirstPasswordTextField.getPassword())
            );
        }

        editingUser.setProfileImage(
            (ImageIcon) jProfileImageLabel.getIcon()
        );

        AuthController.saveUsers();
        CurrentUser.set(editingUser);

        JOptionPane.showMessageDialog(this, "Profile updated successfully");

        new AdminInterface().setVisible(true);
        dispose();
        return;
    }

        // Step 1: Validate form first
        if (!registerUser()) {
            return;
        }

        // Step 2: Confirmation panel
        JTextField confirmField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(new JLabel("Are you sure you want to register?\nPlease type \"CONFIRM\":"));
        panel.add(confirmField);

        int choice = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Confirm Registration",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        // Step 3: Check CONFIRM text
        if (!confirmField.getText().equals("CONFIRM")) {
            JOptionPane.showMessageDialog(
                this,
                "Confirmation failed.\nPlease type CONFIRM exactly.",
                "Registration Cancelled",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        String gender = jRegistrationGenderComboBox.getSelectedItem().toString();

        User user = new User(
            jUserNameTextField.getText().trim(),
            new String(jFirstPasswordTextField.getPassword()),
            "User", // 🔒 FORCE ROLE
            jFirstNameTextField.getText().trim(),
            jLastNameTextField.getText().trim(),
            (ImageIcon) jProfileImageLabel.getIcon(),
            LocalDate.now()
        );

        user.setGender(gender);

        AuthController.register(user);
        CurrentUser.set(user);

        // Step 4: Final success
        showRegistrationSuccess();
    }
    
    
    private boolean hasUnsavedData() 
    {

        if (!jFirstNameTextField.getText().trim().isEmpty()) return true;
        if (!jLastNameTextField.getText().trim().isEmpty()) return true;
        if (!jEmailAddressTextField.getText().trim().isEmpty()) return true;
        if (!jContactNumberTextField.getText().trim().isEmpty()) return true;
        if (!jUserNameTextField.getText().trim().isEmpty()) return true;
        if (jFirstPasswordTextField.getPassword().length > 0) return true;
        if (jConfirmPasswordTextField.getPassword().length > 0) return true;
        if (!jAddressTextArea.getText().trim().isEmpty()) return true;

        // ComboBoxes (if changed from default)
        if (jRegistrationRoleComboBox.getSelectedIndex() != 0) return true;
        if (!jMonthComboBox.getSelectedItem().equals("Month")) return true;
        if (!jDayComboBox.getSelectedItem().equals("Day")) return true;
        if (!jYearComboBox.getSelectedItem().equals("Year")) return true;

        return false;
    }


    class RoundedButton extends JButton {

        RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedPanel extends JPanel 
    {

        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());
            g2.fillRoundRect(
                0, 0,
                getWidth(),
                getHeight(),
                radius,
                radius
            );

            g2.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private DefaultListCellRenderer darkComboRenderer = new DefaultListCellRenderer() 
    {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            // ===== COLLAPSED COMBOBOX (selected value) =====
            if (index == -1) {
                label.setBackground(new Color(21, 20, 20));
                label.setForeground(new Color(170, 170, 170));
            }
            // ===== DROPDOWN LIST =====
            else {
                if (isSelected) {
                    label.setBackground(new Color(40, 40, 40));
                } else {
                    label.setBackground(new Color(21, 20, 20));
                }
                label.setForeground(new Color(170, 170, 170));
            }

            // Placeholder slightly dim
            if (value != null &&
                (value.equals("Month") || value.equals("Day") || value.equals("Year")
                 || value.equals("Choose a Role"))) {
                label.setForeground(new Color(120, 120, 120));
            }

            label.setFont(new Font("Iceberg", Font.PLAIN, 12));
            label.setOpaque(true);

            return label;
        }
    };


    
    private void fixComboPopupColors(JComboBox<?> combo) 
    {
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {

                SwingUtilities.invokeLater(() -> {
                    Object child = combo.getUI().getAccessibleChild(combo, 0);

                    if (child instanceof JPopupMenu popup) {
                        for (Component c : popup.getComponents()) {
                            if (c instanceof JScrollPane scroll) {

                                scroll.setBorder(null);
                                scroll.setBackground(new Color(21, 20, 20));
                                scroll.getViewport().setBackground(new Color(21, 20, 20));

                                JList<?> list = (JList<?>) scroll.getViewport().getView();
                                list.setBackground(new Color(21, 20, 20));
                                list.setForeground(Color.BLACK);
                                list.setSelectionBackground(new Color(40, 40, 40));
                                list.setSelectionForeground(Color.BLACK);
                            }
                        }
                    }
                });
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }
    private void makeComboBoxDark(JComboBox<?> combo) 
    {
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);

                        // background
                        g2.setColor(new Color(21, 20, 20));
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        // chevron arrow (V shape)
                        g2.setStroke(new BasicStroke(2f));
                        g2.setColor(new Color(170, 170, 170));

                        int w = getWidth();
                        int h = getHeight();

                        int x1 = w / 2 - 6;
                        int y1 = h / 2 - 2;
                        int x2 = w / 2;
                        int y2 = h / 2 + 4;
                        int x3 = w / 2 + 6;
                        int y3 = h / 2 - 2;

                        g2.drawLine(x1, y1, x2, y2);
                        g2.drawLine(x2, y2, x3, y3);

                        g2.dispose();
                    }
                };
            }

            @Override
            public void paintCurrentValueBackground(
                    Graphics g,
                    Rectangle bounds,
                    boolean hasFocus) {

                g.setColor(new Color(21, 20, 20));
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        combo.setOpaque(false);
        combo.setBackground(new Color(21, 20, 20));
        combo.setForeground(new Color(170, 170, 170));
    }
    private Icon createCheckBoxIcon(boolean checked) {
        return new Icon() {

            private final int SIZE = 14;

            @Override
            public int getIconWidth() {
                return SIZE;
            }

            @Override
            public int getIconHeight() {
                return SIZE;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background (black)
                g2.setColor(new Color(21, 20, 20));
                g2.fillRoundRect(x, y, SIZE, SIZE, 4, 4);

                // Border (gray)
                g2.setColor(new Color(170, 170, 170));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, y, SIZE - 1, SIZE - 1, 4, 4);

                // Checkmark
                if (checked) {
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(x + 3, y + 7, x + 6, y + 10);
                    g2.drawLine(x + 6, y + 10, x + 11, y + 3);
                }

                g2.dispose();
            }
        };
    }

    private void makeCheckBoxDark(JCheckBox checkBox) {
        checkBox.setIcon(createCheckBoxIcon(false));
        checkBox.setSelectedIcon(createCheckBoxIcon(true));

        checkBox.setBackground(new Color(21, 20, 20));
        checkBox.setForeground(new Color(170, 170, 170));

        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
    }
    
    private void addHoverEffect() {

        final Color normalColor = new Color(30, 30, 30);
        final Color hoverColor  = new Color(50, 50, 50);
        final Color pressColor  = new Color(20, 20, 20);

        // Register button
        jRegistrationButton.setBackground(normalColor);
        jRegistrationButton.setForeground(Color.WHITE);
        jRegistrationButton.setFocusPainted(false);

        jRegistrationButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                jRegistrationButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jRegistrationButton.setBackground(normalColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                jRegistrationButton.setBackground(pressColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                jRegistrationButton.setBackground(hoverColor);
            }
        });

        // Back button
        jRegistrationBackButton.setBackground(normalColor);
        jRegistrationBackButton.setForeground(Color.WHITE);
        jRegistrationBackButton.setFocusPainted(false);

        jRegistrationBackButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                jRegistrationBackButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jRegistrationBackButton.setBackground(normalColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                jRegistrationBackButton.setBackground(pressColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                jRegistrationBackButton.setBackground(hoverColor);
            }
        });
    }

    private void loadUserForEdit()
    {
        if (editingUser == null) return;

        // ===== BASIC =====
        jUserNameTextField.setText(editingUser.getUsername());
        jUserNameTextField.setEditable(false);

        jFirstNameTextField.setText(editingUser.getFirstName());
        jLastNameTextField.setText(editingUser.getLastName());
        jEmailAddressTextField.setText(editingUser.getEmail());
        jContactNumberTextField.setText(editingUser.getContactNumber());
        jAddressTextArea.setText(editingUser.getAddress());

        // ===== ROLE =====
        jRegistrationRoleComboBox.setSelectedItem(editingUser.getRole());
        jRegistrationRoleComboBox.setEnabled(false);

        // ===== DOB =====
        if (editingUser.getDob() != null) {
            String[] parts = editingUser.getDob().split("-");
            if (parts.length == 3) {
                jMonthComboBox.setSelectedItem(parts[0]);
                jDayComboBox.setSelectedItem(parts[1]);
                jYearComboBox.setSelectedItem(parts[2]);
            }
        }

        // ===== PROFILE IMAGE =====
        if (editingUser.getProfileImage() != null) {
            jProfileImageLabel.setIcon(editingUser.getProfileImage());
            isCustomImageSelected = true;
        }

        // ===== UI TEXT =====
        jRegistrationButton.setText("Update Profile");

        // Passwords intentionally blank (security)
        jFirstPasswordTextField.setText("");
        jConfirmPasswordTextField.setText("");
    }

    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new RoundedPanel(30);
        jLabel2 = new javax.swing.JLabel();
        jRegistrationButton = new RoundedButton("Button Text") ;
        jPanel3 = new javax.swing.JPanel();
        jFirstNameTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jContactNumberTextField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLastNameTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jEmailAddressTextField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jUserNameTextField = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jFirstPasswordTextField = new javax.swing.JPasswordField();
        jPanel10 = new javax.swing.JPanel();
        jConfirmPasswordTextField = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jFirstPasswordCheckBox = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jRegistrationRoleComboBox = new javax.swing.JComboBox<>();
        jConfirmPasswordCheckBox = new javax.swing.JCheckBox();
        jRegistrationBackButton = new RoundedButton("Button Text") ;
        jPanel12 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAddressTextArea = new javax.swing.JTextArea();
        jPanel13 = new javax.swing.JPanel();
        jMonthComboBox = new javax.swing.JComboBox<>();
        jDayComboBox = new javax.swing.JComboBox<>();
        jYearComboBox = new javax.swing.JComboBox<>();
        jProfileImageLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jGenderPanel = new javax.swing.JPanel();
        jRegistrationGenderComboBox = new javax.swing.JComboBox<>();

        jPasswordField1.setText("jPasswordField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(53, 54, 55));
        jPanel1.setFont(new java.awt.Font("Iceberg", 0, 48)); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(650, 820));

        jPanel2.setBackground(new java.awt.Color(21, 20, 20));

        jLabel2.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(170, 170, 170));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Registration");

        jRegistrationButton.setBackground(new java.awt.Color(71, 71, 71));
        jRegistrationButton.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jRegistrationButton.setForeground(new java.awt.Color(170, 170, 170));
        jRegistrationButton.setText("Register");
        jRegistrationButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jRegistrationButton.addActionListener(this::jRegistrationButtonActionPerformed);

        jPanel3.setBackground(new java.awt.Color(21, 20, 20));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "First Name", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jFirstNameTextField.setBackground(new java.awt.Color(21, 20, 20));
        jFirstNameTextField.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jFirstNameTextField.setForeground(new java.awt.Color(170, 170, 170));
        jFirstNameTextField.setBorder(null);
        jFirstNameTextField.setPreferredSize(new java.awt.Dimension(65, 15));
        jFirstNameTextField.addActionListener(this::jFirstNameTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jFirstNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jFirstNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(21, 20, 20));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Contact Number", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jContactNumberTextField.setBackground(new java.awt.Color(21, 20, 20));
        jContactNumberTextField.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jContactNumberTextField.setForeground(new java.awt.Color(170, 170, 170));
        jContactNumberTextField.setBorder(null);
        jContactNumberTextField.setPreferredSize(new java.awt.Dimension(65, 15));
        jContactNumberTextField.addActionListener(this::jContactNumberTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jContactNumberTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jContactNumberTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(21, 20, 20));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Last Name", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jLastNameTextField.setBackground(new java.awt.Color(21, 20, 20));
        jLastNameTextField.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jLastNameTextField.setForeground(new java.awt.Color(170, 170, 170));
        jLastNameTextField.setBorder(null);
        jLastNameTextField.setPreferredSize(new java.awt.Dimension(65, 15));
        jLastNameTextField.addActionListener(this::jLastNameTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLastNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLastNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel6.setBackground(new java.awt.Color(21, 20, 20));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Email Address", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jEmailAddressTextField.setBackground(new java.awt.Color(21, 20, 20));
        jEmailAddressTextField.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jEmailAddressTextField.setForeground(new java.awt.Color(170, 170, 170));
        jEmailAddressTextField.setBorder(null);
        jEmailAddressTextField.setPreferredSize(new java.awt.Dimension(65, 15));
        jEmailAddressTextField.addActionListener(this::jEmailAddressTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jEmailAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jEmailAddressTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(21, 20, 20));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "User Name", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N
        jPanel7.setPreferredSize(new java.awt.Dimension(157, 50));

        jUserNameTextField.setBackground(new java.awt.Color(21, 20, 20));
        jUserNameTextField.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jUserNameTextField.setForeground(new java.awt.Color(170, 170, 170));
        jUserNameTextField.setBorder(null);
        jUserNameTextField.setPreferredSize(new java.awt.Dimension(65, 15));
        jUserNameTextField.addActionListener(this::jUserNameTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jUserNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jUserNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(21, 20, 20));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Password", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jFirstPasswordTextField.setBackground(new java.awt.Color(21, 20, 20));
        jFirstPasswordTextField.setForeground(new java.awt.Color(170, 170, 170));
        jFirstPasswordTextField.setBorder(null);
        jFirstPasswordTextField.addActionListener(this::jFirstPasswordTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jFirstPasswordTextField, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jFirstPasswordTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel10.setBackground(new java.awt.Color(21, 20, 20));
        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Confirm Password", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jConfirmPasswordTextField.setBackground(new java.awt.Color(21, 20, 20));
        jConfirmPasswordTextField.setForeground(new java.awt.Color(170, 170, 170));
        jConfirmPasswordTextField.setBorder(null);
        jConfirmPasswordTextField.addActionListener(this::jConfirmPasswordTextFieldActionPerformed);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jConfirmPasswordTextField, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jConfirmPasswordTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jLabel1.setBackground(new java.awt.Color(53, 54, 55));
        jLabel1.setFont(new java.awt.Font("Iceberg", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(170, 170, 170));
        jLabel1.setText("Personal  Details");

        jLabel3.setBackground(new java.awt.Color(53, 54, 55));
        jLabel3.setFont(new java.awt.Font("Iceberg", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(170, 170, 170));
        jLabel3.setText("User Credentials");

        jFirstPasswordCheckBox.setBackground(new java.awt.Color(21, 20, 20));
        jFirstPasswordCheckBox.setFont(new java.awt.Font("Iceberg", 0, 10)); // NOI18N
        jFirstPasswordCheckBox.setForeground(new java.awt.Color(170, 170, 170));
        jFirstPasswordCheckBox.setText("Show Password");
        jFirstPasswordCheckBox.setBorder(null);
        jFirstPasswordCheckBox.addActionListener(this::jFirstPasswordCheckBoxActionPerformed);

        jPanel11.setBackground(new java.awt.Color(21, 20, 20));
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Role", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N
        jPanel11.setPreferredSize(new java.awt.Dimension(157, 50));

        jRegistrationRoleComboBox.setBackground(new java.awt.Color(21, 20, 20));
        jRegistrationRoleComboBox.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jRegistrationRoleComboBox.setForeground(new java.awt.Color(170, 170, 170));
        jRegistrationRoleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose a Role", "Admin", "User" }));
        jRegistrationRoleComboBox.setBorder(null);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jRegistrationRoleComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 151, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jRegistrationRoleComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        jConfirmPasswordCheckBox.setBackground(new java.awt.Color(21, 20, 20));
        jConfirmPasswordCheckBox.setFont(new java.awt.Font("Iceberg", 0, 10)); // NOI18N
        jConfirmPasswordCheckBox.setForeground(new java.awt.Color(170, 170, 170));
        jConfirmPasswordCheckBox.setText("Show Password");
        jConfirmPasswordCheckBox.setBorder(null);
        jConfirmPasswordCheckBox.addActionListener(this::jConfirmPasswordCheckBoxActionPerformed);

        jRegistrationBackButton.setBackground(new java.awt.Color(71, 71, 71));
        jRegistrationBackButton.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jRegistrationBackButton.setForeground(new java.awt.Color(170, 170, 170));
        jRegistrationBackButton.setText("Back");
        jRegistrationBackButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jRegistrationBackButton.addActionListener(this::jRegistrationBackButtonActionPerformed);

        jPanel12.setBackground(new java.awt.Color(21, 20, 20));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Address", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N

        jScrollPane1.setBorder(null);

        jAddressTextArea.setBackground(new java.awt.Color(21, 20, 20));
        jAddressTextArea.setColumns(20);
        jAddressTextArea.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jAddressTextArea.setForeground(new java.awt.Color(170, 170, 170));
        jAddressTextArea.setRows(5);
        jAddressTextArea.setBorder(null);
        jAddressTextArea.setDisabledTextColor(new java.awt.Color(21, 20, 20));
        jScrollPane1.setViewportView(jAddressTextArea);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        jPanel13.setBackground(new java.awt.Color(21, 20, 20));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Date of Birth", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N
        jPanel13.setPreferredSize(new java.awt.Dimension(157, 50));

        jMonthComboBox.setBackground(new java.awt.Color(21, 20, 20));
        jMonthComboBox.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jMonthComboBox.setForeground(new java.awt.Color(170, 170, 170));
        jMonthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month", "January", "Feburary", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        jMonthComboBox.setBorder(null);
        jMonthComboBox.addActionListener(this::jMonthComboBoxActionPerformed);

        jDayComboBox.setBackground(new java.awt.Color(21, 20, 20));
        jDayComboBox.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jDayComboBox.setForeground(new java.awt.Color(170, 170, 170));
        jDayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day", "01", "0 2", "03", "0 4", "05", "0 6", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        jDayComboBox.setBorder(null);
        jDayComboBox.setPreferredSize(new java.awt.Dimension(85, 20));

        jYearComboBox.setBackground(new java.awt.Color(21, 20, 20));
        jYearComboBox.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jYearComboBox.setForeground(new java.awt.Color(170, 170, 170));
        jYearComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Year", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026" }));
        jYearComboBox.setBorder(null);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jMonthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jYearComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jYearComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMonthComboBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jProfileImageLabel.setBackground(new java.awt.Color(21, 20, 20));
        jProfileImageLabel.setFont(new java.awt.Font("Iceberg", 1, 12)); // NOI18N
        jProfileImageLabel.setForeground(new java.awt.Color(170, 170, 170));
        jProfileImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jProfileImageLabel.setPreferredSize(new java.awt.Dimension(120, 120));

        jLabel4.setBackground(new java.awt.Color(53, 54, 55));
        jLabel4.setFont(new java.awt.Font("Iceberg", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(170, 170, 170));
        jLabel4.setText("Profile Picture");

        jGenderPanel.setBackground(new java.awt.Color(21, 20, 20));
        jGenderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Role", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 12), new java.awt.Color(170, 170, 170))); // NOI18N
        jGenderPanel.setPreferredSize(new java.awt.Dimension(157, 50));

        jRegistrationGenderComboBox.setBackground(new java.awt.Color(21, 20, 20));
        jRegistrationGenderComboBox.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jRegistrationGenderComboBox.setForeground(new java.awt.Color(170, 170, 170));
        jRegistrationGenderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Choose your Gender", "Male", "Female" }));
        jRegistrationGenderComboBox.setBorder(null);

        javax.swing.GroupLayout jGenderPanelLayout = new javax.swing.GroupLayout(jGenderPanel);
        jGenderPanel.setLayout(jGenderPanelLayout);
        jGenderPanelLayout.setHorizontalGroup(
            jGenderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jRegistrationGenderComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jGenderPanelLayout.setVerticalGroup(
            jGenderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jRegistrationGenderComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addComponent(jRegistrationBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRegistrationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(127, 127, 127))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jGenderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jConfirmPasswordCheckBox, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jFirstPasswordCheckBox, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(jProfileImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jProfileImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jFirstPasswordCheckBox)
                        .addGap(7, 7, 7)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jConfirmPasswordCheckBox))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(jGenderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRegistrationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRegistrationBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRegistrationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRegistrationButtonActionPerformed
        confirmAndRegister();
    }//GEN-LAST:event_jRegistrationButtonActionPerformed

    private void jFirstNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFirstNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFirstNameTextFieldActionPerformed

    private void jLastNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLastNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLastNameTextFieldActionPerformed

    private void jEmailAddressTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEmailAddressTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jEmailAddressTextFieldActionPerformed

    private void jFirstPasswordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFirstPasswordCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFirstPasswordCheckBoxActionPerformed

    private void jConfirmPasswordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConfirmPasswordCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jConfirmPasswordCheckBoxActionPerformed

    private void jFirstPasswordTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFirstPasswordTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFirstPasswordTextFieldActionPerformed

    private void jConfirmPasswordTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConfirmPasswordTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jConfirmPasswordTextFieldActionPerformed

    private void jContactNumberTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContactNumberTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jContactNumberTextFieldActionPerformed

    private void jUserNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUserNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUserNameTextFieldActionPerformed

    private void jRegistrationBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRegistrationBackButtonActionPerformed
        new LoginRegistrationInterface().setVisible(true);
        dispose();
    }//GEN-LAST:event_jRegistrationBackButtonActionPerformed

    private void jMonthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMonthComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMonthComboBoxActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new RegistrationInterface().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea jAddressTextArea;
    private javax.swing.JCheckBox jConfirmPasswordCheckBox;
    private javax.swing.JPasswordField jConfirmPasswordTextField;
    private javax.swing.JTextField jContactNumberTextField;
    private javax.swing.JComboBox<String> jDayComboBox;
    private javax.swing.JTextField jEmailAddressTextField;
    private javax.swing.JTextField jFirstNameTextField;
    private javax.swing.JCheckBox jFirstPasswordCheckBox;
    private javax.swing.JPasswordField jFirstPasswordTextField;
    private javax.swing.JPanel jGenderPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jLastNameTextField;
    private javax.swing.JComboBox<String> jMonthComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JLabel jProfileImageLabel;
    private javax.swing.JButton jRegistrationBackButton;
    private javax.swing.JButton jRegistrationButton;
    private javax.swing.JComboBox<String> jRegistrationGenderComboBox;
    private javax.swing.JComboBox<String> jRegistrationRoleComboBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jUserNameTextField;
    private javax.swing.JComboBox<String> jYearComboBox;
    // End of variables declaration//GEN-END:variables
}
