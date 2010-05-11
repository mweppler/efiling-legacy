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
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.DocumentType;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.ScannedDocument;
import com.interdevinc.efiling.client.model.SearchComponents;
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
    private ArrayList<FileCabinet> usersFileCabinets;
    private FileCabinet loadedFileCabinet;
    private SearchComponents searchComponents;

    /**
     * CONSTRUCTOR: FILE CABINET SERVICE IMPL
     */
    public FileCabinetServiceImpl() {
    }


    /**
     * METHOD: RETRIEVE FILE CABINET CONTENTS
     * @return loadedFileCabinet
     */
    public FileCabinet retrieveFileCabinetContents(FileCabinet fc) {
	
	loadedFileCabinet = fc;
	
	loadDocumentTypesIntoFileCabinet();
	
	loadScannedDocumentsIntoFileCabinet();
	
	return loadedFileCabinet;
	
    }
 
    public SearchComponents retrieveSearchComponents() {
	
	searchComponents = new SearchComponents();
	
	loadClientInformation();
	loadDocumentTypeInformation();
	
	return searchComponents;
    }
    
    /**
     * METHOD: RETRIEVE USABLE FILE CABINETS
     * @return usersFileCabinets
     * Returns a users usable file cabinets.
     */
    public ArrayList<FileCabinet> retrieveUsableFileCabinets(AuthenticatedUser au) {

	authenticatedUser = au;

	retrieveAuthenticatedUsersFileCabinets();

	return usersFileCabinets;
    }

    /**
     * METHOD: RETRIEVE ALL FILE CABINETS (retrieveUsableFileCabinets)
     * Checks the users access against the resourceID.
     * Sets an arraylist of FileCabinet with the users file cabinets.
     */
    private void retrieveAuthenticatedUsersFileCabinets() {
	
	usersFileCabinets = new ArrayList<FileCabinet>();
	
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
			    usersFileCabinets.add(new FileCabinet(results.getString(1), results.getString(2), results.getString(3)));
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
    
    private void loadDocumentTypesIntoFileCabinet() {
	
	ArrayList<DocumentType> documentTypes = new ArrayList<DocumentType>();
	
	String databaseName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    databaseName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    databaseName = "clientDocType";
	} else {
	    databaseName = "";
	}
	
	final String documentTypeQuery = "SELECT catID, catName, catAbbr FROM " + databaseName;

	try{

	    //init connection and statement
	    connection = getConnection(usernameRead, passwordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(documentTypeQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    documentTypes.add(new DocumentType(results.getString(1), results.getString(2), results.getString(3)));
		}
		
		loadedFileCabinet.setDocumentType(documentTypes);
		
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
    
    private void loadScannedDocumentsIntoFileCabinet() {
	
	ArrayList<ScannedDocument> scannedDocuments = new ArrayList<ScannedDocument>();
	
	String databaseName = new String();
	String groupedBy = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    databaseName = "brokerFileLocation";
	    groupedBy = "repNum";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    databaseName = "clientFileLocation";
	    groupedBy = "clientAcctNum";
	} else {
	    databaseName = "";
	    groupedBy = "";
	}
	
	final String scannedDocumentQuery = "SELECT uploadId, fileName, fileSize, fileType, " + groupedBy + ", docuType, uploadDate FROM " + databaseName;

	try{

	    //init connection and statement
	    connection = getConnection(usernameRead, passwordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(scannedDocumentQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    scannedDocuments.add(new ScannedDocument(results.getString(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7)));
		}
		
		loadedFileCabinet.setScannedDocument(scannedDocuments);
		
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
    
    private void loadClientInformation() {
	
	ArrayList<Client> clientInfo = new ArrayList<Client>();
	
	final String clientInfoQuery = "SELECT acctNum, firstName, lastName FROM clientInfo ORDER BY lastName";

	try{

	    //init connection and statement
	    connection = getConnection(usernameRead, passwordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(clientInfoQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    clientInfo.add(new Client(results.getString(1), results.getString(2), results.getString(3)));
		}
		
		searchComponents.setClientList(clientInfo);
		
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
    
    private void loadDocumentTypeInformation() {

	ArrayList<DocumentType> documentTypeInfo = new ArrayList<DocumentType>();
	
	final String documentTypeInfoQuery = "SELECT catID, catName, catAbbr FROM clientDocType ORDER BY catName";

	try{

	    //init connection and statement
	    connection = getConnection(usernameRead, passwordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(documentTypeInfoQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    documentTypeInfo.add(new DocumentType(results.getString(1), results.getString(2), results.getString(3)));
		}
		
		searchComponents.setDocumentTypeList(documentTypeInfo);
		
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