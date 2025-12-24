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

public class AdminInterface extends javax.swing.JFrame {
    ArrayList<ExpenseModel> ExpenseList = new  ArrayList<>();
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminInterface.class.getName());

    public AdminInterface() {
        initComponents();
        prepareInitialData();
        loadStudentListToTable(jDashboardTableAdmin, ExpenseList);
        setLocationRelativeTo(null);   //Centers window
    }
    
    //Method
    private void prepareInitialData()
    {
        ExpenseList.add(new ExpenseModel(23489614, "Aashish Pradhan", "Univercity Lunch", "Food & Groceries", 240.0, "980555721", "2025-12-24"));
        ExpenseList.add(new ExpenseModel(24700971, "Shradhye Maan Shrestha", "Bus Fare", "Transport / Travel", 50.0, "980555778", "2025-11-04"));
        ExpenseList.add(new ExpenseModel(24046582, "Erick Pradhan", "Univercity Fee", "Bills & Utilities", 345000.0, "9811107621", "2025-08-01"));
        ExpenseList.add(new ExpenseModel(24560793, "Saikshya Shrestha","Tribal Rain Concert", "Entertainment / Leisure", 1500.0, "9867667819", "2024-07-17"));
        ExpenseList.add(new ExpenseModel(24704129, "Arnold Pradhan", "Teeth Cleansing", "Health & Personal Care", 4500.0, "9819285542", "2024-01-15"));
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


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new GradientPanel() ;
        jPanel2 = new GradientPanel() ;
        jLabel1 = new javax.swing.JLabel();
        jDashboardButtonAdmin = new RoundedButton("Button Text")
        ;
        jDashboardButtonAdmin2 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin6 = new RoundedButton("Button Text") ;
        jDashboardButtonAdmin7 = new RoundedButton("Button Text") ;
        jPanel3 = new GradientPanelDark() ;
        jParentPanel = new javax.swing.JPanel();
        jDashboardPanelAdmin = new GradientPanelDark() ;
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jDashboardTableAdmin = new javax.swing.JTable();
        jAddRecordPanelAdmin = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jStatisticsPanelAdmin = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jProfilePanelAdmin = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

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
        jDashboardButtonAdmin6.setText("      Statistics");
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jDashboardButtonAdmin7, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(12, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDashboardButtonAdmin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDashboardButtonAdmin2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDashboardButtonAdmin6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jDashboardButtonAdmin6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(395, 395, 395)
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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 299, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
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
                .addGap(17, 17, 17)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if (jDashboardTableAdmin.getColumnModel().getColumnCount() > 0) {
            jDashboardTableAdmin.getColumnModel().getColumn(2).setResizable(false);
        }

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
                        .addGap(79, 79, 79)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
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
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Add Records?");

        javax.swing.GroupLayout jAddRecordPanelAdminLayout = new javax.swing.GroupLayout(jAddRecordPanelAdmin);
        jAddRecordPanelAdmin.setLayout(jAddRecordPanelAdminLayout);
        jAddRecordPanelAdminLayout.setHorizontalGroup(
            jAddRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jAddRecordPanelAdminLayout.createSequentialGroup()
                .addContainerGap(423, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(337, 337, 337))
        );
        jAddRecordPanelAdminLayout.setVerticalGroup(
            jAddRecordPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRecordPanelAdminLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel6)
                .addContainerGap(671, Short.MAX_VALUE))
        );

        jParentPanel.add(jAddRecordPanelAdmin, "card7");

        jStatisticsPanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel2.setBackground(new java.awt.Color(21, 20, 20));
        jLabel2.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(170, 170, 170));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Statistics are here");

        javax.swing.GroupLayout jStatisticsPanelAdminLayout = new javax.swing.GroupLayout(jStatisticsPanelAdmin);
        jStatisticsPanelAdmin.setLayout(jStatisticsPanelAdminLayout);
        jStatisticsPanelAdminLayout.setHorizontalGroup(
            jStatisticsPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStatisticsPanelAdminLayout.createSequentialGroup()
                .addContainerGap(410, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(352, 352, 352))
        );
        jStatisticsPanelAdminLayout.setVerticalGroup(
            jStatisticsPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jStatisticsPanelAdminLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel2)
                .addContainerGap(671, Short.MAX_VALUE))
        );

        jParentPanel.add(jStatisticsPanelAdmin, "card2");

        jProfilePanelAdmin.setBackground(new java.awt.Color(21, 20, 20));

        jLabel3.setBackground(new java.awt.Color(21, 20, 20));
        jLabel3.setFont(new java.awt.Font("Iceberg", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(170, 170, 170));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Profile");

        javax.swing.GroupLayout jProfilePanelAdminLayout = new javax.swing.GroupLayout(jProfilePanelAdmin);
        jProfilePanelAdmin.setLayout(jProfilePanelAdminLayout);
        jProfilePanelAdminLayout.setHorizontalGroup(
            jProfilePanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jProfilePanelAdminLayout.createSequentialGroup()
                .addContainerGap(401, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(355, 355, 355))
        );
        jProfilePanelAdminLayout.setVerticalGroup(
            jProfilePanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProfilePanelAdminLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel3)
                .addContainerGap(674, Short.MAX_VALUE))
        );

        jParentPanel.add(jProfilePanelAdmin, "card3");

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
        jParentPanel.add(jStatisticsPanelAdmin);
        jParentPanel.repaint();
        jParentPanel.revalidate();
    }//GEN-LAST:event_jDashboardButtonAdmin6ActionPerformed

    private void jDashboardButtonAdmin7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDashboardButtonAdmin7ActionPerformed
        jParentPanel.removeAll();
        jParentPanel.add(jProfilePanelAdmin);
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
    private javax.swing.JPanel jAddRecordPanelAdmin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jDashboardButtonAdmin;
    private javax.swing.JButton jDashboardButtonAdmin2;
    private javax.swing.JButton jDashboardButtonAdmin6;
    private javax.swing.JButton jDashboardButtonAdmin7;
    private javax.swing.JPanel jDashboardPanelAdmin;
    private javax.swing.JTable jDashboardTableAdmin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jParentPanel;
    private javax.swing.JPanel jProfilePanelAdmin;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jStatisticsPanelAdmin;
    // End of variables declaration//GEN-END:variables
}
