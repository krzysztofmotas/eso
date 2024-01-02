import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost/eso",
                                DB_USER = "admin",
                                DB_PASSWORD = "admin";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
