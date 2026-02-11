package View;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Model.Vehicle;
import Controller.Fines;
import Controller.Floors;

public class ReportPanel extends JPanel {

      private CardLayout cardLayout;
      private JPanel contentPanel;
      private JTable vehicleTable;
      private DefaultTableModel tableModel;
      private JTable revenueTable;
      private DefaultTableModel revenueTableModel;
      private JTable occupancyTable;
      private DefaultTableModel occupancyTableModel;
      private JTable finesTable;
      private DefaultTableModel finesTableModel;


      

       private void loadVehicleData() {
       tableModel.setRowCount(0); // clear existing rows

        List<Vehicle> vehicles = Vehicle.getVehicleReportList();
        int i = 0;
        for (Vehicle v : vehicles) {
            tableModel.addRow(new Object[]{
                    i + 1,
                    v.getPlateNumber(),
                    v.getType(),
                    v.getTicketNumber() != null ? v.getTicketNumber() : "NULL",
                    v.getSpotID() != null ? v.getSpotID() : "NULL",
                    v.getEntryTime() != null ? v.getEntryTime().toString() : "NULL",
                    v.getExitTime() != null ? v.getExitTime().toString() : "NULL",
                    v.getPaymentStatus() != null ? v.getPaymentStatus() : "NULL"
                    //ternary operator
                    //condition ? value_if_true : value_if_false
            });
            i++; //increment
        }
    }

    private void loadOccupancyData() {
        if (occupancyTableModel == null) return;
        occupancyTableModel.setRowCount(0);
        
        List<Floors> floors = Floors.getFloors();
        
        for (Floors f : floors){
            occupancyTableModel.addRow(new Object[]{
                    f.getFloorNumber(),
                    f.getFloorName(),
                    f.getTotalSpots(f.getFloorNumber()),
                    f.getOccupiedSpots(f.getFloorNumber()),
                    f.getAvailableSpots(f.getFloorNumber()),
                    String.format("%.1f%%", f.floorOccupancyRate(f.getFloorNumber()))

    
            });
        }
    }

    private void loadFinesData() {
        if (finesTableModel == null) return;
        finesTableModel.setRowCount(0);

        // Assuming a method exists to get fines data
        List<Fines> fines = Fines.getFinesReportList();

        for (Fines fine : fines) {
            finesTableModel.addRow(new Object[]{
                    fine.getFineID(),
                    fine.getVehiclePlate(),
                    fine.getAmount(),
                    fine.getStatus()
            });
        }
    }

    
    


      public ReportPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout()); // main panel uses BorderLayout

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding

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
        add(panel, BorderLayout.NORTH);

       //i want the pages to like switch based on what button is pressed to show each report basically
          
      cardLayout = new CardLayout();
      contentPanel = new JPanel(cardLayout);

      JPanel vehiclePanel = new JPanel(new BorderLayout());
      JLabel vehicleLabel = new JLabel("List of Vehicles", SwingConstants.CENTER);
      vehicleLabel.setFont(new Font("Arial", Font.BOLD, 20));
      vehiclePanel.add(vehicleLabel, BorderLayout.NORTH);

      

      String[] vehicleColumn = { "Vehicle" ,"Lisence Plate", "Vehicle Type", "Ticket ID", "Parking Spot", "Entry Time", "Exit Time", "Payment Status"};
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

      tableModel = new DefaultTableModel(vehicleColumn, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
              return false; // make table non-editable
          }
        };

       // Vehicle JTable panel

      
        // JTable
        vehicleTable = new JTable(tableModel);
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.setRowHeight(25);
        vehicleTable.setFont(new Font("Arial", Font.PLAIN, 14));
        vehicleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        vehicleTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); //centers vehicle column
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        vehiclePanel.add(scrollPane, BorderLayout.CENTER); 

        loadVehicleData();
    


      JPanel revenuePanel = new JPanel(new BorderLayout());
      JLabel revenueLabel = new JLabel("Revenue Report", SwingConstants.CENTER);
      revenueLabel.setFont(new Font("Arial", Font.BOLD, 20));
      revenuePanel.add(revenueLabel, BorderLayout.CENTER);
      
   

      JPanel occupancyPanel = new JPanel(new BorderLayout());
      JLabel occupancyLabel = new JLabel("Occupancy Report", SwingConstants.CENTER);
      occupancyLabel.setFont(new Font("Arial", Font.BOLD, 20));
      occupancyPanel.add(occupancyLabel, BorderLayout.CENTER);

      String[] occupancyColumn = { "Floor", "Floor Name", "Total Spot", "Occupied", "Available", "Occupancy Rate"};
      occupancyTableModel = new DefaultTableModel(occupancyColumn, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
              return false; // make table non-editable
          }
        };

        // JTable
        occupancyTable = new JTable(occupancyTableModel);
        occupancyTable.setFillsViewportHeight(true);
        occupancyTable.setRowHeight(25);
        occupancyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        occupancyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        occupancyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // center Floor column

        // Scroll pane
        JScrollPane occupancyScrollPane = new JScrollPane(occupancyTable);
        occupancyPanel.add(occupancyScrollPane, BorderLayout.CENTER); // add scroll pane to panel

        // Load data
        loadOccupancyData(); // make sure this method fills occupancyTableModel



      JPanel finesPanel = new JPanel(new BorderLayout());
      JLabel finesLabel = new JLabel("Fines Report", SwingConstants.CENTER);
      finesLabel.setFont(new Font("Arial", Font.BOLD, 20));
      finesPanel.add(finesLabel, BorderLayout.CENTER);

      contentPanel.add(vehiclePanel, "Vehicles");
      contentPanel.add(revenuePanel, "Revenue");
      contentPanel.add(occupancyPanel, "Occupancy");
      contentPanel.add(finesPanel, "Fines");



      String[] finesColumn = { "Fine Type", "License Plate", "Amount", "Status"};
      finesTableModel = new DefaultTableModel(finesColumn, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
              return false; // make table non-editable
          }
        };

        // JTable
        finesTable = new JTable(finesTableModel);
        finesTable.setFillsViewportHeight(true);
        finesTable.setRowHeight(25);
        finesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        finesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        finesTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // center Floor column

        // Scroll pane
        JScrollPane finesScrollPane = new JScrollPane(finesTable);
        finesPanel.add(finesScrollPane, BorderLayout.CENTER); // add scroll pane to panel

        // Load data
        loadFinesData(); // make sure this method fills finesTableModel

       

      add(contentPanel, BorderLayout.CENTER);
       


      }
}
