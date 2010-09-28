package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

	@SuppressWarnings("unused")
	private AuthenticatedUser authenticatedUser;
	private String accountNumber;
	private String dateSent;
	private ActivityLetter activityLetter;

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
	 * METHOD: RETRIEVE THRITY DAY NOT RECEIVED STATUS
	 * @return activityLetters
	 */
	public ArrayList<ActivityLetter> retrieveThrityDayNotReceivedStatus(AuthenticatedUser au) {

		ArrayList<ActivityLetter> activityLetters = retrieveClientsWithNullUpdates(au);

		if (activityLetters != null) {
			updateActivityLetterStatus30Day(activityLetters);
		}

		return activityLetters;

	}

	/**
	 * METHOD: RETRIEVE TWELVE MONTH STATUS
	 * @return
	 */
	public ActivityLetter retrieveTwelveMonthStatus(AuthenticatedUser au, String acctNum) {

		ActivityLetter activityLetter = fetchActivityLetterByAccount(acctNum);

		if (activityLetter.getMisc().equals("No Activity Letter on file.")) {
		} else {
			updateActivityLetterStatus12Day(activityLetter);
		}

		return activityLetter;

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
	 * METHOD: CALCULATE DATE ONE YEAR AGO
	 * @return dateOneYearAgo
	 */
	private String calculateDateOneYearAgo() {

		Calendar today = Calendar.getInstance();

		today.add(Calendar.YEAR, -1);

		int intYear = today.get(Calendar.YEAR);
		int intMonth = today.get(Calendar.MONTH) + 1;
		int intDay = today.get(Calendar.DAY_OF_MONTH);

		String strMonth;
		String strDay;

		if (intMonth < 10) {
			strMonth = "0" + intMonth;
		} else {
			strMonth = intMonth + "";
		}

		if (intDay < 10) {
			strDay = "0" + intDay;
		} else {
			strDay = intDay + "";
		}

		String dateOneYearAgo = intYear + strMonth + strDay;

		return dateOneYearAgo;

	}


	/**
	 * METHOD: CALCULATE DATE THRITY DAYS FROM NOW
	 * @return dateThrityDaysFromNow
	 */
	private String calculateDateThrityDaysFromNow(String dateSent) {

		String dateThrityDaysFromNow = new String();

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date dateDue = sdf.parse(dateSent);

			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(dateDue);

			int intYear = dueDate.get(Calendar.YEAR);
			int intMonth = dueDate.get(Calendar.MONTH) + 1;
			int intDay = dueDate.get(Calendar.DAY_OF_MONTH);

			String strMonth;
			String strDay;

			if (intMonth < 10) {
				strMonth = "0" + intMonth;
			} else {
				strMonth = intMonth + "";
			}

			if (intDay < 10) {
				strDay = "0" + intDay;
			} else {
				strDay = intDay + "";
			}

			dateThrityDaysFromNow = intYear + strMonth + strDay;

		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return dateThrityDaysFromNow;

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


	/**
	 * METHOD: FETCH ACTIVITY LETTER BY ACCOUNT 
	 * @param acctNum
	 * @return activityLetter
	 */
	private ActivityLetter fetchActivityLetterByAccount(String acctNum) {

		final String selectQuery = "SELECT letterID, accountNumber, MAX(dateSent), dateReceieved, scannedDocumentID, misc FROM `ActivityLetter` WHERE accountNumber='"+acctNum+"'";

		try{

			//init connection and statement
			connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
			statement = connection.createStatement();

			statement.execute(selectQuery);
			results = statement.getResultSet();

			if (results.next()) {
				activityLetter = new ActivityLetter(results.getString(1), results.getString(2), results.getDate(3), results.getDate(4), results.getString(5), results.getString(6));
			}

			//close all processing objects
			results.close();
			statement.close();		
			connection.close();

		}catch (SQLException e){
			e.printStackTrace();
		}

		try {
			databaseFormat.format(activityLetter.getDateReceived());
		} catch (NullPointerException npe) {
			activityLetter = new ActivityLetter(null, acctNum, null, null, null, "No Activity Letter on file.");
		}

		return activityLetter;

	}

	/**
	 * METHOD: TODAYS DATE AS STRING
	 * @return todayAsString
	 */
	private String todaysDateAsString() {

		Calendar today = Calendar.getInstance();

		int intYear = today.get(Calendar.YEAR);
		int intMonth = today.get(Calendar.MONTH) + 1;
		int intDay = today.get(Calendar.DAY_OF_MONTH);

		String strMonth;
		String strDay;

		if (intMonth < 10) {
			strMonth = "0" + intMonth;
		} else {
			strMonth = intMonth + "";
		}

		if (intDay < 10) {
			strDay = "0" + intDay;
		} else {
			strDay = intDay + "";
		}

		String todayAsString = intYear + strMonth + strDay;

		return todayAsString;

	}


	/**
	 * METHOD: UPDATE ACTIVITY LETTER STATUS 12 DAY
	 */
	private void updateActivityLetterStatus12Day(ActivityLetter al) {

		Date dateReceived = al.getDateReceived();
		String dateRecieved = databaseFormat.format(dateReceived);
		String dateOneYearAgo = calculateDateOneYearAgo();

		if (Integer.parseInt(dateOneYearAgo) > Integer.parseInt(dateRecieved)) {
			al.setMisc("Client has been sent an Activity Letter in the past 12 months: " + dateRecieved);
		} else {
			al.setMisc("Client has not been sent an Activity Letter in the past 12 months: " + dateRecieved);
		}

	}

	/**
	 * METHOD: UPDATE ACTIVITY LETTER STATUS 30 DAY
	 */
	private void updateActivityLetterStatus30Day(ArrayList<ActivityLetter> al) {

		String today = todaysDateAsString();

		for (ActivityLetter activityLetters : al) {

			String thrityDaysFromNow = calculateDateThrityDaysFromNow(activityLetters.getDateSentAsDatabaseString());

			if (Integer.parseInt(today) > Integer.parseInt(thrityDaysFromNow)) {
				activityLetters.setMisc("Activity Letter is Past the 30 day due date of: " + thrityDaysFromNow);
			} else {
				activityLetters.setMisc("Activity Letter is due of: " + thrityDaysFromNow);
			}

		}

	}

}
