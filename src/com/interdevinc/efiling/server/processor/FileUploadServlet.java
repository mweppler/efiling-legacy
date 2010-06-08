package com.interdevinc.efiling.server.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadServlet extends HttpServlet {

    /**
     * The serializable class FileUploadServlet needs a static final serialVersionUID field of type long.
     */
    private static final long serialVersionUID = -8048716200519402624L;

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

    private HttpServletRequest request;
    private HttpServletResponse response;

    private boolean dataWrittenToDatabase;
    private boolean fileWrittenToStorage;
    private File uploadedFile;
    private String associatedWith;
    private String documentType;
    private String fileCabinet;
    private String fileName;
    private String fileExtension;
    private String uploadID;
    private final Date date = new Date();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * METHOD: DO GET
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
    } 

    /**
     * METHOD: DO POST
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	request = req;
	response = resp;

	dataWrittenToDatabase = false;
	fileWrittenToStorage = false;

	processFormElements();

	writeResponseMessage();

    }

    /**
     * METHOD: INSERT DATA INTO DATABASE
     * @return recordInserted
     * Inserts file information into database, sets the uploadID used to rename the file for storage.
     */
    private boolean insertDataIntoDatabase() {

	boolean recordInserted = false;

	String groupBy = new String();
	String table = new String();

	//Production Environment, will not work in Development mode
	if (fileCabinet.equals("broker")) {
	    groupBy = "repNum";
	    table = "brokerFileLocation";
	} else if (fileCabinet.equals("client")) {
	    groupBy = "clientAcctNum";
	    table = "clientFileLocation";
	}

	final String insertFileQuery = "INSERT INTO " + table + " (fileName, fileSize, fileType, " + groupBy + ", docuType, uploadDate) VALUES ('" + associatedWith + "(" + documentType + ")" + fileExtension + "'" + ", '" + uploadedFile.length() + "', 'application/"+ fileExtension.substring(1, fileExtension.length()) +"', '" + associatedWith + "', '" + documentType + "', '" + sdf.format(date) + "')";

	final String lastUploadIDQuery = "SELECT MAX(uploadID) FROM " + table;

//	System.out.println(insertFileQuery + "\n" + lastUploadIDQuery);

	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameWrite, efilingPasswordWrite);
	    statement = connection.createStatement();

	    //execute update
	    statement.executeUpdate(insertFileQuery);

	    statement.execute(lastUploadIDQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		results.next();
		uploadID = results.getString(1);
		recordInserted = true;
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

	return recordInserted;

    }

    /**
     * METHOD: PROCESS FORM ELEMENTS
     * Initial method called by doPost(). 
     * Calls processFormField() & processUploadedFile() to process form fields. 
     * Than calls insertDataIntoDatabase() & writeFileToStorage().
     */
    private void processFormElements() {

	//verify that the request is multipart
	if (ServletFileUpload.isMultipartContent(request)) {

	    FileItemFactory factory = new DiskFileItemFactory();
	    ServletFileUpload upload = new ServletFileUpload(factory);

	    try {

		//get the items from the request object
		List items = upload.parseRequest(request);

		Iterator iter = items.iterator();
		while (iter.hasNext()) {
		    Object iteratorObject = iter.next();

		    if (iteratorObject == null) { //make sure the object is not null
			continue;
		    }

		    FileItem item = (FileItem)iteratorObject;

		    if (item.isFormField()) { // If item is a form field then get the value from the item
			processFormField(item);
		    } else if (!item.isFormField()) { // If the item is not a form field, its an uploaded file, process it.
			processUploadedFile(item);
		    }
		}

//		System.out.println("Associated With: " + associatedWith);
//		System.out.println("Document Type: " + documentType);
//		System.out.println("File Cabinet: " + fileCabinet);
//		System.out.println("FileName: " + fileName);

		if (uploadedFile.exists()) {
		    if (dataWrittenToDatabase = insertDataIntoDatabase()) {
			if (fileWrittenToStorage = writeFileToStorage()) {
			} else {
			    System.out.println("File not written to Storage.");
			}
		    } else {
			System.out.println("Data not written to Database.");
		    }
		} else {
		    System.out.println("File: " + uploadedFile.getAbsolutePath() + "\nDoes not exist.");
		}

	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
    }

    /**
     * METHOD: PROCESS FORM FIELD
     * @param i the form field value
     * Sets associatedWith, documentType, fileCabinet.  
     */
    private void processFormField(FileItem i) {
	if (i.getFieldName().equals("associatedWith")) {
	    associatedWith = i.getString();
	} else if (i.getFieldName().equals("documentType")) {
	    documentType = i.getString();
	} else if (i.getFieldName().equals("fileCabinet")) {
	    fileCabinet = i.getString();
	}
    }

    /**
     * METHOD: PROCESS UPLOADED FILE
     * @param i the file upload
     * Sets the fileName, fileExtension, uploadedFile(temp name & location of uploaded file).
     * Writes uploadedFile to location.
     */
    private void processUploadedFile(FileItem i) {

	// Set the filename and fileExtenstion
	fileName = i.getName();
	int extensionStartsAt = fileName.indexOf(".");
	fileExtension = fileName.substring(extensionStartsAt, fileName.length());
	Random randomGenerator = new Random();
	int randomNumber = randomGenerator.nextInt();

	// Production Environment, will not work in Development mode
	uploadedFile = new File("/opt/lampp/webapps/cabinets/tempDir/efilingTemp" + randomNumber + fileExtension);

	try {
	    i.write(uploadedFile);
	} catch (FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * METHOD: WRITE FILE TO STORAGE
     * @return true if the file was written and false otherwise.
     * Writes uploadedFile to storedFile and deletes uploadedFile.
     */
    private boolean writeFileToStorage() {

	// Production Environment, will not work in Development mode
	File storedFile = new File("/opt/lampp/webapps/cabinets/" + fileCabinet + "/" + uploadID + fileExtension);

	try {
	    InputStream in = new FileInputStream(uploadedFile);
	    OutputStream out = new FileOutputStream(storedFile);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();

	    // Delete temp file.
	    uploadedFile.delete();

	} catch (FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	}  catch (IOException ioe) {
	    ioe.printStackTrace();
	}

	if (storedFile.exists()) {
	    return true;
	} else {
	    return false;
	}

    }

    /**
     * METHOD: WRITE RESPONSE MESSAGE
     * Writes an html formatted message to response.
     */
    private void writeResponseMessage() {

	try {
	    response.setContentType("text/html;charset=UTF-8");
	    PrintWriter responseMessage;
	    responseMessage = response.getWriter();

	    String responseString = new String();

	    if (dataWrittenToDatabase && fileWrittenToStorage) {	    
		responseString = ("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
			"Transitional//EN\">\n" +
			"<HTML>\n" +
			"<HEAD><TITLE>Upload Successful</TITLE></HEAD>\n" +
			"<BODY>\n" +
			"<H1>(" + documentType + ") document for (" + associatedWith + ") uploaded into (" + fileCabinet + ") cabinet with uploadID (" + uploadID + ")</H1>\n" +
		"</BODY></HTML>");
	    } else {
		responseString = ("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
			"Transitional//EN\">\n" +
			"<HTML>\n" +
			"<HEAD><TITLE>Upload Failure</TITLE></HEAD>\n" +
			"<BODY>\n" +
			"<H1>Upload Failure</H1>\n" +
		"</BODY></HTML>");
	    }

	    responseMessage.println(responseString);

	} catch (IOException ioe) {
	    ioe.printStackTrace();
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
