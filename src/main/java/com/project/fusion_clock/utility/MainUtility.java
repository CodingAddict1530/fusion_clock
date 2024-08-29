package com.project.fusion_clock.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;

public class MainUtility {

    /**
     * The logger for the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(MainUtility.class);

    public static boolean checkIfTableExists(String table) {

        try (Connection conn = DatabaseUtility.connect()) {
            ResultSet rs = DatabaseUtility.executeQuery(conn, "SELECT id FROM " + table + " LIMIT 1", table);
            boolean exists = rs.next();
            rs.getStatement().close();
            return exists;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

    }

}
