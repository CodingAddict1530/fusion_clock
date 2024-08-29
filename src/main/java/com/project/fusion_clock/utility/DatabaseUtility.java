package com.project.fusion_clock.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Handles Database related operations.
 */
public class DatabaseUtility {

    /**
     * The logger for the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtility.class);

    /**
     * The URL to connect to the database.
     */
    private static final String URL = "jdbc:sqlite:fusion.db";

    private static final ArrayList<Connection> connections = new ArrayList<>();

    public static final String SQLITE3_PATH = "lib/sqlite3/sqlite3.exe";
    public static final String DB_PATH = "fusion.db";
    public static final String CSV_FILE_PATH = "files/worldcities.csv";
    public static final String CSV_FILE_PATH_DST = "files/worldcitiesdst.csv";
    public static final String TABLE_NAME = "cities";
    public static final String TABLE_NAME_DST = "cities_dst";

    private static boolean pragmaSet = false;

    public static int createTable(String table) throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                SQLITE3_PATH,
                DB_PATH,
                "-cmd",
                (table.equals(TABLE_NAME)) ?
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        "city TEXT," +
                        "city_ascii TEXT," +
                        "lat TEXT," +
                        "lng TEXT," +
                        "country TEXT," +
                        "iso2 TEXT," +
                        "iso3 TEXT," +
                        "admin_name TEXT," +
                        "capital TEXT," +
                        "population TEXT," +
                        "id TEXT PRIMARY KEY" +
                        ");"
                        :
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DST + " (" +
                        "id TEXT PRIMARY KEY," +
                        "raw_offset TEXT," +
                        "dst_offset TEXT," +
                        "dst_from TEXT," +
                        "dst_until TEXT," +
                        "FOREIGN KEY (id) REFERENCES " + TABLE_NAME + "(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                        ");"
        );

        int exitStatus = pb.inheritIO().start().waitFor();
        if (!pragmaSet) {
            try (Connection conn = connect()) {
                if (conn != null) {
                    Statement statement = conn.createStatement();
                    statement.execute("PRAGMA foreign_keys = ON;");
                    pragmaSet = true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return exitStatus;

    }

    public static int importCSV(String path, String table) throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                SQLITE3_PATH,
                DB_PATH,
                "-cmd",
                ".mode csv",
                ".import " + path + " " + table
        );

        // Start the process and wait for it to complete
        return pb.inheritIO().start().waitFor();

    }

    /**
     * Creates a connection to the database.
     *
     * @return The Connection.
     */
    public static Connection connect() {

        try {
            logger.info("Connected to database");
            Connection c = DriverManager.getConnection(URL);
            connections.add(c);
            return c;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    /**
     * Executes a SELECT query.
     *
     * @param conn The database connection.
     * @param query The query. (In Prepared statement form).
     * @param params The parameters for the prepared statement.
     * @return A ResultSet.
     */
    public static ResultSet executeQuery(Connection conn, String query, String table, Object... params) {

        try {

            // Make sure the table exists.
            createTable(table);

            PreparedStatement ps = conn.prepareStatement(query);

            // Set all parameters.
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    /**
     * Executes INSERT, UPDATE, DELETE AND DROP statements.
     *
     * @param conn The database connection.
     * @param query The query. (In Prepared statement form).
     * @param params The parameters for the prepared statement.
     */
    public static void executeUpdate(Connection conn, String query, String table, Object... params) {

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            // Make sure the table exists.
            createTable(table);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * Closes a given connection.
     *
     * @param conn The Connection to close.
     */
    public static void close(Connection conn) {

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                logger.info("Closing connection");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * Closes all connections to the database.
     */
    public static void closeAll() {

        for (Connection conn : connections) {
            close(conn);
        }

    }

}

