/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;
import Controller.AuthController;
import java.awt.*;
import javax.swing.*;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author erick
 */
public class AsUserRole extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AsUserRole.class.getName());

    private int loginAttempts = 0;
    private boolean isLocked = false;
    /**
     * Creates new form RoleSelectionFrame
     */
    public AsUserRole() 
    {
        initComponents();
        setLocationRelativeTo(null);   //Centers window
        //Iceberg Font
        try 
        {
            Font iceberg = Font.createFont
            (
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/fonts/Iceberg-Regular.ttf")
            ).deriveFont(Font.PLAIN, 16f);

            UIManager.put("OptionPane.messageFont", iceberg);
            UIManager.put("OptionPane.buttonFont", iceberg);

        } 
        catch (Exception ex) 
        {
            Font fallback = new Font("Arial", Font.PLAIN, 16);
            UIManager.put("OptionPane.messageFont", fallback);
            UIManager.put("OptionPane.buttonFont", fallback);
        }
        
        // Makes ENTER key trigger Login button
        getRootPane().setDefaultButton(jLoginButtonUser);

        jUsernameTextFieldUser.requestFocusInWindow();

        jUsernameTextFieldUser.addActionListener(e -> jLoginButtonUser.doClick());
        jPasswordTextFieldUser.addActionListener(e -> jLoginButtonUser.doClick());

    
        //Action Listener
        jLoginButtonUser.addActionListener(e ->
        {
            String user = jUsernameTextFieldUser.getText().trim();
            String pass = new String(jPasswordTextFieldUser.getPassword()).trim();

            // === EMPTY FIELD VALIDATION ===
            if(user.isEmpty() && pass.isEmpty())
            {
                JOptionPane.showMessageDialog(this,
                        "Please enter your Username and Password!");
                return;
            }

            if(user.isEmpty())
            {
                JOptionPane.showMessageDialog(this,
                        "Please enter your Username!");
                return;
            }

            if(pass.isEmpty())
            {
                JOptionPane.showMessageDialog(this,
                        "Please enter your Password!");
                return;
            }
            String result = AuthController.login(user, pass, "user");

            switch (result)
            {
                case "SUCCESS":
                    loginAttempts = 0;
                    isLocked = false;
                    JOptionPane.showMessageDialog(this,"User Login Successful!");
                    new Dashboard().setVisible(true);
                    dispose();
                    break;

                case "WRONG_USERNAME":
                    JOptionPane.showMessageDialog(this,"Invalid User's Username!");
                    loginAttempts++;
                    checkAttempts();
                    break;

                case "WRONG_PASSWORD":
                    JOptionPane.showMessageDialog(this,"Your password is incorrect, Please try again!");
                    loginAttempts++;
                    checkAttempts();
                    break;

                case "BOTH_WRONG":
                    JOptionPane.showMessageDialog(this,"Invalid credentials, Please try again!");
                    loginAttempts++;
                    checkAttempts();
                    break;
            }
        });
    }
    private void checkAttempts()
    {
        if(loginAttempts < 5 || isLocked)
            return;

        isLocked = true;
        jLoginButtonUser.setEnabled(false);

        int lockSeconds = 30;

        final JDialog dialog = new JDialog(this, "Login Locked", true);

        JPanel panel = new JPanel(new BorderLayout(10,10));

        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");

        JLabel label = new JLabel(
                "Too many attempts! Please wait " + lockSeconds + " seconds!",
                errorIcon,
                SwingConstants.LEFT
        ); 
        //Iceberg Font:
        try {
            Font iceberg = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/Iceberg-Regular.ttf")
            );
            iceberg = iceberg.deriveFont(Font.PLAIN, 16f);
            label.setFont(iceberg);
        } catch (Exception ex) {
            label.setFont(new Font("Arial", Font.PLAIN, 16)); // fallback
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.pack();                 // auto fits content
        dialog.setSize(385, 150);
        dialog.setLocationRelativeTo(this);


        // Timer for countdown
        new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            int timeLeft = lockSeconds;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                timeLeft--;
                label.setText("Too many attempts! Please wait " + timeLeft + " seconds.");

                if(timeLeft <= 0){
                    ((javax.swing.Timer)e.getSource()).stop();
                    dialog.dispose();
                    jLoginButtonUser.setEnabled(true);
                    loginAttempts = 0;
                    isLocked = false;
                }
            }
        }).start();

        dialog.setVisible(true);
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
    //RoundedButtons and Hover effect
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Change color based on state
            if (getModel().isArmed()) {
                g2.setColor(pressColor);     // When clicking
            } 
            else if (getModel().isRollover()) {
                g2.setColor(hoverColor);     // Hover effect
            } 
            else {
                g2.setColor(getBackground()); // Normal
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new RoundedPanel(30);
        jLabel2 = new javax.swing.JLabel();
        jLoginButtonUser = new RoundedButton("Button Text") ;
        jPanel3 = new javax.swing.JPanel();
        jUsernameTextFieldUser = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPasswordTextFieldUser = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Iceberg", 1, 24)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(71, 71, 71));
        jPanel1.setFont(new java.awt.Font("Iceberg", 0, 48)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(21, 20, 20));

        jLabel2.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(170, 170, 170));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Login as User");

        jLoginButtonUser.setBackground(new java.awt.Color(71, 71, 71));
        jLoginButtonUser.setFont(new java.awt.Font("Iceberg", 0, 18)); // NOI18N
        jLoginButtonUser.setForeground(new java.awt.Color(170, 170, 170));
        jLoginButtonUser.setText("Login");
        jLoginButtonUser.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLoginButtonUser.addActionListener(this::jLoginButtonUserActionPerformed);

        jPanel3.setBackground(new java.awt.Color(21, 20, 20));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Username", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 14), new java.awt.Color(170, 170, 170))); // NOI18N

        jUsernameTextFieldUser.setBackground(new java.awt.Color(21, 20, 20));
        jUsernameTextFieldUser.setFont(new java.awt.Font("Iceberg", 0, 12)); // NOI18N
        jUsernameTextFieldUser.setForeground(new java.awt.Color(170, 170, 170));
        jUsernameTextFieldUser.setBorder(null);
        jUsernameTextFieldUser.setPreferredSize(new java.awt.Dimension(65, 15));
        jUsernameTextFieldUser.addActionListener(this::jUsernameTextFieldUserActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jUsernameTextFieldUser, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jUsernameTextFieldUser, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(21, 20, 20));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 170, 170), 1, true), "Password", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Iceberg", 0, 14), new java.awt.Color(170, 170, 170))); // NOI18N

        jPasswordTextFieldUser.setBackground(new java.awt.Color(21, 20, 20));
        jPasswordTextFieldUser.setForeground(new java.awt.Color(170, 170, 170));
        jPasswordTextFieldUser.setBorder(null);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPasswordTextFieldUser, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPasswordTextFieldUser, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 17, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLoginButtonUser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLoginButtonUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
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

    private void jLoginButtonUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLoginButtonUserActionPerformed

    }//GEN-LAST:event_jLoginButtonUserActionPerformed

    private void jUsernameTextFieldUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUsernameTextFieldUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUsernameTextFieldUserActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new AsUserRole().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jLoginButtonUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPasswordField jPasswordTextFieldUser;
    private javax.swing.JTextField jUsernameTextFieldUser;
    // End of variables declaration//GEN-END:variables
}
