package View;

import InterfaceLibrary.ParkingGroup;
import InterfaceLibrary.ParkingSpot;
import Model.Ticket;
import Model.Vehicle;
import Model.Vehicle.VehicleType;
import WritetoCSV.WritetoCSV;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class EntryPanel extends JPanel {

    private JTextField txtPlate;
    private JComboBox<VehicleType> cmbType;
    private JComboBox<ParkingSpot> cmbAvailableSpots;
    private JButton btnCheckSpots;
    private JButton btnEnter;
    private JTextArea txtDisplay;
    
    private ParkingGroup parkingGroup; 

    public EntryPanel(ParkingGroup pg) {
        this.parkingGroup = pg;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Top: Input Form ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        inputPanel.add(new JLabel("License Plate:"));
        txtPlate = new JTextField();
        inputPanel.add(txtPlate);

        inputPanel.add(new JLabel("Vehicle Type:"));
        cmbType = new JComboBox<>(VehicleType.values());
        inputPanel.add(cmbType);

        inputPanel.add(new JLabel("Available Spots:"));
        cmbAvailableSpots = new JComboBox<>();
        cmbAvailableSpots.setEnabled(false); 
        inputPanel.add(cmbAvailableSpots);

        btnCheckSpots = new JButton("1. Find Spots");
        btnEnter = new JButton("2. Generate Ticket");
        btnEnter.setEnabled(false);
        
        inputPanel.add(btnCheckSpots);
        inputPanel.add(btnEnter);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center: Display Ticket ---
        txtDisplay = new JTextArea();
        txtDisplay.setEditable(false);
        txtDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(txtDisplay), BorderLayout.CENTER);

        // --- Event Listeners ---
        
        cmbType.addActionListener(e -> {
            cmbAvailableSpots.removeAllItems(); // Clear stale spots
            cmbAvailableSpots.setEnabled(false);
            btnEnter.setEnabled(false);         // Disable entry button
            txtDisplay.setText("");
        });
        // -------------------------------------------------------

        // 1. Check for available spots based on type
        btnCheckSpots.addActionListener(e -> {
            VehicleType selectedType = (VehicleType) cmbType.getSelectedItem();
            List<ParkingSpot> available = parkingGroup.getAvailableSpots(selectedType);
            
            cmbAvailableSpots.removeAllItems();
            
            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No spots available for " + selectedType);
                btnEnter.setEnabled(false);
                cmbAvailableSpots.setEnabled(false);
            } else {
                for (ParkingSpot p : available) {
                    cmbAvailableSpots.addItem(p);
                }
                cmbAvailableSpots.setEnabled(true);
                btnEnter.setEnabled(true);
                txtDisplay.setText("Please select a spot and click Generate Ticket.");
            }
        });

        // 2. Confirm Entry
        btnEnter.addActionListener(e -> processEntry());
    }

    private void processEntry() {
        String plate = txtPlate.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a License Plate.");
            return;
        }

        VehicleType type = (VehicleType) cmbType.getSelectedItem();
        
        // 2. Get Selected Spot
        ParkingSpot selectedSpot = (ParkingSpot) cmbAvailableSpots.getSelectedItem();
        if (selectedSpot == null) return;

         //this ensures logic integrity
        if (!selectedSpot.isAvailableFor(type)) {
            JOptionPane.showMessageDialog(this, 
                "Error: This spot is not suitable for a " + type + ".\nPlease click 'Find Spots' again.");
            return;
        }
        // ------------------------------------------------------

        // 1. Create Vehicle (Moved after check)
        Vehicle vehicle = new Vehicle(plate, type);

        // 3. Mark Occupied
        selectedSpot.occupy();

        // 4. Generate Ticket
        Ticket ticket = new Ticket(vehicle, selectedSpot.getSpotID());

        // 5. Save to CSV
        WritetoCSV.saveTicket(ticket);

        // 6. Display to User
        txtDisplay.setText("=== ENTRY CONFIRMED ===\n");
        txtDisplay.append(ticket.getTicketDetails());
        
        // Reset UI for next user
        txtPlate.setText("");
        cmbAvailableSpots.removeAllItems();
        cmbAvailableSpots.setEnabled(false);
        btnEnter.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Ticket Generated Successfully!");
    }
    
    // Main method for testing
    public static void main(String[] args) {
        JFrame frame = new JFrame("Parking Entry Panel");
        ParkingGroup pg = new ParkingGroup(); 
        EntryPanel entryPanel = new EntryPanel(pg);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(entryPanel);
        frame.setVisible(true);
    }
}