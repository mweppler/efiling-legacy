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
     * METHOD: LOG FILE DOWNLOAD
     */
    private void logFileDownload(boolean wasDownloaded) {
	
	int status;
	
	if (wasDownloaded) {
	    status = 1;
	} else {
	    status = 0;
	}
	
	final String logQuery = "INSERT INTO UsageLog (`user`, `resource`, `action`, `status`) VALUES ('"+request.getParameter("user")+"', 'efiling', 'download_"+request.getParameter("cabinet")+"_"+scannedDocument.getUploadID()+"', '"+status+"')";

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
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "READ");
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

	    logFileDownload(true);
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    logFileDownload(false);
	} catch (IOException e) {
	    e.printStackTrace();
	    logFileDownload(false);
	} 
    }
    
}
