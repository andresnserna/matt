/**
 * Architecture layer: model
 * Persistence manager responsible for database lifecycle and setup.
 */
package edu.stedwards.matt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // This URL points directly to the local SQLite file in your project root
    private static final String URL = "jdbc:sqlite:matt.db";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try {
            ensureOnboardingStatusColumn(connection);
            return connection;
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
    }

    /** Adds the onboarding flag to databases created before the column was introduced. */
    private static void ensureOnboardingStatusColumn(Connection connection) throws SQLException {
        boolean userProfileTableExists = false;
        boolean onboardingColumnExists = false;
        try (var statement = connection.createStatement();
             var result = statement.executeQuery("PRAGMA table_info(user_profile)")) {
            while (result.next()) {
                userProfileTableExists = true;
                if ("is_onboarded".equalsIgnoreCase(result.getString("name"))) {
                    onboardingColumnExists = true;
                }
            }
        }

        if (userProfileTableExists && !onboardingColumnExists) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "ALTER TABLE user_profile ADD COLUMN is_onboarded INTEGER NOT NULL DEFAULT 0 " +
                        "CHECK (is_onboarded IN (0, 1))");
            }
        }
    }
}


// for ai 
// assert that the json is not empty
