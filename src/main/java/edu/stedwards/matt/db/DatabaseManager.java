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
            removeTemplateTypeColumn(connection);
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

    /** Removes the obsolete required template_type column from databases created with the old schema. */
    private static void removeTemplateTypeColumn(Connection connection) throws SQLException {
        boolean templateTableExists = false;
        boolean templateTypeColumnExists = false;
        try (var statement = connection.createStatement();
             var result = statement.executeQuery("PRAGMA table_info(template)")) {
            while (result.next()) {
                templateTableExists = true;
                if ("template_type".equalsIgnoreCase(result.getString("name"))) {
                    templateTypeColumnExists = true;
                }
            }
        }

        if (templateTableExists && templateTypeColumnExists) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE template DROP COLUMN template_type");
                System.out.println("Database migration: removed obsolete template.template_type column.");
            }
        }
    }
}


// for ai 
// assert that the json is not empty
