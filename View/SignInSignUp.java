package View;

import Model.User;
import WritetoCSV.UserManager;

import javax.swing.*;
import java.awt.*;

public class SignInSignUp extends JFrame {
    private UserManager userManager;
    private User currentUser;

    public SignInSignUp() {
        userManager = new UserManager();
        currentUser = null;
        
        setTitle("Parking Management System - Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        showSignInPanel();
        setVisible(true);
    }

    private void showSignInPanel() {
        getContentPane().removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Sign In Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton signInButton = new JButton("Sign In");
        panel.add(signInButton, gbc);

        // Sign Up Button
        gbc.gridx = 1;
        JButton signUpButton = new JButton("Sign Up");
        panel.add(signUpButton, gbc);

        signInButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = userManager.signIn(username, password);
            if (user != null) {
                currentUser = user;
                JOptionPane.showMessageDialog(this, "Welcome, " + user.getFullname() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if ("Admin".equalsIgnoreCase(user.getRole())) {
                    new AdminPanel();
                } else {
                    // Open user panel or main panel
                    openMainPanel(user);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Sign In Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        signUpButton.addActionListener(e -> showSignUpPanel());

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void showSignUpPanel() {
        getContentPane().removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Full Name
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel fullnameLabel = new JLabel("Full Name:");
        panel.add(fullnameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField fullnameField = new JTextField(15);
        panel.add(fullnameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Role Selection
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel roleLabel = new JLabel("Role:");
        panel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"User", "Admin"});
        panel.add(roleCombo, gbc);

        // Sign Up Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton signUpButton = new JButton("Create Account");
        panel.add(signUpButton, gbc);

        // Back Button
        gbc.gridx = 1;
        JButton backButton = new JButton("Back to Sign In");
        panel.add(backButton, gbc);

        signUpButton.addActionListener(e -> {
            String fullname = fullnameField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (fullname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userManager.usernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userManager.signUp(username, password, email, fullname, role)) {
                JOptionPane.showMessageDialog(this, "Account created successfully! Please sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                showSignInPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Sign up failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> showSignInPanel());

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void openMainPanel(User user) {
        MainFrame mainFrame = new MainFrame(user);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignInSignUp());
    }
}
