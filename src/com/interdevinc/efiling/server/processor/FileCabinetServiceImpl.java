package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
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

	private Connection connection;
	private Statement statement;
	private ResultSet results;

	private AuthenticatedUser authenticatedUser;
	private ArrayList<FileCabinet> usersFileCabinets;
	private FileCabinet loadedFileCabinet;
	private SearchComponents searchComponents;
	private ScannedDocument scannedDocument;
	private ArrayList<ScannedDocument> scannedDocuments;

	private String documentTypeID;
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
	public String addDocumentType(AuthenticatedUser au, FileCabinet fc, String dtn, String dta) {

		authenticatedUser = au;
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
	public String deleteDocumentType(AuthenticatedUser au, FileCabinet fc, String dta) {

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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
			statement = connection.createStatement();

			//execute statement and retrieve resultSet
			int insertedRows = statement.executeUpdate(deleteQuery);


			//close all processing objects
			statement.close();		
			connection.close();

			if (insertedRows > 0) {
				retrieveSelectedDocumentType(dta);
				logDocumentTypeAttempt("deleteDocumentType", true);
				resultMessage = new String("Deleted document type: " + documentTypeName + " - " + documentTypeAbbr);
			} else {
				logDocumentTypeAttempt("deleteDocumentType", false);
				resultMessage = new String("Error deleting document type: " + documentTypeName + " - " + documentTypeAbbr);
			}

		}catch (SQLException e){
			e.printStackTrace();
		}

		return resultMessage;
	}

	/**
	 * METHOD: DISASSOCIATE DOCUMENT
	 * @return resultMessage
	 * Disassociates a Document from a Broker/Client.
	 */
	public String disassociateDocument(AuthenticatedUser au, FileCabinet fc, ScannedDocument sd) {
		scannedDocument = sd;
		loadedFileCabinet = fc;

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
		
		final String disassociateQuery = "UPDATE " + tableName + " SET " + groupedBy + "='DISASSOCIATED' WHERE uploadId=" + sd.getUploadID();
		
		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
			statement = connection.createStatement();

			//execute statement and retrieve resultSet
			int disassociatedRows = statement.executeUpdate(disassociateQuery);


			//close all processing objects
			statement.close();		
			connection.close();

			if (disassociatedRows > 0) {
				logDocumentTypeAttempt("disassociateDocument", true);
				resultMessage = new String("Disassociated Document: " + scannedDocument.getFileName() + " - " + scannedDocument.getUploadID());
			} else {
				logDocumentTypeAttempt("disassociateDocument", false);
				resultMessage = new String("Error Disassociating Document: " + documentTypeName + " - " + documentTypeAbbr);
			}

		}catch (SQLException sqle){
			sqle.printStackTrace();
		}

		return resultMessage;
	}
	
	/**
	 * METHOD: EDIT DOCUMENT TYPE
	 * @return resultMessage
	 * Updates a catName & catAbbr by catAbbr.
	 */
	public String editDocumentType(AuthenticatedUser au, FileCabinet fc, String dtn, String dta, String dtao) {

		authenticatedUser = au;
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
		String whereClause = where.substring(0, (where.length() - 4)).toString().concat("AND " + groupedBy + "!='DISASSOCIATED' ");
		String orderClause = "ORDER BY fileName ASC, uploadDate DESC";
		
		final String searchQuery = "SELECT uploadId, fileName, ROUND(fileSize/1024), fileType, " + groupedBy + ", docuType, DATE_FORMAT(uploadDate, '%M %e %Y') FROM " + tableName + whereClause + orderClause;

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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

		}catch (SQLException e){
			e.printStackTrace();
		}

		return documentTypeExists;
	}

	/**
	 * METHOD: CREATE CONNECTION ACCESS RIGHTS (NOT YET IMPLEMENTED)
	 */
	@SuppressWarnings("unused")
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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
			statement = connection.createStatement();

			//execute statement and retrieve resultSet
			int insertedRows = statement.executeUpdate(insertQuery);

			//close all processing objects
			statement.close();		
			connection.close();

			if (insertedRows > 0) {
				retrieveSelectedDocumentType(selectLastDocumentTypeID());
				logDocumentTypeAttempt("addDocumentType", true);
				resultMessage = new String("Added document type: " + documentTypeName + " - " + documentTypeAbbr);
			} else {
				logDocumentTypeAttempt("addDocumentType", false);
				resultMessage = new String("Error adding document type: " + documentTypeName + " - " + documentTypeAbbr);
			}

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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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

		final String scannedDocumentQuery = "SELECT uploadId, fileName, fileSize, fileType, " + groupedBy + ", docuType, uploadDate FROM " + tableName;

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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

		}catch (SQLException e){
			e.printStackTrace();
		}

	}

	/**
	 * METHOD: LOAD CLIENT INFORMATION (retrieveSearchComponents)
	 * Creates an ArrayList of Client objects, sets searchComponent Client List.
	 */
	private void loadClientInformation() {

		final String clientInfoQuery = "SELECT `key`, acctNum, lastName, firstName, repNum FROM clientInfo ORDER BY lastName";

		ArrayList<Client> clientInfo = new ArrayList<Client>();

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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

		}catch (SQLException e){
			e.printStackTrace();
		}

	}

	/**
	 * METHOD: LOG DOCUMENT TYPE ATTEMPT
	 */
	private void logDocumentTypeAttempt(String editType, boolean wasWritten) {

		String action;
		if (editType.equals("addDocumentType")) {
			action = "addDocumentType_|" + documentTypeID + "|" + documentTypeName + "|" + documentTypeAbbr;
		} else if (editType.equals("deleteDocumentType")) {
			action = "deleteDocumentType_|" + documentTypeID + "|" + documentTypeName + "|" + documentTypeAbbr;
		} else if (editType.equals("editDocumentType")) {
			action = "editDocumentType_|" + documentTypeAbbr;
		} else if (editType.equals("disassociateDocument")) { 
			action = "disassociatedDocument_|" + scannedDocument.getUploadID() + "|" + scannedDocument.getGroupedBy() + "|" + scannedDocument.getFileName();
		} else {
			action = "documentType_unknown_action";
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
	 * METHOD: RETRIEVE ALL FILE CABINETS (retrieveUsableFileCabinets)
	 * Checks the users access against the resourceID.
	 * Sets an arraylist of FileCabinet with the users file cabinets.
	 */
	private void retrieveAuthenticatedUsersFileCabinets() {

		usersFileCabinets = new ArrayList<FileCabinet>();

		final String availableFileCabinetQuery = "SELECT cabinetID, cabinetName, resourceID FROM FilingCabinet";

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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

		}catch (SQLException e){
			e.printStackTrace();
		}

	}

	/**
	 * METHOD: RETRIEVE SELECTED CLIENT
	 * Retrieves clientInfo record based on clientID/key sets clientInfo var.
	 */
	private void retrieveSelectedDocumentType(String ca) {

		String tableName = new String();
		if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
			tableName = "brokerDocType";
		} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
			tableName = "clientDocType";
		} else {
			tableName = "";
		}

		final String selectQuery = "SELECT catName, catAbbr FROM "+tableName+" WHERE catAbbr='"+ca+"'";

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
			statement = connection.createStatement();

			//execute statement and retrieve resultSet
			statement.execute(selectQuery);
			results = statement.getResultSet();

			if (results.next()) {
				documentTypeID = ca;
				documentTypeName = results.getString(1);
				documentTypeAbbr = results.getString(2);
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
	 * METHOD: SELECT LAST DOCUMENT TYPE ID
	 * @return did
	 * Retrieves the last catID and catAbbr in the fooDocType database.
	 */
	private String selectLastDocumentTypeID() {

		String tableName = new String();
		if (loadedFileCabinet.getCabinetName().equals("Broker Paperwork")) {
			tableName = "brokerDocType";
		} else if (loadedFileCabinet.getCabinetName().equals("Client Paperwork")) {
			tableName = "clientDocType";
		} else {
			tableName = "";
		}

		String catAbbr = new String();
		String catID = new String();

		final String selectQuery = "SELECT MAX(`catID`) FROM `"+tableName+"`";

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
			statement = connection.createStatement();

			//execute statement and retrieve resultSet
			statement.execute(selectQuery);
			results = statement.getResultSet();

			if (results.next()) {

				catID = results.getString(1);

				String selectCatAbbrQuery = "SELECT catAbbr FROM `"+tableName+"` WHERE catID='"+catID+"'";
				statement.execute(selectCatAbbrQuery);
				results = statement.getResultSet();

				if (results.next()) {
					catAbbr = results.getString(1);
				} else {
					catAbbr = "null";
				}
			} else {
				catID = "0";
			}

			//close all processing objects
			results.close();
			statement.close();		
			connection.close();

		}catch (SQLException e){
			e.printStackTrace();
		}

		return catAbbr;

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
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
			statement = connection.createStatement();

			//execute statement and retrieve resultSet
			int updatedRows = statement.executeUpdate(updateQuery);


			//close all processing objects
			statement.close();		
			connection.close();

			if (updatedRows > 0) {
				logDocumentTypeAttempt("editDocumentType", true);
				resultMessage = new String("Updated document type: \"" + documentTypeAbbrOld + "\" to: " + documentTypeName + " - " + documentTypeAbbr);
			} else {
				logDocumentTypeAttempt("editDocumentType", false);
				resultMessage = new String("Error updating document type: " + documentTypeName + " - " + documentTypeAbbr);
			}

		}catch (SQLException e){
			e.printStackTrace();
		}
	}

}