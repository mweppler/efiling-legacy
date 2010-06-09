package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.interdevinc.efiling.client.model.AccessControl;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.processor.AuthenticationService;

public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService{

    /**
     * The serializable class AuthenticationServiceImpl needs a static final serialVersionUID field of type long.
     */
    private static final long serialVersionUID = -8814344346409497283L;

    private Connection connection;
    private Statement statement;
    private ResultSet results;

    private AuthenticatedUser authenticatedUser;

    /**
     * CONSTRUCTOR: AUTHENTICATION SERVICE IMPL (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public AuthenticationServiceImpl() {
    }

    /**
     * METHOD: AUTHENTICATE USER
     */
    public AuthenticatedUser authenticateUser(String u, String p) {

	retrieveUserDetails(u, p);
	
	if (authenticatedUser != null) {
	    logLoginAttempt(u, true);
	    retrieveAccessControl();
	} else {
	    logLoginAttempt(u, false);
	}

	return authenticatedUser;

    }

    /**
     * METHOD: LOG LOGIN ATTEMPT
     * @param u
     * @param isLoggedIn
     * Logs the event into the UsageLog table in the efilingsys database.
     */
    private void logLoginAttempt(String u, boolean isLoggedIn) {
	
	int status;
	if (isLoggedIn) {
	    status = 1;
	} else {
	    status = 0;
	}
	
	//query statement
	final String insertQuery = "INSERT INTO UsageLog (`user`, `resource`, `action`, `status`) VALUES ('"+u+"', 'efiling', 'login', '"+status+"')";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("efilingsys", "WRITE");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.executeUpdate(insertQuery);

	    //close all processing objects
	    statement.close();
	    connection.close();			

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
    }
    
    /**
     * METHOD: RETRIEVE USER DETAILS
     * @param u username
     * @param p password
     * Validates username/password, creates an AuthenticatedUser instance
     */
    private void retrieveUserDetails(String u, String p) {

	//query statement
	final String userQuery = "SELECT userid, username, emailAddress FROM users WHERE (username='"+u+"' AND password='"+p+"')";
	
	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("eamAppAuth", "READ");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(userQuery);
	    results = statement.getResultSet();

	    if(results!=null){
		//if a single result exists
		if(results.next()){
		    //init authenticated user
		    authenticatedUser = new AuthenticatedUser(results.getString(1), results.getString(2), results.getString(3));
		}else{
		    authenticatedUser = null;
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
     * METHOD: RETRIEVE ACCESS CONTROL
     * Creates AccessControl instances, added them to the authenticatedUser instance
     */
    private void retrieveAccessControl() {

	ArrayList<AccessControl> accessControl = new ArrayList<AccessControl>();
	
	//query statement
	final String accessQuery = "SELECT resources.resourceid, resources.resourcename, roles.roleid, roles.rolename FROM access LEFT JOIN roles ON access.roleid=roles.roleid LEFT JOIN resources ON access.resourceid=resources.resourceid WHERE userid='"+authenticatedUser.getUserID()+"' ORDER BY resources.resourceid, roles.roleid ASC";

	try{

	    //init connection and statement
	    connection = DatabaseConnectionService.retrieveDatabaseConnection("eamAppAuth", "READ");
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(accessQuery);
	    results = statement.getResultSet();

	    if(results!=null){
		while (results.next()) {
		    accessControl.add(new AccessControl(results.getString(1), results.getString(2), results.getString(3), results.getString(4), authenticatedUser.getUserID()));
		}
	    }

	    //close all processing objects
	    results.close();
	    statement.close();
	    connection.close();			

	}catch (SQLException e){
	    e.printStackTrace();
	}
	
	authenticatedUser.setAccessControl(accessControl);

    }

}