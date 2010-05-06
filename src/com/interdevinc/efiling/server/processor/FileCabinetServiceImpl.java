package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.interdevinc.efiling.client.model.AccessControl;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.processor.FileCabinetService;

public class FileCabinetServiceImpl extends RemoteServiceServlet implements FileCabinetService {

    private static final long serialVersionUID = 1L;

    private final String url="jdbc:mysql://192.168.11.6/efilingsys";
    private final String usernameRead = "efilingRead";
    private final String passwordRead = "TUPMVfwTAEE8dTjv";
    private final String usernameWrite = "efilingWrite";
    private final String passwordWrite = "JERQUqGp74RUhN9d";

    private Connection connection;
    private Statement statement;
    private ResultSet results;

    private AuthenticatedUser authenticatedUser;
    private ArrayList<FileCabinet> fileCabinets;

    public FileCabinetServiceImpl() {
    }

    
    public ArrayList<FileCabinet> retrieveUsableFileCabinets(AuthenticatedUser au) {

	authenticatedUser = au;

	retrieveUsersFileCabinets();

	return fileCabinets;
    }

    /**
     * METHOD: RETRIEVE ALL FILE CABINETS (retrieveUsableFileCabinets)
     * Checks the users access against the resourceID.
     * Sets an arraylist of FileCabinet with the users file cabinets.
     */
    private void retrieveUsersFileCabinets() {
	
	fileCabinets = new ArrayList<FileCabinet>();
	
	final String availableFileCabinetQuery = "SELECT cabinetID, cabinetName, resourceID FROM FilingCabinet";

	try{

	    //init connection and statement
	    connection = getConnection(usernameRead, passwordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(availableFileCabinetQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    for (AccessControl accessControl : authenticatedUser.getAccessControl()) {
			if (accessControl.getResourceID().equals(results.getString(3))) {
			    fileCabinets.add(new FileCabinet(results.getString(1), results.getString(2), results.getString(3)));
			}
		    }
		}
	    }

	    //close all processing objects
	    results.close();
	    statement.close();
	    connection.close();			

	}catch (InstantiationException e){
	    e.printStackTrace();
	}catch (IllegalAccessException e){
	    e.printStackTrace();
	}catch (ClassNotFoundException e){
	    e.printStackTrace();
	}catch (SQLException e){
	    e.printStackTrace();
	}

    }

    private void createConnectionAccessRights() {

	for (AccessControl accessControl: authenticatedUser.getAccessControl()) {
	    if (accessControl.getRoleID().equals("admin")) {

		break;
	    }
	}

    }

    /**
     * METHOD: GET CONNECTION
     * @return
     * @throws InstantiationException 
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException */
    private Connection getConnection(String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	return DriverManager.getConnection(url,username,password);
    }

}