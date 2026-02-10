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


        backBtn.addActionListener(e -> {
           mainFrame.showPage("AdminPanel");
       });
       //i want the pages to like switch based on what button is pressed to show each report basically
          
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

       JPanel vehiclePanel = new JPanel();
       JPanel revenuePanel = new JPanel();
       JPanel occupancyPanel = new JPanel();
       JPanel finesPanel = new JPanel();

       contentPanel.add(vehiclePanel, "VEHICLES");
       contentPanel.add(revenuePanel, "REVENUE");
       contentPanel.add(occupancyPanel, "OCCUPANCY");
       contentPanel.add(finesPanel, "FINES");

       



        panel.add(label);
        panel.add(buttonPanel);
        add(contentPanel, BorderLayout.CENTER);
        add(panel);


      }
}
