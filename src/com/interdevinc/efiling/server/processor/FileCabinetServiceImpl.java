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
import com.interdevinc.efiling.client.model.Broker;
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.DocumentType;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.ScannedDocument;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.FileCabinetService;

public class FileCabinetServiceImpl extends RemoteServiceServlet implements FileCabinetService {

    private static final long serialVersionUID = 1L;

    private final String host = "jdbc:mysql://192.168.11.6/";
    
    private final String efilingDatabase = "efilingsys";
    private final String efilingUsernameRead = "efilingRead";
    private final String efilingPasswordRead = "TUPMVfwTAEE8dTjv";
    private final String efilingUsernameWrite = "efilingWrite";
    private final String efilingPasswordWrite = "JERQUqGp74RUhN9d";
    
    private final String tradeDataDatabase = "clearingdata";
    private final String tradeUsernameRead = "tradeDataRead";
    private final String tradePasswordRead = "7rxLBUc5duVrWRZ2";

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
	
	try {
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	
	return loadedFileCabinet;
	
    }
 
    /**
     * METHOD: RETRIEVE SEARCH COMPONENTS
     * @return searchComponents
     */
    public SearchComponents retrieveSearchComponents(FileCabinet fc) {
	
	loadedFileCabinet = fc;
	searchComponents = new SearchComponents();
	
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    loadBrokerInformation();
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    loadClientInformation();
	}

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

	try {
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}	
	
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
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
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
    
    /**
     * METHOD: LOAD DOCUMENT TYPES INTO FILE CABINET (retrieveFileCabinetContents)
     * Creates an ArrayList of DocumentType objects, sets FileCabinet with DocumentType.
     */
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
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
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
    
    /**
     * METHOD: LOAD SCANNED DOCUMENTS INTO FILE CABINET (retrieveFileCabinetContents)
     * Creates an ArrayList of ScannedDocument objects, sets FileCabinet with ScannedDocument
     */
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
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
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
    
    /**
     * METHOD: LOAD BROKER INFORMATION (retrieveSearchComponents)
     * Creates an ArrayList of Broker objects, sets searchComponent Broker List.
     */
    private void loadBrokerInformation() {
	
	final String brokerInfoQuery = "SELECT clearingdata.eamUsers.firstName, clearingdata.eamUsers.lastName, clearingdata.eamUsers.repNum FROM clearingdata.eamUsers WHERE repNum IS NOT NULL ORDER BY lastName";

	ArrayList<Broker> brokerInfo = new ArrayList<Broker>();
	
	try{

	    //init connection and statement
	    connection = getConnection(tradeDataDatabase, tradeUsernameRead, tradePasswordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(brokerInfoQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    brokerInfo.add(new Broker(results.getString(1), results.getString(2), results.getString(3)));
		}
		
		searchComponents.setBrokerList(brokerInfo);
		
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
    
    /**
     * METHOD: LOAD CLIENT INFORMATION (retrieveSearchComponents)
     * Creates an ArrayList of Client objects, sets searchComponent Client List.
     */
    private void loadClientInformation() {
	
	final String clientInfoQuery = "SELECT acctNum, firstName, lastName FROM clientInfo ORDER BY lastName";

	ArrayList<Client> clientInfo = new ArrayList<Client>();
	
	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
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
    
    /**
     * METHOD: LOAD DOCUMENT TYPE INFORMATION (retrieveSearchComponents)
     * Creates an ArrayList of DocumentType objects, sets searchComponents Document Type List
     */
    private void loadDocumentTypeInformation() {

	ArrayList<DocumentType> documentTypeInfo = new ArrayList<DocumentType>();
	
	String databaseName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    databaseName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    databaseName = "clientDocType";
	} else {
	    databaseName = "";
	}
	
	final String documentTypeInfoQuery = "SELECT catID, catName, catAbbr FROM " + databaseName + " ORDER BY catName";

	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
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
    private Connection getConnection(String database, String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	String url = host + database;
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	return DriverManager.getConnection(url, username, password);
    }

}