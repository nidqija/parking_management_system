package Model;

import InterfaceLibrary.ParkingFines;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * CalculatorFee: Computes parking fees and fines, and updates ticket records.
 *
 * Responsibilities:
 * - Calculate parking duration (ceiling to nearest hour)
 * - Lookup spot hourly rates from Data/parking_spots.csv
 * - Calculate fee and fines
 * - Find active ticket for a plate and settle it (write exit time & fee to CSV)
 */
public class CalculatorFee implements ParkingFines {

    private static final String PARKING_DATA_FILE = "Data/parking_spots.csv";
    private static final String TICKET_FILE = "Data/tickets.csv";

    // Rate table (RM/hour)
    private static final double RATE_COMPACT = 2.0;
    private static final double RATE_REGULAR = 5.0;
    private static final double RATE_HANDICAPPED = 2.0;
    private static final double RATE_RESERVED = 10.0;

    /**
     * Represents an active ticket read from the tickets CSV.
     */
    public static class ActiveTicket {
        public final String ticketID;
        public final String plate;
        public final String spotID;
        public final LocalDateTime entryTime;

        public ActiveTicket(String ticketID, String plate, String spotID, LocalDateTime entryTime) {
            this.ticketID = ticketID;
            this.plate = plate;
            this.spotID = spotID;
            this.entryTime = entryTime;
        }
    }

    /**
     * Calculate parking duration in hours, rounding up to the next whole hour.
     */
    public static long calculateHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        long seconds = ChronoUnit.SECONDS.between(entryTime, exitTime);
        if (seconds <= 0) return 0;
        double hours = seconds / 3600.0;
        return (long) Math.ceil(hours);
    }

    /**
     * Returns hourly rate (RM) for a given spot id by reading the parking_spots.csv.
     */
    public static double getHourlyRate(String spotID) {
        File file = new File(PARKING_DATA_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].equals(spotID)) {
                    String type = data[2].trim();
                    switch (type) {
                        case "Compact": return RATE_COMPACT;
                        case "Regular": return RATE_REGULAR;
                        case "Handicapped": return RATE_HANDICAPPED;
                        case "Reserved": return RATE_RESERVED;
                        default: return RATE_REGULAR; // fallback
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading parking data: " + e.getMessage());
        }
        return RATE_REGULAR; // default if not found
    }

    /**
     * Calculate fee (RM) for a ticket: hours * rate. exitTime may be null to use now.
     */
    public static double calculateFee(LocalDateTime entryTime, String spotID, LocalDateTime exitTime) {
        if (exitTime == null) exitTime = LocalDateTime.now();
        long hours = calculateHours(entryTime, exitTime);
        double rate = getHourlyRate(spotID);
        return hours * rate;
    }

    /**
     * Finds the active ticket (no exit_time) for the given plate in the tickets CSV.
     * Returns null if none found.
     */
    public static ActiveTicket findActiveTicket(String plate) {
        File file = new File(TICKET_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine(); // header
            String line;
            ActiveTicket last = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length < 4) continue; // malformed
                String ticketID = data[0];
                String license = data[1];
                String spotID = data[2];
                String entryTimeStr = data[3];

                boolean hasExit = (data.length >= 5 && data[4] != null && !data[4].trim().isEmpty());

                if (license.equals(plate) && !hasExit) {
                    LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr);
                    last = new ActiveTicket(ticketID, license, spotID, entryTime);
                }
            }
            return last;
        } catch (FileNotFoundException e) {
            System.err.println("Tickets file not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading tickets: " + e.getMessage());
        }
        return null;
    }

    /**
     * Settle the active ticket for the plate: set exit time to exitTime (or now) and fee, and mark spot available.
     * Returns true if an active ticket was found and updated.
     */
    public static boolean settleTicket(String plate, LocalDateTime exitTime, double fee) {
        if (exitTime == null) exitTime = LocalDateTime.now();
        File file = new File(TICKET_FILE);
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null) return false;
            lines.add(header);
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lines.add(line);
                    continue;
                }
                String[] data = line.split(",");
                if (data.length < 4) {
                    lines.add(line);
                    continue;
                }
                String license = data[1];
                boolean hasExit = (data.length >= 5 && data[4] != null && !data[4].trim().isEmpty());

                if (!updated && license.equals(plate) && !hasExit) {
                    // Ensure we have 6 fields: ticket_id,plate,spot_id,entry_time,exit_time,fee
                    String ticketID = data[0];
                    String spotID = data[2];
                    String entryTimeStr = data[3];
                    String exitTimeStr = exitTime.toString();
                    String feeStr = String.format("%.2f", fee);

                    String updatedLine = String.join(",", ticketID, license, spotID, entryTimeStr, exitTimeStr, feeStr);
                    lines.add(updatedLine);
                    updated = true;

                    // mark spot available
                    WritetoCSV.WritetoCSV.updateSpotStatus(spotID, "Available");
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading tickets file: " + e.getMessage());
            return false;
        }

        if (updated) {
            // write back
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                for (String out : lines) {
                    bw.write(out);
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error writing tickets file: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Calculate fine based on violationType. Supports the scheme codes:
     *  - "FIXED" -> RM 50
     *  - "PROGRESSIVE:n" -> RM 50 * n  (capped at RM 200)
     *  - "HOURLY:h" -> RM 20 * h
     */
    @Override
    public double calculateFine(String violationType) {
        if (violationType == null) return 0.0;
        violationType = violationType.trim().toUpperCase();
        if (violationType.equals("FIXED")) {
            return 50.0;
        }
        if (violationType.startsWith("PROGRESSIVE:")) {
            try {
                String[] parts = violationType.split(":");
                int n = Integer.parseInt(parts[1]);
                double fine = 50.0 * n;
                return Math.min(fine, 200.0);
            } catch (Exception e) {
                return 50.0;
            }
        }
        if (violationType.startsWith("HOURLY:")) {
            try {
                String[] parts = violationType.split(":");
                int h = Integer.parseInt(parts[1]);
                return 20.0 * h;
            } catch (Exception e) {
                return 0.0;
            }
        }
        // Default for known violations like OVERSTAY
        if (violationType.equals("OVERSTAY") || violationType.equals("WRONG SPOT")) {
            return 50.0;
        }
        return 0.0;
    }

}

