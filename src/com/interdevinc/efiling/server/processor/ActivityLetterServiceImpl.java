package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.interdevinc.efiling.client.model.ActivityLetter;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.ScannedDocument;
import com.interdevinc.efiling.client.processor.ActivityLetterService;

public class ActivityLetterServiceImpl extends RemoteServiceServlet implements ActivityLetterService {

    /**
     * The serializable class ClientInformationServiceImpl needs a static final serialVersionUID field of type long.
     */
    private static final long serialVersionUID = -5777942719768950058L;

    private Connection connection;
    private Statement statement;
    private ResultSet results;

    private AuthenticatedUser authenticatedUser;
    private String accountNumber;
    private String dateSent;

    private SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * CONSTRUCTOR: ACTIVITY LETTER SERVICE IMPL
     */
    public ActivityLetterServiceImpl() {
    }

    /**
     * METHOD: ADD NEW ACTIVITY LETTER
     * Checks for an existing entry & inserts a new entry in the database.
     */
    public String addNewActivityLetter(AuthenticatedUser au, String an, String ds) {

	authenticatedUser = au;
	accountNumber = an;
	dateSent = ds;

	String resultMessage = null;

	if (!checkForExistingEntry()) {
	    if (insertActivityLetterData()) {
		resultMessage = "New Activity Letter created.";
	    } else {
		resultMessage = "Activity Letter was not created due to a database issue. Please contact support.";
	    }
	} else {
	    resultMessage = "An Activity Letter already exists for the Client/Date Sent combination selected.";
	}

	return resultMessage;
    }

    /**
     * METHOD: RETRIEVE CLIENTS WITH NULL UPDATES
     * @return activityLetter
     * Returns an arraylist of ActivityLetters where the receieved fields is null. This means an activity letter exists and needs updating.
     */
    public ArrayList<ActivityLetter> retrieveClientsWithNullUpdates(AuthenticatedUser au) {

	ArrayList<ActivityLetter> activityLetter = new ArrayList<ActivityLetter>();

	final String selectQuery = "SELECT letterID, accountNumber, dateSent, dateReceieved, scannedDocumentID, misc FROM `ActivityLetter` WHERE dateReceieved IS NULL";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
	    statement = connection.createStatement();

	    statement.execute(selectQuery);
	    results = statement.getResultSet();

	    if (!results.next() ) {
		activityLetter = null;
	    } else {
		do {
		    activityLetter.add(new ActivityLetter(results.getString(1), results.getString(2), results.getDate(3), results.getDate(4), results.getString(5), results.getString(6)));
		} while (results.next());
	    }

	    //close all processing objects
	    results.close();
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}

	return activityLetter;
    }

    /**
     * METHOD: RETRIEVE SCANNED ACTIVITY LETTER FOR CLIENT
     * @return scannedDocument
     * Returns an arraylist of ScannedDocuments where the client account number matches the request.
     */
    public ArrayList<ScannedDocument> retrieveScannedActivityLetterForClient(AuthenticatedUser au, String acctNum) {

	ArrayList<ScannedDocument> scannedDocument = new ArrayList<ScannedDocument>();

	final String selectQuery = "SELECT uploadId, fileName, fileSize, fileType, uploadDate FROM `clientFileLocation` WHERE clientAcctNum='"+acctNum+"' AND docuType='AL'";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
	    statement = connection.createStatement();

	    statement.execute(selectQuery);
	    results = statement.getResultSet();

	    if (!results.next() ) {
		scannedDocument = null;
	    } else {
		do {
		    scannedDocument.add(new ScannedDocument(results.getString(1), results.getString(2), results.getString(3), results.getString(4), acctNum, "AL", results.getString(5)));
		} while (results.next());
	    }

	    //close all processing objects
	    results.close();
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}

	return scannedDocument;

    }

    /**
     * METHOD: UPDATE ACTIVITY LETTER
     * @return resultMessage
     * Updates the ActivityLetter table with a dateReceived, and scannedDocument
     */
    public String updateActivityLetter(AuthenticatedUser au, ActivityLetter al) {
	
	String resultMessage = new String();

	final String insertQuery = "UPDATE ActivityLetter SET dateReceieved='"+databaseFormat.format(al.getDateReceived())+"', scannedDocumentID='"+al.getScannedDocument()+"', misc='"+al.getMisc()+"' WHERE letterID='"+al.getLetterID()+"' ";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    //int insertedRows = statement.executeUpdate(insertQuery);

	    if (statement.executeUpdate(insertQuery) > 0) {
		resultMessage = "Activity Letter updated successfully.";
	    } else {
		resultMessage = "Database error. Please contact support.";
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
     * METHOD: CHECK FOR EXISTING ENTRY
     * @return entryExists
     * Checks the ActivityLetter table for a record that matches the accountNumber and dateSent
     */
    private boolean checkForExistingEntry() {

	boolean entryExists = true;

	final String selectQuery = "SELECT letterID FROM ActivityLetter WHERE accountNumber='"+accountNumber+"' AND dateSent='"+dateSent+"'";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
	    statement = connection.createStatement();

	    statement.execute(selectQuery);
	    results = statement.getResultSet();

	    if (!results.next()) {
		entryExists = false;
	    }

	    //close all processing objects
	    results.close();
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}

	return entryExists;

    }

    /**
     * METHOD: INSERT ACTIVITY LETTER DATA
     * @return dataEntered
     * Inserts a new record into the ActivityLetter table.
     */
    private boolean insertActivityLetterData() {

	boolean dataEntered = false;

	final String insertQuery = "INSERT INTO ActivityLetter (accountNumber, dateSent) VALUES ('"+accountNumber+"', '"+dateSent+"')";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    //int insertedRows = statement.executeUpdate(insertQuery);

	    if (statement.executeUpdate(insertQuery) > 0) {
		dataEntered = true;
	    }

	    //close all processing objects
	    statement.close();		
	    connection.close();

	}catch (SQLException e){
	    e.printStackTrace();
	}

	return dataEntered;

    }

}
