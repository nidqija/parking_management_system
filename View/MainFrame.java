package View;

import Model.User;
import javax.swing.*;



public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame() {
        this(null);
    }

    public MainFrame(User user) {
        this.currentUser = user;
        
        setTitle("Parking Management System" + (user != null ? " - " + user.getFullname() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        ManageFines manageFines = new ManageFines();


        tabbedPane.addTab("Manage Fines", manageFines);



        add(tabbedPane);
        setVisible(true);
    }

    public User getCurrentUser() {
        return currentUser;
    }

   
}