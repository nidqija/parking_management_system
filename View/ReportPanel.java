package View;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Model.CalculatorFee;
import Model.RevenueRecord;
import Model.Vehicle;
import Controller.Fines;
import Controller.Floors;
import Controller.ParkingComplex;

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
            tableModel.addRow(new Object[] {
                    i + 1,
                    v.getPlateNumber(),
                    v.getType(),
                    v.getTicketNumber() != null ? v.getTicketNumber() : "NULL",
                    v.getSpotID() != null ? v.getSpotID() : "NULL",
                    v.getEntryTime() != null ? v.getEntryTime().toString() : "NULL",
                    v.getExitTime() != null ? v.getExitTime().toString() : "NULL",
                    v.getPaymentStatus() != null ? v.getPaymentStatus() : "NULL"
            });
            i++; // increment
        }
    }

    private void loadOccupancyData() {
        if (occupancyTableModel == null)
            return;
        occupancyTableModel.setRowCount(0);

        List<Floors> floors = Floors.getFloors();

        for (Floors f : floors) {
            occupancyTableModel.addRow(new Object[] {
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
        if (finesTableModel == null)
            return;
        finesTableModel.setRowCount(0);

        // Assuming a method exists to get fines data
        List<Fines> fines = Fines.getFinesReportList();

        for (Fines fine : fines) {
            finesTableModel.addRow(new Object[] {
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

        JButton vehiclesBtn = new JButton("Vehicles List");
        JButton revenueBtn = new JButton("Revenue Report");
        JButton occupancyBtn = new JButton("Occupancy Report");
        JButton finesBtn = new JButton("Outstanding Fines");
        JButton backBtn = new JButton("Back");

        JButton[] arrayOfButtons = { vehiclesBtn, revenueBtn, occupancyBtn, finesBtn, backBtn };

        for (JButton btn : arrayOfButtons) {
            buttonPanel.add(btn);
        }

        vehiclesBtn.addActionListener(e -> cardLayout.show(contentPanel, "Vehicles"));
        revenueBtn.addActionListener(e -> cardLayout.show(contentPanel, "Revenue"));
        occupancyBtn.addActionListener(e -> cardLayout.show(contentPanel, "Occupancy"));
        finesBtn.addActionListener(e -> cardLayout.show(contentPanel, "Fines"));

        backBtn.addActionListener(e -> {
            mainFrame.showPage("AdminPanel");
        });

        panel.add(label);
        panel.add(buttonPanel);
        add(panel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Standard Table Formatting Variables
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        Font headerFont = new Font("Arial", Font.BOLD, 16);
        Font tableFont = new Font("Arial", Font.PLAIN, 14);

        // ==========================
        // VEHICLE PANEL
        // ==========================
        JPanel vehiclePanel = new JPanel(new BorderLayout());
        JLabel vehicleLabel = new JLabel("Vehicle List Report", SwingConstants.CENTER);
        vehicleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        vehiclePanel.add(vehicleLabel, BorderLayout.NORTH);

        String[] vehicleColumn = { "Vehicle", "License Plate", "Vehicle Type", "Ticket ID", "Parking Spot",
                "Entry Time", "Exit Time", "Payment Status" };

        tableModel = new DefaultTableModel(vehicleColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table non-editable
            }
        };

        vehicleTable = new JTable(tableModel);
        vehicleTable.setRowHeight(25);
        vehicleTable.setFont(tableFont);
        vehicleTable.getTableHeader().setFont(headerFont);
        vehicleTable.setDefaultRenderer(Object.class, centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        vehiclePanel.add(scrollPane, BorderLayout.CENTER);
        loadVehicleData();
        
        // Add to card layout
        contentPanel.add(vehiclePanel, "Vehicles");

        // ==========================
        // REVENUE PANEL
        // ==========================
        JPanel revenuePanel = new JPanel(new BorderLayout());

        JPanel revenueTopPanel = new JPanel();
        revenueTopPanel.setLayout(new BoxLayout(revenueTopPanel, BoxLayout.Y_AXIS));

        ParkingComplex parkingComplex = new ParkingComplex();
        double totalRevenue = parkingComplex.getFormattedRevenue();
        JLabel revenueValueLabel = new JLabel(String.format("Total Revenue: $%.2f", totalRevenue),
                SwingConstants.CENTER);
        revenueValueLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        revenueValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        revenueTopPanel.add(revenueValueLabel);
        revenueTopPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel revHeader = new JLabel("Revenue Breakdown by Paid Tickets", SwingConstants.CENTER);
        revHeader.setFont(new Font("Arial", Font.PLAIN, 15));
        revHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        revenueTopPanel.add(revHeader);
        revenuePanel.add(revenueTopPanel, BorderLayout.NORTH);

        String[] revenueColumnNames = { "Plate Number", "Entry Time", "Exit Time", "Amount Paid", "Payment Method" };
        revenueTableModel = new DefaultTableModel(revenueColumnNames, 0); 
        
        revenueTable = new JTable(revenueTableModel); 
        // --- Standard Formatting Applied Here ---
        revenueTable.setRowHeight(25);
        revenueTable.setFont(tableFont);
        revenueTable.getTableHeader().setFont(headerFont);
        revenueTable.setDefaultRenderer(Object.class, centerRenderer);
        
        JScrollPane revenueScrollPane = new JScrollPane(revenueTable);
        revenueScrollPane.setPreferredSize(new Dimension(700, 300));
        revenuePanel.add(revenueScrollPane, BorderLayout.CENTER);

        CalculatorFee calculator = new CalculatorFee();
        List<Object[]> paidTicketsData = calculator.getPaidTicket();
        for (Object[] rowData : paidTicketsData) {
            revenueTableModel.addRow(rowData);
        }

        // Add to card layout
        contentPanel.add(revenuePanel, "Revenue");

        // ==========================
        // OCCUPANCY PANEL
        // ==========================
        JPanel occupancyPanel = new JPanel(new BorderLayout());
        JLabel occupancyLabel = new JLabel("Occupancy Status Report", SwingConstants.CENTER);
        occupancyLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        occupancyPanel.add(occupancyLabel, BorderLayout.NORTH);

        String[] occupancyColumn = { "Floor", "Floor Name", "Total Spot", "Occupied", "Available", "Occupancy Rate" };
        occupancyTableModel = new DefaultTableModel(occupancyColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        occupancyTable = new JTable(occupancyTableModel);
        occupancyTable.setRowHeight(25);
        occupancyTable.setFont(tableFont);
        occupancyTable.getTableHeader().setFont(headerFont);
        occupancyTable.setDefaultRenderer(Object.class, centerRenderer);
        
        JScrollPane occupancyScrollPane = new JScrollPane(occupancyTable);
        occupancyPanel.add(occupancyScrollPane, BorderLayout.CENTER);
        loadOccupancyData();

        // Add to card layout
        contentPanel.add(occupancyPanel, "Occupancy");

        // ==========================
        // FINES PANEL
        // ==========================
        JPanel finePanel = new JPanel(new BorderLayout());

        JPanel fineTopPanel = new JPanel();
        fineTopPanel.setLayout(new BoxLayout(fineTopPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("List of Outstanding Fines:");
        infoLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fineTopPanel.add(infoLabel);

        finePanel.add(fineTopPanel, BorderLayout.NORTH);

        String[] fineColumnNames = { "License Plate", "Violation Type", "Amount", "Status" };
        finesTableModel = new DefaultTableModel(fineColumnNames, 0); 
        
        finesTable = new JTable(finesTableModel); 
        // --- Standard Formatting Applied Here ---
        finesTable.setRowHeight(25);
        finesTable.setFont(tableFont);
        finesTable.getTableHeader().setFont(headerFont);
        finesTable.setDefaultRenderer(Object.class, centerRenderer);
        
        JScrollPane fineScrollPane = new JScrollPane(finesTable);
        fineScrollPane.setPreferredSize(new Dimension(700, 300));
        finePanel.add(fineScrollPane, BorderLayout.CENTER); 

        Controller.Fines finesController = new Controller.Fines();
        List<Fines> outstandingFines = finesController.getUnpaidFines();

        if (outstandingFines != null) {
            for (Fines fine : outstandingFines) {
                Object[] rowData = {
                        fine.getVehiclePlate(),
                        fine.getFineID(),
                        fine.getAmount(),
                        fine.getStatus()
                };
                finesTableModel.addRow(rowData);
            }
        }

        // Add to card layout
        contentPanel.add(finePanel, "Fines");

        // Add the main content panel to the frame
        add(contentPanel, BorderLayout.CENTER);
    }
}