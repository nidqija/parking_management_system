package View;

import javax.swing.*;



public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        ManageFines manageFines = new ManageFines();


        tabbedPane.addTab("Manage Fines", manageFines);



        add(tabbedPane);
        setVisible(true);
    }

   
}