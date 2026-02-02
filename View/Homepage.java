package View;

import View.MainFrame;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

public class Homepage extends JPanel {

    public Homepage(MainFrame mainFrame) {

        // ======================================== WELCOME LABEL
        // ===========================================//
        setLayout(new CardLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Welcome to the Parking Management System");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        // ======================================== LOGIN BUTTON
        // ========================================== //

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(200, 40));
        loginButton.addActionListener(e -> mainFrame.showPage("AdminSignInPage"));

        // ======================================== SIGNUP BUTTON
        // ========================================= //

        JButton signUpButton = new JButton("Grab a ticket");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(200, 40));
        signUpButton.addActionListener(e -> mainFrame.showPage("EntryPanel"));

        card.add(Box.createVerticalGlue());
        card.add(label);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(signUpButton);
        card.add(Box.createVerticalGlue());

        add(card, "Homepage");
    }
}
