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

    /**
     * The serializable class FileCabinetServiceImpl needs a static final serialVersionUID field of type long.
     */
    private static final long serialVersionUID = 1642153038873292865L;

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
    private ArrayList<ScannedDocument> scannedDocuments;

    private String documentTypeName;
    private String documentTypeAbbr;
    private String documentTypeAbbrOld;
    private String resultMessage;
    
    /**
     * CONSTRUCTOR: FILE CABINET SERVICE IMPL
     */
    public FileCabinetServiceImpl() {
    }

    /**
     * METHOD: ADD DOCUMENT TYPE
     * @return resultMessage
     * Calls checkForDocumentTypeExistance() if no existing document type insertDocumentType().
     */
    public String addDocumentType(FileCabinet fc, String dtn, String dta) {
	
	loadedFileCabinet = fc;
	documentTypeName = dtn;
	documentTypeAbbr = dta;
	
	// If the Document Abbr is already in the database return without making any changes.
	if (checkForDocumentTypeExistance()) {
	    return "A document type: " + documentTypeAbbr + " already exists.";
	}
	
	insertDocumentType();
	
	return resultMessage;
    }
    
    /**
     * METHOD: DELETE DOCUMENT TYPE
     * @return resultMessage
     * Deletes a catAbbr by catAbbr.
     */
    public String deleteDocumentType(FileCabinet fc, String dta) {
	
	loadedFileCabinet = fc;
	documentTypeAbbr = dta;
	
	// Table modifiers
	String tableName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    tableName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    tableName = "clientDocType";
	} else {
	    tableName = "";
	}
	
	final String deleteQuery = "DELETE FROM " + tableName + " WHERE catAbbr='" + documentTypeAbbr + "'";
	
	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameWrite, efilingPasswordWrite);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int insertedRows = statement.executeUpdate(deleteQuery);

	    if (insertedRows > 0) {
		resultMessage = new String("Deleted document type: " + documentTypeName + " - " + documentTypeAbbr);
	    } else {
		resultMessage = new String("Error deleting document type: " + documentTypeName + " - " + documentTypeAbbr);
	    }
	    
	    //close all processing objects
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
	
	return resultMessage;
    }
    
    /**
     * METHOD: EDIT DOCUMENT TYPE
     * @return resultMessage
     * Updates a catName & catAbbr by catAbbr.
     */
    public String editDocumentType(FileCabinet fc, String dtn, String dta, String dtao) {
	
	loadedFileCabinet = fc;
	documentTypeName = dtn;
	documentTypeAbbr = dta;
	documentTypeAbbrOld = dtao;
	
	updateDocumentType();
	
	return resultMessage;
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
     * METHOD: RETRIEVE SEARCH RESULTS
     * @return scannedDocuments
     * Returns an ArrayList of ScannedDocument objects.
     */
    public ArrayList<ScannedDocument> retrieveSearchResults(FileCabinet fc, String n, String d) {
	
	scannedDocuments = new ArrayList<ScannedDocument>();
	
	// Table modifiers
	String tableName = new String();
	String groupedBy = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    tableName = "brokerFileLocation";
	    groupedBy = "repNum";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    tableName = "clientFileLocation";
	    groupedBy = "clientAcctNum";
	} else {
	    tableName = "";
	    groupedBy = "";
	}
	
	// Query modifiers
	StringBuilder where = new StringBuilder(" WHERE ");
	if (n != null) {
	    where.append(groupedBy + "='" + n + "' AND ");
	}
	if (d != null) {
	    where.append("docuType='" + d + "' AND ");
	}
	String whereClause = where.substring(0, (where.length() - 4)).toString();
	
	final String searchQuery = "SELECT uploadId, fileName, ROUND(fileSize/1024), fileType, " + groupedBy + ", docuType, DATE_FORMAT(uploadDate, '%M %e %Y') FROM " + tableName + whereClause + "ORDER BY fileName ASC, uploadDate DESC";
	
	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(searchQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    scannedDocuments.add(new ScannedDocument(results.getString(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7)));
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
	
	return scannedDocuments;
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
     * METHOD: CHECK FOR DOCUMENT TYPE EXISTANCE (addDocumentType)
     * Checks the document type table for an existing catAbbr. If there is an existing catAbbr, quits and sets the result message.
     */
    private boolean checkForDocumentTypeExistance() {
	
	boolean documentTypeExists = false;
	
	// Table modifiers
	String tableName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    tableName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    tableName = "clientDocType";
	} else {
	    tableName = "";
	}
	
	final String searchQuery = "SELECT catID FROM " + tableName + " WHERE catName='" + documentTypeName + "' AND catAbbr='" + documentTypeAbbr + "'";
	
	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(searchQuery);
	    results = statement.getResultSet();

	    if (results.next()) {
		documentTypeExists = true;
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
	
	return documentTypeExists;
    }
    
    /**
     * METHOD: CREATE CONNECTION ACCESS RIGHTS (NOT YET IMPLEMENTED)
     */
    private void createConnectionAccessRights() {

	for (AccessControl accessControl: authenticatedUser.getAccessControl()) {
	    if (accessControl.getRoleID().equals("admin")) {

		break;
	    }
	}

    }
    
    /**
     * METHOD: INSERT DOCUMENT TYPE (addDocumentType)
     * Inserts the catName & catAbbr into the document type table. Sets the result message.
     */
    private void insertDocumentType() {
	
	// Table modifiers
	String tableName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    tableName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    tableName = "clientDocType";
	} else {
	    tableName = "";
	}
	
	final String insertQuery = "INSERT INTO " + tableName + " (catName, catAbbr) VALUES ('" + documentTypeName + "', '" + documentTypeAbbr + "')";
	
	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameWrite, efilingPasswordWrite);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int insertedRows = statement.executeUpdate(insertQuery);

	    if (insertedRows > 0) {
		resultMessage = new String("Added document type: " + documentTypeName + " - " + documentTypeAbbr);
	    } else {
		resultMessage = new String("Error adding document type: " + documentTypeName + " - " + documentTypeAbbr);
	    }
	    
	    //close all processing objects
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
	
	final String clientInfoQuery = "SELECT `key`, acctNum, firstName, lastName, repNum FROM clientInfo ORDER BY lastName";

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
		    clientInfo.add(new Client(results.getString(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5)));
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
	
	String tableName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    tableName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    tableName = "clientDocType";
	} else {
	    tableName = "";
	}
	
	final String documentTypeInfoQuery = "SELECT catID, catName, catAbbr FROM " + tableName + " ORDER BY catName";

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
     * METHOD: UPDATE DOCUMENT TYPE (editDocumentType)
     * Updates the catName & catAbbr where the old category abbr. Sets the result message.
     */
    private void updateDocumentType() {
	
	// Table modifiers
	String tableName = new String();
	if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    tableName = "brokerDocType";
	} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
	    tableName = "clientDocType";
	} else {
	    tableName = "";
	}
	
	final String updateQuery = "UPDATE " + tableName + " SET catName='" + documentTypeName + "', catAbbr='" + documentTypeAbbr + "' WHERE catAbbr='" + documentTypeAbbrOld + "'";
	
	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameWrite, efilingPasswordWrite);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    int updatedRows = statement.executeUpdate(updateQuery);

	    if (updatedRows > 0) {
		resultMessage = new String("Updated document type: \"" + documentTypeAbbrOld + "\" to: " + documentTypeName + " - " + documentTypeAbbr);
	    } else {
		resultMessage = new String("Error updating document type: " + documentTypeName + " - " + documentTypeAbbr);
	    }
	    
	    //close all processing objects
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