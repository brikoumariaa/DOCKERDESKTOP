package gr.aueb.dmst.monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;

// Η κλάση αυτή υλοποιεί την Runnable για να μπορεί να τρέχει σαν Thread
// Παίρνει instances από ContainerInstanceMetrics
// Ελέγχει αν υπάρχουν στο Container και τα κάνει update ανάλογα

public class DatabaseRunnable implements Runnable {
    private final BlockingQueue<ContainerInstanceMetrics> queue;
    private final Database db;

    public DatabaseRunnable(
        BlockingQueue<ContainerInstanceMetrics> queue,
        Database db
    ) {
        this.queue = queue;
        this.db = db;
    }

    @Override
    public void run() {
        while (true) {
            try (Connection conn = db.getConnection()) {
                
                // Παίρνουμε το αντίστοιχο instance από την ουρά
                
                ContainerInstanceMetrics c = queue.take();

                // Ελέγχουμε αν υπάρχει στην βάση, αλλιώς δημιουργούμε νέο αντικείμενο (record)
                
                if (!containerInstanceExists(conn, c.id())) {
                    createContainerInstanceRecord(
                        conn, 
                        c.id(), 
                        c.image(), 
                        c.name()
                    );
                }

                // Αν υπαρχει το κάνουμε update 
                
                createContainerMetricsRecord(
                    conn,
                    Timestamp.from(ZonedDateTime.parse(c.timestamp()).toInstant()),
                    c.cpuPercent(),
                    c.memoryPercent(),
                    c.id()
                );
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    // Μέθοδος που ελέγχει αν υπάρχει το αντίστοιχο Instance στην βάση μας
    public boolean containerInstanceExists(Connection conn, String containerId) {
        boolean result = false;
        String queryString = "SELECT * FROM container_instances WHERE id = ?;";

        try (PreparedStatement myStmt = conn.prepareStatement(queryString)) {
            myStmt.setString(1, containerId);
            ResultSet myRs = myStmt.executeQuery();
            result = myRs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void createContainerInstanceRecord(
        Connection conn,
        String containerId,
        String image, 
        String name
    ) {
        String queryString = """
            INSERT INTO container_instances (id, image, name) VALUES (?, ?, ?);
        """;

        try (PreparedStatement myStmt = conn.prepareStatement(queryString)) {
            myStmt.setString(1, containerId);
            myStmt.setString(2, image);
            myStmt.setString(3, name);
            myStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createContainerMetricsRecord(
        Connection conn,
        Timestamp timestamp,
        double cpuPercent,
        double memoryPercent,
        String containerId
    ) {
        String queryString = """
            INSERT INTO container_metrics (
                measurement_timestamp,
                cpu_percent,
                memory_percent,
                container_instance_id
            ) VALUES (?, ?, ?, ?);
        """;

        try (PreparedStatement myStmt = conn.prepareStatement(queryString)) {
            myStmt.setTimestamp(1, timestamp);
            myStmt.setDouble(2, cpuPercent);
            myStmt.setDouble(3, memoryPercent);
            myStmt.setString(4, containerId);
            myStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
