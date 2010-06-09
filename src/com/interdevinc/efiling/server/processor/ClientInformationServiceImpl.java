package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.processor.ClientInformationService;

public class ClientInformationServiceImpl extends RemoteServiceServlet implements ClientInformationService {

    /**
     * The serializable class ClientInformationServiceImpl needs a static final serialVersionUID field of type long.
     */
    private static final long serialVersionUID = 4028094314825595722L;

    private Connection connection;
    private Statement statement;
    private ResultSet results;

    private AuthenticatedUser authenticatedUser;
    private FileCabinet loadedFileCabinet;

    private String clientFirstName;
    private String clientLastName;
    private String clientAccountNumber;
    private String clientRepNumber;
    private String clientID;
    private String resultMessage;
    
    /**
     * CONSTRUCTOR: CLIENT INFORMATION SERVICE IMPL
     */
    public ClientInformationServiceImpl() {
    }
    
    /**
     * METHOD: ADD CLIENT INFORMATION
     * @return resultMessage
     * Calls checkForClientExistance() if no existing client insertClientInformation().
     */
    public String addClientInformation(FileCabinet fc, String cfn, String cln, String can, String crn) {
	
	loadedFileCabinet = fc;
	clientFirstName = cfn;
	clientLastName = cln;
	clientAccountNumber = can;
	clientRepNumber = crn;
	    
	// If the Client Account Number is already in the database return without making any changes.
	if (checkForClientExistance()) {
	    return "A Client: " + clientAccountNumber + " already exists.";
	}
	
	insertDocumentType();
	
	return resultMessage;
    }
    
    /**
     * METHOD: DELETE CLIENT INFORMATION
     * @return resultMessage
     * Deletes a key by clientID.
     */
    public String deleteClientInformation(FileCabinet fc, String cid) {
	
	loadedFileCabinet = fc;
	clientID = cid;
	
	final String deleteQuery = "DELETE FROM clientInfo WHERE `key`='" + clientID + "'";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int insertedRows = statement.executeUpdate(deleteQuery);

	    if (insertedRows > 0) {
		resultMessage = new String("Deleted Client: " + clientID );
	    } else {
		resultMessage = new String("Error deleting client information: " + clientID);
	    }
	    
	    //close all processing objects
	    statement.close();		
	    connection.close();
	    
	}catch (SQLException e){
	    e.printStackTrace();
	}
	
	return resultMessage;
    }
    
    /**
     * METHOD: EDIT CLIENT INFORMATION
     * @return resultMessage
     * Updates a acctNum, lastName, firstName, repNum by key.
     */
    public String editClientInformation(FileCabinet fc, String cfn, String cln, String can, String crn, String cid) {
	
	loadedFileCabinet = fc;
	clientFirstName = cfn;
	clientLastName = cln;
	clientAccountNumber = can;
	clientRepNumber = crn;
	clientID = cid;
	
	updateDocumentType();
	
	return resultMessage;
    }

    /**
     * METHOD: CHECK FOR CLIENT EXISTANCE (addClientInformation)
     * Checks the client table for an existing account number. If there is an existing acctNum, quits and sets the result message.
     */
    private boolean checkForClientExistance() {
	
	boolean clientExists = false;
	
	final String searchQuery = "SELECT acctNum FROM clientInfo WHERE acctNum='" + clientAccountNumber + "'";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("eamAppAuth", "READ");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(searchQuery);
	    results = statement.getResultSet();

	    if (results.next()) {
		clientExists = true;
	    }

	    //close all processing objects
	    results.close();
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
	return clientExists;
    }
    
    /**
     * METHOD: INSERT CLIENT INFORMATION (addClientInformation)
     * Inserts the acctNum, lastName, firstName, repNum into the clientInfo table. Sets the result message.
     */
    private void insertDocumentType() {
	
	final String insertQuery = "INSERT INTO clientInfo (acctNum, lastName, firstName, repNum) VALUES ('" + clientAccountNumber + "', '" + clientLastName + "', '" + clientFirstName + "', '" + clientRepNumber + "')";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int insertedRows = statement.executeUpdate(insertQuery);

	    if (insertedRows > 0) {
		resultMessage = new String("Added Client: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName);
	    } else {
		resultMessage = new String("Error adding client information: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName);
	    }
	    
	    //close all processing objects
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
    }
    
    /**
     * METHOD: UPDATE CLIENT INFORMATION (editClientInformation)
     * Updates the acctNum, lastName, firstName, repNum where the old key. Sets the result message.
     */
    private void updateDocumentType() {
	
	final String updateQuery = "UPDATE clientInfo SET acctNum='" + clientAccountNumber + "', lastName='" + clientLastName + "', firstName='" + clientFirstName + "', repNum='" + clientRepNumber + "' WHERE `key`='" + clientID + "'";
	
	System.out.println(updateQuery);
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int updatedRows = statement.executeUpdate(updateQuery);

	    if (updatedRows > 0) {
		resultMessage = new String("Updated client: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName + " | " + clientRepNumber);
	    } else {
		resultMessage = new String("Error updating client information: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName + " | " + clientRepNumber);
	    }
	    
	    //close all processing objects
	    statement.close();		
	    connection.close();
	    
	}catch (SQLException e){
	    e.printStackTrace();
	}
    }
    
}
