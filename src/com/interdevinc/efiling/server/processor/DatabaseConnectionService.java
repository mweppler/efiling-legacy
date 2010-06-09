package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionService {

    private static final String host = "jdbc:mysql://192.168.11.6/";

    private static final String authDatabase = "eamAppAuth";
    private static final String authUsername = "eamAppAuth";
    private static final String authPassword = "XVSxQaTeFhNpHLhn";

    private static final String efilingDatabase = "efilingsys";
    private static final String efilingUsernameRead = "efilingRead";
    private static final String efilingPasswordRead = "TUPMVfwTAEE8dTjv";
    private static final String efilingUsernameWrite = "efilingWrite";
    private static final String efilingPasswordWrite = "JERQUqGp74RUhN9d";

    private static final String tradeDataDatabase = "clearingdata";
    private static final String tradeUsernameRead = "tradeDataRead";
    private static final String tradePasswordRead = "7rxLBUc5duVrWRZ2";

    private static String database;
    private static String username;
    private static String password;

    private static Connection connection;

    public static Connection retrieveDatabaseConnection(String dbName, String dbAccess) {
	setConnectionCredentials(dbName, dbAccess);
	try {
	    setDBConnection();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return getDBConnection();
    }
    
    /**
     * METHOD: GET DB CONNECTION
     * @return connection
     */
    private static Connection getDBConnection() {
	return connection;
    }

    /**
     * METHOD: SET CONNECTION CREDENTIALS
     * @param dbName
     * @param dbAccess
     */
    private static void setConnectionCredentials(String dbName, String dbAccess) {

	if (dbName.equals("eamAppAuth")) {
	    database = authDatabase;
	    username = authUsername;
	    password = authPassword;
	} else if (dbName.equals("efilingsys")) {
	    database = efilingDatabase;
	    if (dbAccess.equals("READ")) {
		username = efilingUsernameRead;
		password = efilingPasswordRead;
	    } else if (dbAccess.equals("WRITE")) {
		username = efilingUsernameWrite;
		password = efilingPasswordWrite;
	    }
	} else if (dbName.equals("clearingdata")) {
	    database = tradeDataDatabase;
	    username = tradeUsernameRead;
	    password = tradePasswordRead;
	}

    }

    /**
     * METHOD: SET DB CONNECTION
     * @throws InstantiationException 
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException */
    private static void setDBConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	String url = host + database;
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	connection = DriverManager.getConnection(url, username, password);
    }

}
