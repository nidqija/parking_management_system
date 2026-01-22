package View;
import Controller.Admin;
import java.awt.*;
import javax.swing.*;

public class AdminSignInPanel extends JFrame {


    public AdminSignInPanel(){
        JFrame frame = new JFrame("Admin Sign In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);

    
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        

// ============================== Welcome Label ================================= //

        JLabel welcomeLabel = new JLabel("Welcome to the Admin Sign In Panel");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(welcomeLabel);

        panel.add(Box.createVerticalStrut(20)); 

// ============================== Username & Password Fields =============================//

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameLabel);

        JTextField usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(200, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameField);

        panel.add(Box.createVerticalStrut(20)); 

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordLabel);

        JTextField passwordField = new JTextField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordField);

        panel.add(Box.createVerticalStrut(20)); 
        
        JButton signInButton = new JButton("Sign In");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(signInButton);

        panel.add(Box.createVerticalStrut(20)); 

      


// ============================== Button Actions ================================= //

        signInButton.addActionListener(e->{
           Admin admin = new Admin(usernameField.getText(), passwordField.getText());
           admin.executeSignIn();
           frame.dispose();
        });


       

        

        frame.add(panel);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        new AdminSignInPanel();
    }
}