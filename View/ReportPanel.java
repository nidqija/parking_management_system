package View;

import java.awt.*;
import javax.swing.*;

public class ReportPanel extends JPanel {

      private CardLayout cardLayout;
      private JPanel contentPanel;
    


      public ReportPanel(MainFrame mainFrame) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Report Panel - Parking Management System");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton vehiclesBtn   = new JButton("Vehicles List");
        JButton revenueBtn    = new JButton("Revenue Report");
        JButton occupancyBtn  = new JButton("Occupancy Report");
        JButton finesBtn      = new JButton("Outstanding Fines");
        JButton backBtn = new JButton("Back");
        
        JButton[] arrayOfButtons = { vehiclesBtn, revenueBtn, occupancyBtn, finesBtn, backBtn} ;

        for (JButton btn : arrayOfButtons){
          buttonPanel.add(btn);
        }

        vehiclesBtn.addActionListener(e ->
                cardLayout.show(contentPanel, "Vehicles"));

        revenueBtn.addActionListener(e ->
                cardLayout.show(contentPanel, "Revenue"));

        occupancyBtn.addActionListener(e ->
                cardLayout.show(contentPanel, "Occupancy"));

        finesBtn.addActionListener(e ->
                cardLayout.show(contentPanel, "Fines"));

        backBtn.addActionListener(e -> {
           mainFrame.showPage("AdminPanel");
       });

        panel.add(label);
        panel.add(buttonPanel);
        add(panel);

       //i want the pages to like switch based on what button is pressed to show each report basically
          
      cardLayout = new CardLayout();
      contentPanel = new JPanel(cardLayout);

      JPanel vehiclePanel = new JPanel(new BorderLayout());
      JLabel vehicleLabel = new JLabel("List of Vehicles", SwingConstants.CENTER);
      vehicleLabel.setFont(new Font("Arial", Font.BOLD, 20));
      vehiclePanel.add(vehicleLabel, BorderLayout.CENTER);



      


      JPanel revenuePanel = new JPanel(new BorderLayout());
      JLabel revenueLabel = new JLabel("Revenue Report", SwingConstants.CENTER);
      revenueLabel.setFont(new Font("Arial", Font.BOLD, 20));
      revenuePanel.add(revenueLabel, BorderLayout.CENTER);
      

      JPanel occupancyPanel = new JPanel(new BorderLayout());
      JLabel occupancyLabel = new JLabel("Occupancy Report", SwingConstants.CENTER);
      occupancyLabel.setFont(new Font("Arial", Font.BOLD, 20));
      occupancyPanel.add(occupancyLabel, BorderLayout.CENTER);

      JPanel finesPanel = new JPanel(new BorderLayout());
      JLabel finesLabel = new JLabel("Fines Report", SwingConstants.CENTER);
      finesLabel.setFont(new Font("Arial", Font.BOLD, 20));
      finesPanel.add(finesLabel, BorderLayout.CENTER);

      contentPanel.add(vehiclePanel, "Vehicles");
      contentPanel.add(revenuePanel, "Revenue");
      contentPanel.add(occupancyPanel, "Occupancy");
      contentPanel.add(finesPanel, "Fines");

       

      add(contentPanel, BorderLayout.CENTER);
       


      }
}
