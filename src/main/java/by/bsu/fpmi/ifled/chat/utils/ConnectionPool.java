package by.bsu.fpmi.ifled.chat.utils;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Level;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ConnectionPool {

    private static final Logger logger =
            LogManager.getLogger(ConnectionPool.class);
    private static final Marker CONNECTION_POOL =
            MarkerManager.getMarker("CONNECTION_POOL");

    private static HashMap<ArrayList<String>, DataSource> dataSources =
                                    new HashMap<ArrayList<String>, DataSource>();

    public synchronized static Connection getConnection(String dbDriver,
                                                        String dbName,
                                                        String dbUsername,
                                                        String dbPass) {
        logger.entry(dbDriver, dbName, dbUsername, dbPass);

        Connection connection = null;
        ArrayList<String> key = new ArrayList<String>(Arrays.asList(dbDriver,
                                                    dbName, dbUsername, dbPass));
        logger.debug(CONNECTION_POOL, key);
        DataSource dataSource = dataSources.get(key);
        logger.debug(CONNECTION_POOL, dataSource);
        if (dataSource == null) {
            logger.trace("dataSource == null");
            dataSources.put(key, setupDataSource(dbDriver, dbName, dbUsername,
                    dbPass));
            dataSource = dataSources.get(key);

        }
        logger.debug(CONNECTION_POOL, dataSource);

        try {
            connection = dataSource.getConnection();
        }
        catch (SQLException se) {
            logger.catching(Level.TRACE, se);
        }

        logger.debug(CONNECTION_POOL,
                "active: " + ((BasicDataSource)dataSource).getNumActive()+
                " idle: " + ((BasicDataSource)dataSource).getNumIdle());

        if (connection != null) {
            logger.debug(CONNECTION_POOL, connection.hashCode());
        }
        return logger.exit(connection);
    }
    public static DataSource setupDataSource(String dbDriver, String dbName,
                                             String dbUsername, String dbPass) {
        logger.entry(dbDriver, dbName, dbUsername, dbPass);

        try {
            Class.forName(dbDriver);
        }
        catch (ClassNotFoundException cfe) {
            logger.catching(Level.TRACE, cfe);
        }

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(dbDriver);
        ds.setUrl(dbName);
        ds.setUsername(dbUsername);
        ds.setPassword(dbPass);
        ds.setMaxActive(-1);

        return logger.exit(ds);
    }
}
