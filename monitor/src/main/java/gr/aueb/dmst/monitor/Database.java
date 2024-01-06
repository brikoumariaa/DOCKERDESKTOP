package gr.aueb.dmst.monitor;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/container_instances_metrics";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "mysecretpassword";

    public Connection getConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}

        return conn;
    }
}
