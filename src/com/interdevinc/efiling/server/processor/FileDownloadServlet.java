package com.interdevinc.efiling.server.processor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interdevinc.efiling.client.model.ScannedDocument;

public class FileDownloadServlet extends HttpServlet {

    /**
     * The serializable class FileDownloadServlet needs a static final serialVersionUID field of type long.
     */
    private static final long serialVersionUID = -5892158287193293449L;

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
    private ScannedDocument scannedDocument;
    
    /**
     * METHOD: DO GET
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
	request = req;
	response = resp;

	retrieveRequestedFileInformation();

	sendRequestedFile();

    } 

    /**
     * METHOD: DO POST
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    /**
     * METHOD: RETRIEVE REQUESTED FILE INFORMATION
     * Queries database for file information, which is used by sendRequestedFile();
     */
    private void retrieveRequestedFileInformation() {

	String groupBy = new String();
	String table = new String();
	
	// TODO Production Environment
	if (request.getParameter("cabinet").equals("broker")) {
	    groupBy = "repNum";
	    table = "brokerFileLocation";
	} else if (request.getParameter("cabinet").equals("client")) {
	    groupBy = "clientAcctNum";
	    table = "clientFileLocation";
	}
	
	// TODO Development Environment
	//table = "testLocation";
	
	final String requestedFileInfoQuery = "SELECT uploadId, fileName, fileSize, fileType, " + groupBy + ", docuType, uploadDate FROM " + table + " WHERE uploadId='" + request.getParameter("uploadID") + "'";

	try{

	    //init connection and statement
	    connection = getConnection(efilingDatabase, efilingUsernameRead, efilingPasswordRead);
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(requestedFileInfoQuery);
	    results = statement.getResultSet();

	    if (results != null) {
		while (results.next()) {
		    scannedDocument = new ScannedDocument(results.getString(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7));
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

    /**
     * METHOD: SEND REQUESTED FILE
     * Creates a file object to be sent to the user.
     */
    private void sendRequestedFile() {

	// Set the filename and fileExtenstion
	String fileName = new String(scannedDocument.getFileName());
	int extensionStartsAt = fileName.indexOf(".");
	String fileExtension = fileName.substring(extensionStartsAt, fileName.length());
	
	// TODO Production Environment
	File file = new File("/opt/lampp/webapps/cabinets/" + request.getParameter("cabinet") + "/" + scannedDocument.getUploadID() + fileExtension);

	// TODO Development Environment
	//File file = new File("/tmp/" + request.getParameter("cabinet") + "/" + scannedDocument.getUploadID() + fileExtension);
	
	int length = 0; 
	ServletOutputStream outputStream;
	try {
	    
//	    System.out.println("Absolute path is: " + file.getAbsolutePath());
//	    System.out.println("Canonical path is: " + file.getCanonicalPath());
	    
	    outputStream = response.getOutputStream();
	    ServletContext context = getServletConfig().getServletContext(); 
	    response.setContentType(scannedDocument.getFileType()); 
	    response.setContentLength((int) file.length()); 
	    response.setHeader("Content-Disposition", "attachment; filename=" + fileName); 
	    byte[] byteBuffer = new byte[1024]; 
	    DataInputStream inputStream;
	    inputStream = new DataInputStream(new FileInputStream(file));
	    while ((inputStream != null) && ((length = inputStream.read(byteBuffer)) != -1)) { 
		outputStream.write(byteBuffer, 0, length); 
	    } 
	    inputStream.close(); 
	    outputStream.flush(); 
	    outputStream.close(); 

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
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
