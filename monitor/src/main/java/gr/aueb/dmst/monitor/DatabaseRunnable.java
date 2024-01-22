package gr.aueb.dmst.monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;

// This class implements the Runnable interface to be able to run as a Thread
// It takes instances from ContainerInstanceMetrics
// Checks if they exist in the Container and updates them accordingly

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
                
                // Retrieve the corresponding instance from the queue
                
                ContainerInstanceMetrics c = queue.take();

                // Check if it exists in the database; otherwise, create a new record
                
                if (!containerInstanceExists(conn, c.id())) {
                    createContainerInstanceRecord(
                        conn, 
                        c.id(), 
                        c.image(), 
                        c.name()
                    );
                }

                // Update the record if it exists
                
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

    // Method to check if the corresponding instance exists in our database
    
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

    //  Inserts a new record into the container_instances table
    
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

    // Inserts a new record into the container_metrics table

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
