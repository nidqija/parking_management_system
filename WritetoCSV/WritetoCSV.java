package WritetoCSV;

import Model.Ticket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


//// When a car exits and pays: can use
//WritetoCSV.updateSpotStatus("F1-01", "AVAILABLE");
public class WritetoCSV {
    
    private static final String TICKET_FILE = "Data/tickets.csv";
    private static final String PARKING_DATA_FILE = "Data/parking_spots.csv";

    // 1. Existing method to save the ticket
    public static void saveTicket(Ticket ticket) {
        try (FileWriter fw = new FileWriter(TICKET_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Format: TicketID, Plate, SpotID, EntryTime
            String line = String.format("%s,%s,%s,%s",
                    ticket.getTicketID(),
                    ticket.getPlateNumber(),
                    ticket.getSpotID(),
                    ticket.getEntryTimeStr());
            
            pw.println(line);
            System.out.println("Ticket saved to CSV.");
            
            // 2. NEW: Automatically update the parking_data.csv status
            updateSpotStatus(ticket.getSpotID(), "OCCUPIED");
            
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    /**
     * Reads the parking data file, finds the specific spot, updates its status, 
     * and overwrites the file with the new data.
     * * @param spotID The ID of the spot to update (e.g., "F1-01")
     * @param newStatus The new status (e.g., "OCCUPIED", "AVAILABLE")
     */
    public static void updateSpotStatus(String spotID, String newStatus) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        // Step A: Read all lines into memory
        File file = new File(PARKING_DATA_FILE);
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;
            
            while ((currentLine = br.readLine()) != null) {
                // Split the CSV line by comma
                // Expected format: spot_id, floor_id, spot_type, status
                String[] data = currentLine.split(",");
                
                // Check if this is the spot we are looking for
                if (data.length >= 4 && data[0].equals(spotID)) {
                    // Update the status (index 3)
                    data[3] = newStatus;
                    
                    // Rejoin the array back into a CSV string
                    String updatedLine = String.join(",", data);
                    lines.add(updatedLine);
                    found = true;
                } else {
                    // Keep the line exactly as it was
                    lines.add(currentLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading parking data: " + e.getMessage());
            return; // Stop if we can't read
        }

        // Step B: Overwrite the file with the updated lines
        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine(); // Add new line character
                }
                System.out.println("Parking spot " + spotID + " updated to " + newStatus);
            } catch (IOException e) {
                System.err.println("Error updating parking data: " + e.getMessage());
            }
        } else {
            System.err.println("Spot ID " + spotID + " not found in database.");
        }
    }
}