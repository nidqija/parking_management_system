package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//RUN THS FILE TO INIT OR RESET DATABASE 
public class Sqlite {

    private static String url = "jdbc:sqlite:Data/Parking_Management_System.db";
    private static String[] reservedPlates = { "WXY5521", "JJU8822", "VKA4321", "BMA1188", "PKR7777" };

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        // This will create 'Parking_Management_System.db' in your project folder
        // Ensure the "Data" folder exists, or change path to
        // "Parking_Management_System.db"

        // 1. Load the driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver not found. Include it in your library path.");
            return;
        }

        // 2. Establish connection and create tables
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");

                // Create a sample table

                try (Statement stmt = conn.createStatement()) {

                    // Enable Foreign Keys (OFF by default in SQLite)
                    stmt.execute("PRAGMA foreign_keys = ON;");

                    // --- Table 2: Floors ---
                    String tb_floors = "CREATE TABLE IF NOT EXISTS Floors (" +
                            "floor_id INTEGER PRIMARY KEY, " +
                            "floor_name TEXT NOT NULL" +
                            ");";
                    stmt.execute(tb_floors);

                    // --- Table 3: Vehicles (Stores Types & Card Holder Status) ---
                    String tb_vehicles = "CREATE TABLE IF NOT EXISTS Vehicles (" +
                            "license_plate TEXT PRIMARY KEY, " +
                            "vehicle_type TEXT NOT NULL  " +
                            ");";
                    stmt.execute(tb_vehicles);

                    // --- Table 4: Parking Spots ---
                    // 'spot_id' stores strings like 'F1-R1-S1'
                    String tb_spots = "CREATE TABLE IF NOT EXISTS Parking_Spots (" +
                            "spot_id TEXT PRIMARY KEY, " +
                            "floor_id INTEGER NOT NULL, " +
                            "spot_type TEXT NOT NULL, " +
                            "status TEXT DEFAULT 'AVAILABLE' , " +
                            "hourly_rate FLOAT NOT NULL, " +
                            "reserved_for_plate TEXT, " + // For VIP permanent reservations
                            "current_vehicle_plate TEXT, " + // For quick lookup
                            "FOREIGN KEY (floor_id) REFERENCES Floors(floor_id), " +
                            "FOREIGN KEY (reserved_for_plate) REFERENCES Vehicles(license_plate), " +
                            "FOREIGN KEY (current_vehicle_plate) REFERENCES Vehicles(license_plate)" +
                            ");";
                    stmt.execute(tb_spots);

                    // --- Table 1: Fine_Settings --- //
                    // Stores fine amounts for different violation types
                    String tb_fine_settings = "CREATE TABLE IF NOT EXISTS Fine_Settings (" +
                            "fine_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "violation_type TEXT NOT NULL, " +
                            "amount FLOAT NOT NULL" +
                            ");";
                    stmt.execute(tb_fine_settings);

                    // --- Table 5: Tickets (Visit History) ---
                    // 'entry_time' stored as ISO8601 String
                    String tb_tickets = "CREATE TABLE IF NOT EXISTS Tickets (" +
                            "ticket_number TEXT PRIMARY KEY, " +
                            "license_plate TEXT NOT NULL, " +
                            "spot_id TEXT NOT NULL, " +
                            "entry_time TEXT NOT NULL, " +
                            "exit_time TEXT, " +
                            "duration_hours INTEGER, " +
                            "parking_fee FLOAT, " +
                            "payment_method TEXT, " +
                            "payment_status TEXT DEFAULT 'UNPAID' , " +
                            "FOREIGN KEY (license_plate) REFERENCES Vehicles(license_plate), " +
                            "FOREIGN KEY (spot_id) REFERENCES Parking_Spots(spot_id)" +
                            ");";
                    stmt.execute(tb_tickets);

                    // --- Table 6: Fines Ledger (Historical Debt) ---
                    // Tracks fines separately from daily tickets
                    String tb_fines = "CREATE TABLE IF NOT EXISTS Fines_Ledger (" +
                            "fine_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "license_plate TEXT NOT NULL, " +
                            "ticket_ref TEXT, " +
                            "violation_type TEXT NOT NULL , " +
                            "amount FLOAT NOT NULL, " +
                            "status TEXT DEFAULT 'UNPAID' ," +
                            "created_at TEXT NOT NULL, " +
                            "FOREIGN KEY (license_plate) REFERENCES Vehicles(license_plate), " +
                            "FOREIGN KEY (ticket_ref) REFERENCES Tickets(ticket_number)" +
                            ");";
                    stmt.execute(tb_fines);

                    // --- Table 7: Reservations (Future-Proofing) ---
                    // Allows booking a spot for a specific time range
                    String tb_reservations = "CREATE TABLE IF NOT EXISTS Reservations (" +
                            "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "license_plate TEXT NOT NULL, " +
                            "spot_id TEXT NOT NULL, " +
                            "start_time TEXT NOT NULL, " +
                            "end_time TEXT NOT NULL, " +
                            "status TEXT DEFAULT 'ACTIVE', " +
                            "FOREIGN KEY (license_plate) REFERENCES Vehicles(license_plate), " +
                            "FOREIGN KEY (spot_id) REFERENCES Parking_Spots(spot_id)" +
                            ");";
                    stmt.execute(tb_reservations);

                    String tb_activeschemesettings = "CREATE TABLE IF NOT EXISTS Active_Scheme_Settings (" +
                            "id INTEGER PRIMARY KEY CHECK (id = 1), " +
                            "active_scheme TEXT NOT NULL, " +
                            "settings_value TEXT NOT NULL" +
                            ");";

                    stmt.execute(tb_activeschemesettings);

                    String admin_table = "CREATE TABLE IF NOT EXISTS Admins (" +
                            "admin_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL" +
                            ");";
                    stmt.execute(admin_table);

                    String receipt_table = "CREATE TABLE IF NOT EXISTS Receipts (" +
                            "receipt_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "ticket_number TEXT NOT NULL, " +
                            "license_plate TEXT NOT NULL, " +
                            "spot_id TEXT NOT NULL, " +
                            "entry_time TEXT NOT NULL, " +
                            "exit_time TEXT NOT NULL, " +
                            "duration_hours INTEGER NOT NULL, " +
                            "parking_fee FLOAT NOT NULL, " +
                            "payment_method TEXT NOT NULL, " +
                            "FOREIGN KEY (ticket_number) REFERENCES Tickets(ticket_number), " +
                            "FOREIGN KEY (license_plate) REFERENCES Vehicles(license_plate), " +
                            "FOREIGN KEY (spot_id) REFERENCES Parking_Spots(spot_id)" +
                            ");";

                    stmt.execute(receipt_table);

                    System.out.println("All tables created successfully.");

                    System.out.println("Inserting sample data...");

                    stmt.execute(
                            "INSERT OR IGNORE INTO Active_Scheme_Settings (id, active_scheme, settings_value) VALUES (1, 'Option A', 'Fixed Fine');");

                    // A. Insert Floors
                    // ------------------------------------------------------------
                    stmt.execute("INSERT OR IGNORE INTO Floors (floor_id, floor_name) VALUES (1, 'Ground Floor');");
                    stmt.execute("INSERT OR IGNORE INTO Floors (floor_id, floor_name) VALUES (2, 'Level 1');");
                    stmt.execute("INSERT OR IGNORE INTO Floors (floor_id, floor_name) VALUES (3, 'Level 2');");
                    stmt.execute("INSERT OR IGNORE INTO Floors (floor_id, floor_name) VALUES (4, 'Level 3');");
                    stmt.execute("INSERT OR IGNORE INTO Floors (floor_id, floor_name) VALUES (5, 'Level 4');");

                    // B. Insert Vehicles (Needed first for Foreign Keys)
                    // ------------------------------------------------------------
                    stmt.execute(
                            "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES ('VIP-9999', 'SUV');");
                    stmt.execute(
                            "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES ('CAR-1234', 'CAR');");
                    stmt.execute(
                            "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES ('BIKE-8888', 'MOTORCYCLE');");
                    stmt.execute(
                            "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES ('BAD-5555', 'CAR');"); // Has
                                                                                                                         // unpaid
                                                                                                                         // fine
                    stmt.execute(
                            "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES ('TEST-FINE', 'CAR');"); // For
                                                                                                                          // fine
                                                                                                                          // testing

                    // Add random reserved vehicles for spots S4 on each floor
                    for (String plate : reservedPlates) {
                        stmt.execute(String.format(
                                "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES ('%s', 'CAR');",
                                plate));
                    }

                    stmt.execute(
                            "INSERT OR IGNORE INTO Parking_Spots (spot_id, floor_id, spot_type, status, hourly_rate) VALUES "
                                    +
                                    "('F4-R1-S1', 4, 'COMPACT', 'AVAILABLE', 2.0), ('F4-R1-S2', 4, 'COMPACT', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S3', 4, 'COMPACT', 'AVAILABLE', 2.0), ('F4-R1-S4', 4, 'COMPACT', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S5', 4, 'COMPACT', 'AVAILABLE', 2.0), ('F4-R1-S6', 4, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S7', 4, 'REGULAR', 'AVAILABLE', 2.0), ('F4-R1-S8', 4, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S9', 4, 'REGULAR', 'AVAILABLE', 2.0), ('F4-R1-S10', 4, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S11', 4, 'REGULAR', 'AVAILABLE', 2.0), ('F4-R1-S12', 4, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S13', 4, 'REGULAR', 'AVAILABLE', 2.0), ('F4-R1-S14', 4, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F4-R1-S15', 4, 'REGULAR', 'AVAILABLE', 2.0);");

                    // --- Adding Floor 5 (Level 4) ---
                    stmt.execute(
                            "INSERT OR IGNORE INTO Parking_Spots (spot_id, floor_id, spot_type, status, hourly_rate) VALUES "
                                    +
                                    "('F5-R1-S1', 5, 'COMPACT', 'AVAILABLE', 2.0), ('F5-R1-S2', 5, 'COMPACT', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S3', 5, 'COMPACT', 'AVAILABLE', 2.0), ('F5-R1-S4', 5, 'COMPACT', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S5', 5, 'COMPACT', 'AVAILABLE', 2.0), ('F5-R1-S6', 5, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S7', 5, 'REGULAR', 'AVAILABLE', 2.0), ('F5-R1-S8', 5, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S9', 5, 'REGULAR', 'AVAILABLE', 2.0), ('F5-R1-S10', 5, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S11', 5, 'REGULAR', 'AVAILABLE', 2.0), ('F5-R1-S12', 5, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S13', 5, 'REGULAR', 'AVAILABLE', 2.0), ('F5-R1-S14', 5, 'REGULAR', 'AVAILABLE', 2.0), "
                                    +
                                    "('F5-R1-S15', 5, 'REGULAR', 'AVAILABLE', 2.0);");
                    // C. Insert 45 Parking Spots (Loop Logic)
                    // ------------------------------------------------------------
                    // We delete existing spots to avoid duplicates during testing re-runs
                    stmt.execute("DELETE FROM Tickets;");
                    stmt.execute("DELETE FROM Parking_Spots;");

                    for (int floor = 1; floor <= 5; floor++) {
                        for (int s = 1; s <= 15; s++) {
                            // Generate ID: F1-R1-S1 (assuming 1 Row for simplicity, or we can map S1-S15)
                            String spotID = String.format("F%d-R1-S%d", floor, s);
                            String type = "REGULAR";
                            double rate = 5.0;
                            String reservedPlate = "NULL";
                            String status = "'AVAILABLE'";
                            String currentPlate = "NULL";

                            // --- Logic to vary spot types (all floors) ---
                            if (s <= 2) {
                                type = "HANDICAPPED";
                                rate = 2.0;
                            } else if (s <= 4) {
                                type = "RESERVED";
                                rate = 10.0;
                                // Make Spot 4 reserved for a random plate per floor (using the realistic list)
                                if (s == 4)
                                    reservedPlate = String.format("'%s'", reservedPlates[floor - 1]);
                            } else if (s <= 10) {
                                type = "COMPACT";
                                rate = 2.0;
                            }

                            // --- Simulate Occupied Spots (for testing Exit) ---
                            // 1. Regular Car in F1-R1-S15
                            if (spotID.equals("F1-R1-S15")) {
                                status = "'OCCUPIED'";
                                currentPlate = "'CAR-1234'";
                            }
                            // 2. Bike in F2-R1-S1
                            if (spotID.equals("F2-R1-S1")) {
                                status = "'OCCUPIED'";
                                currentPlate = "'BIKE-8888'";
                            }

                            // Build INSERT String
                            String sql = String.format(
                                    "INSERT INTO Parking_Spots (spot_id, floor_id, spot_type, status, hourly_rate, reserved_for_plate, current_vehicle_plate) "
                                            +
                                            "VALUES ('%s', %d, '%s', %s, %.2f, %s, %s);",
                                    spotID, floor, type, status, rate, reservedPlate, currentPlate);
                            stmt.execute(sql);
                        }
                    }

                    // D. Insert Active Tickets (For the occupied spots)
                    // ------------------------------------------------------------
                    // Ticket for CAR-1234 (Entered 2 hours ago)
                    stmt.execute(
                            "INSERT OR IGNORE INTO Tickets (ticket_number, license_plate, spot_id, entry_time, payment_status) "
                                    +
                                    "VALUES ('T-CAR1234-001', 'CAR-1234', 'F1-R1-S15', datetime('now', '-2 hours'), 'UNPAID');");

                    // Ticket for BIKE-8888 (Entered 30 mins ago)
                    stmt.execute(
                            "INSERT OR IGNORE INTO Tickets (ticket_number, license_plate, spot_id, entry_time, payment_status) "
                                    +
                                    "VALUES ('T-BIKE8888-001', 'BIKE-8888', 'F2-R1-S1', datetime('now', '-30 minutes'), 'UNPAID');");

                    // Tickets to test fine

                    stmt.execute(
                            "INSERT OR IGNORE INTO Tickets (ticket_number, license_plate, spot_id, entry_time, payment_status) "
                                    +
                                    "VALUES ('T-FINE-001', 'TEST-FINE', 'F1-R1-S2', datetime('now', '-99 hours'), 'UNPAID');");

                    // E. Insert Historical Data (Fines)
                    // ------------------------------------------------------------
                    // BAD-5555 Left without paying a previous fine
                    stmt.execute(
                            "INSERT OR IGNORE INTO Fines_Ledger (license_plate, violation_type, amount, status, created_at) "
                                    +
                                    "VALUES ('BAD-5555', 'OVERSTAY', 50.00, 'UNPAID', datetime('now', '-5 days'));");

                    stmt.execute("INSERT OR IGNORE INTO Admins (username, password) " +
                            "VALUES ('raziq', 'raziq123');");

                    stmt.execute("DELETE FROM fine_settings");

                    stmt.execute("INSERT OR IGNORE INTO fine_settings (violation_type, amount) " +
                            "VALUES ('FIXED', 50.00), ('PROGRESSIVE', 50.00), ('HOURLY', 20.00);");

                    System.out.println("Sample data inserted successfully.");

                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}