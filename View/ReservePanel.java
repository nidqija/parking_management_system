package View;
import Data.Sqlite;
import InterfaceLibrary.ParkingGroup;
import InterfaceLibrary.ParkingSpotInterface;
import Model.Vehicle.VehicleType;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class ReservePanel extends JPanel {

    private JTextField txtPlate;
    private JComboBox<VehicleType> cmbType;
    private JComboBox<ParkingSpotInterface> cmbAvailableSpots;
    private JButton btnCheckSpots;
    private JButton btnEnter;
    private JTextArea txtDisplay;
    private ParkingGroup parkingGroup;
    private List<ParkingSpotInterface> currentAvailableSpots;


    public ReservePanel(MainFrame mainFrame, ParkingGroup pg) {
       

    
        this.parkingGroup = pg;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


       


        // --- Top: Input Form ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        JLabel title = new JLabel("Reserve a Parking Spot", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);


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

       

    
        saveReservationtoDb(plate, type, selectedSpot.getSpotID());

       
        // 4. Display to User
        txtDisplay.setText("=== ENTRY CONFIRMED ===\n");
        txtDisplay.append("License Plate: " + plate + "\n");
        txtDisplay.append("Vehicle Type: " + type + "\n");
        txtDisplay.append("Spot ID: " + selectedSpot.getSpotID() + "\n");   
        // 5. Cleanup
        txtPlate.setText("");
        resetSelection();
        JOptionPane.showMessageDialog(this, "Reservation Generated & Saved to Database!");

    }






    private void saveReservationtoDb(String plate, VehicleType type, String spotId) {
       Sqlite db = new Sqlite();
       try (Connection conn = db.connect()) {
           String sql = "INSERT INTO Reservations (license_plate, spot_id, start_time, end_time, status) " +
                 "VALUES (?, ?, datetime('now', 'localtime'), datetime('now', 'localtime', '+2 hours'), 'ACTIVE')";
           try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setString(1, plate);
               pstmt.setString(2, spotId);

               pstmt.executeUpdate();
               System.out.println("Reservation saved for " + plate + " at spot " + spotId);
           }
           
       } catch (Exception e) {
           e.printStackTrace();
    }
}
}
    

