package View;

import InterfaceLibrary.ParkingGroup;
import InterfaceLibrary.ParkingSpotInterface;
import Model.Ticket;
import Model.Vehicle;
import Model.Vehicle.VehicleType;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class EntryPanel extends JPanel {

    private JTextField txtPlate;
    private JComboBox<VehicleType> cmbType;
    private JComboBox<ParkingSpotInterface> cmbAvailableSpots;
    private JButton btnCheckSpots;
    private JButton btnEnter;
    private JTextArea txtDisplay;
    private MainFrame mainFrame;


    private ParkingGroup parkingGroup;

    public EntryPanel(MainFrame mainFrame, ParkingGroup pg) {
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
            resetSelection(); // Refactored reset logic
        });

        // 1. Check for available spots
        btnCheckSpots.addActionListener(e -> {
            // Optional: Refresh DB data to ensure availability is current
            parkingGroup.refresh();

            VehicleType selectedType = (VehicleType) cmbType.getSelectedItem();
            String plate = txtPlate.getText().trim();
            List<ParkingSpotInterface> available = parkingGroup.getAvailableSpots(selectedType, plate);

            cmbAvailableSpots.removeAllItems();

            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No spots available for " + selectedType);
                btnEnter.setEnabled(false);
                cmbAvailableSpots.setEnabled(false);
            } else {
                for (ParkingSpotInterface p : available) {
                    cmbAvailableSpots.addItem(p);
                }
                cmbAvailableSpots.setEnabled(true);
                btnEnter.setEnabled(true);
                txtDisplay.setText("Please select a spot and click Generate Ticket.");
            }
        });

        // 2. Confirm Entry
        btnEnter.addActionListener(e -> processEntry());

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            mainFrame.showPage("Homepage");
        });

        add(backButton, BorderLayout.SOUTH);
    }

    private void resetSelection() {
        cmbAvailableSpots.removeAllItems();
        cmbAvailableSpots.setEnabled(false);
        btnEnter.setEnabled(false);
        txtDisplay.setText("");
    }

    private void processEntry() {
        String plate = txtPlate.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a License Plate.");
            return;
        }

        VehicleType type = (VehicleType) cmbType.getSelectedItem();

        ParkingSpotInterface selectedSpot = (ParkingSpotInterface) cmbAvailableSpots.getSelectedItem();
        if (selectedSpot == null)
            return;

        // Double check availability
        if (!selectedSpot.isAvailableFor(type, plate)) {
            JOptionPane.showMessageDialog(this, "Error: Spot mismatch.");
            return;
        }

        // 1. Create Vehicle
        Vehicle vehicle = new Vehicle(plate, type);

        // 2. Mark Occupied (Memory Update)
        selectedSpot.occupy();

        // 3. Generate Ticket (will automatically link to reservation if exists)
        Ticket ticket = new Ticket(vehicle, selectedSpot.getSpotID());

        // 4. Display to User
        txtDisplay.setText("=== ENTRY CONFIRMED ===\n");
        txtDisplay.append(ticket.getTicketDetails());

        // 5. Cleanup
        txtPlate.setText("");
        resetSelection();
        JOptionPane.showMessageDialog(this, "Ticket Generated & Saved to Database!");

    }


    

}