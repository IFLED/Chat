package by.bsu.fpmi.ifled.chat.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    final static String DB_DRIVER = "org.postgresql.Driver";
    final static String DB_NAME = "jdbc:postgresql://localhost:5432/logging";
    final static String DB_USERNAME = "IFLED";
    final static String DB_PASSWORD = "4321";

    public static Connection getDatabaseConnection() throws SQLException {
        return ConnectionPool.getConnection(DB_DRIVER, DB_NAME, DB_USERNAME,
                                            DB_PASSWORD);
    }

}
