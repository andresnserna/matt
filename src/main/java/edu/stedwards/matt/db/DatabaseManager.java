package edu.stedwards.matt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // This URL points directly to the local SQLite file in your project root
    private static final String URL = "jdbc:sqlite:matt.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}


// for ai 
// assert that the json is not empty