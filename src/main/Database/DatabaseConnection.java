package main.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/Stock%20Management%20System"; // Your Database URL
    private static final String USER = "postgres"; // Username
    private static final String PASSWORD = "root"; // Password

    private static Connection connection;

    public static Connection getConnection() throws SQLException{
        if (connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
