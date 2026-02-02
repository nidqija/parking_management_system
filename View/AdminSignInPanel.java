package View;
import Model.Admin;
import View.MainFrame;
import java.awt.*;
import javax.swing.*;

public class AdminSignInPanel extends JPanel{


    public AdminSignInPanel(MainFrame mainFrame) {
        

    
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

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(backButton);


        panel.add(Box.createVerticalStrut(20)); 

      


// ============================== Button Actions ================================= //
        backButton.addActionListener(e -> mainFrame.showPage("Homepage"));

        signInButton.addActionListener(e->{
           Admin admin = new Admin(usernameField.getText(), passwordField.getText(), mainFrame);
           admin.executeSignIn();
        
        });


       
        add(panel);
        

       
    }
   
}