package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.Client;
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
    private Client clientInfo;
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
    public String addClientInformation(AuthenticatedUser au, FileCabinet fc, String cfn, String cln, String can, String crn) {
	
	authenticatedUser = au;
	loadedFileCabinet = fc;
	clientFirstName = cfn;
	clientLastName = cln;
	clientAccountNumber = can;
	clientRepNumber = crn;
	    
	// If the Client Account Number is already in the database return without making any changes.
	if (checkForClientExistance()) {
	    return "A Client: " + clientAccountNumber + " already exists.";
	}
	
	insertClientInfo();
	
	return resultMessage;
    }
    
    /**
     * METHOD: DELETE CLIENT INFORMATION
     * @return resultMessage
     * Deletes a key by clientID.
     */
    public String deleteClientInformation(AuthenticatedUser au, FileCabinet fc, String cid) {
	
	authenticatedUser = au;
	loadedFileCabinet = fc;
	clientID = cid;
	
	final String deleteQuery = "DELETE FROM clientInfo WHERE `key`='" + clientID + "'";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int insertedRows = statement.executeUpdate(deleteQuery);

	    //close all processing objects
	    statement.close();		
	    connection.close();
	    
	    if (insertedRows > 0) {
		retrieveSelectedClient(clientID);
		logClientInfoAttempt("deleteClient", true);
		resultMessage = new String("Deleted Client: " + clientID );
	    } else {
		logClientInfoAttempt("deleteClient", false);
		resultMessage = new String("Error deleting client information: " + clientID);
	    }
	    
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
    public String editClientInformation(AuthenticatedUser au, FileCabinet fc, String cfn, String cln, String can, String crn, String cid) {
	
	authenticatedUser = au;
	loadedFileCabinet = fc;
	clientFirstName = cfn;
	clientLastName = cln;
	clientAccountNumber = can;
	clientRepNumber = crn;
	clientID = cid;
	
	updateClientInfo();
	
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
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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
    private void insertClientInfo() {
	
	final String insertQuery = "INSERT INTO clientInfo (acctNum, lastName, firstName, repNum) VALUES ('" + clientAccountNumber + "', '" + clientLastName + "', '" + clientFirstName + "', '" + clientRepNumber + "')";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int insertedRows = statement.executeUpdate(insertQuery);

	    
	    //close all processing objects
	    statement.close();		
	    connection.close();
	    
	    if (insertedRows > 0) {
		retrieveSelectedClient(selectLastClientID());
		logClientInfoAttempt("addClient", true);
		resultMessage = new String("Added Client: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName);
	    } else {
		logClientInfoAttempt("addClient", false);
		resultMessage = new String("Error adding client information: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName);
	    }

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
    }
    
    /**
     * METHOD: LOG CLIENT INFO ATTEMPT
     */
    private void logClientInfoAttempt(String editType, boolean wasWritten) {
	
	String action;
	if (editType.equals("addClient")) {
	    action = "addClient_|" + clientInfo.getClientID() + "|" + clientInfo.getAccountNumber() + "|" + clientInfo.getLastName() + "|" + clientInfo.getFirstName() + "|" + clientInfo.getRepNumber();
	} else if (editType.equals("deleteClient")) {
	    action = "deleteClient_|" + clientInfo.getClientID() + "|" + clientInfo.getAccountNumber() + "|" + clientInfo.getLastName() + "|"  + clientInfo.getFirstName() + "|" + clientInfo.getRepNumber();
	} else if (editType.equals("editClient")) {
	    action = "editClient_|" + clientID;
	} else {
	    action = "client_unknown_action";
	}
	
	int status;
	
	if (wasWritten) {
	    status = 1;
	} else {
	    status = 0;
	}
	
	final String logQuery = "INSERT INTO UsageLog (`user`, `resource`, `action`, `status`) VALUES ('"+authenticatedUser.getUsername()+"', 'efiling', '"+action+"', '"+status+"')";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.executeUpdate(logQuery);

	    //close all processing objects
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
    }
    
    /**
     * METHOD: RETRIEVE SELECTED CLIENT
     * Retrieves clientInfo record based on clientID/key sets clientInfo var.
     */
    private void retrieveSelectedClient(String cid) {
	
	final String selectQuery = "SELECT acctNum, lastName, firstName, repNum FROM `clientInfo` WHERE `key`='"+cid+"'";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(selectQuery);
	    results = statement.getResultSet();

	    if (results.next()) {
		clientInfo = new Client(cid, results.getString(1), results.getString(2), results.getString(3), results.getString(4));
	    }

	    //close all processing objects
	    results.close();
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}
    }
    
    /**
     * METHOD: SELECT LAST CLIENT
     * @return cid
     * Retrieves the last clientID/key in the clientInfo database.
     */
    private String selectLastClientID() {
	
	String cid = new String();
	
	final String selectQuery = "SELECT MAX(`key`) FROM `clientInfo`";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(selectQuery);
	    results = statement.getResultSet();

	    if (results.next()) {
		cid = results.getString(1);
	    } else {
		cid = "0";
	    }

	    //close all processing objects
	    results.close();
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
	return cid;
	
    }
    
    /**
     * METHOD: UPDATE CLIENT INFORMATION (editClientInformation)
     * Updates the acctNum, lastName, firstName, repNum where the old key. Sets the result message.
     */
    private void updateClientInfo() {
	
	final String updateQuery = "UPDATE clientInfo SET acctNum='" + clientAccountNumber + "', lastName='" + clientLastName + "', firstName='" + clientFirstName + "', repNum='" + clientRepNumber + "' WHERE `key`='" + clientID + "'";
	
	//System.out.println(updateQuery);
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int updatedRows = statement.executeUpdate(updateQuery);

	    //close all processing objects
	    statement.close();		
	    connection.close();
	    
	    if (updatedRows > 0) {
		logClientInfoAttempt("editClient", true);
		resultMessage = new String("Updated client: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName + " | " + clientRepNumber);
	    } else {
		logClientInfoAttempt("editClient", false);
		resultMessage = new String("Error updating client information: " + clientAccountNumber + " - " + clientLastName + ", " + clientFirstName + " | " + clientRepNumber);
	    }
	    
	}catch (SQLException e){
	    e.printStackTrace();
	}
    }
    
}
