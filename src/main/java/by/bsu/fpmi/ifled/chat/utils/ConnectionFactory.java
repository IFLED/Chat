package by.bsu.fpmi.ifled.chat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    //private static final Logger logger = LogManager.getLogger(ConnectionFactory.class);

    final static String DB_DRIVER = "org.postgresql.Driver";
    final static String DB_NAME = "jdbc:postgresql://localhost:5432/logging";
    final static String DB_USERNAME = "IFLED";
    final static String DB_PASSWORD = "4321";

    public static Connection getDatabaseConnection() throws SQLException {

        //logger.entry();
        //logger.info("getDatabaseConnection");
        Connection connection = null;

        try {
            //logger.trace("try to load " + DB_DRIVER);
            Class.forName(DB_DRIVER);

            //logger.trace("try to get connection");
            connection = DriverManager.getConnection(DB_NAME, DB_USERNAME,
                    DB_PASSWORD);
            //logger.trace("done!");
        }
        catch (ClassNotFoundException cfe) {
            //logger.catching(cfe);
        }
        catch (SQLException se) {
            //logger.catching(se);
        }
        //return logger.exit(connection);
        return connection;
    }

}
