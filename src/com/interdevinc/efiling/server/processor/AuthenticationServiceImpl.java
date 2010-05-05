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
import com.interdevinc.efiling.client.processor.AuthenticationService;


public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService{

    private static final long serialVersionUID = 1L;

    private final String url="jdbc:mysql://192.168.11.6/eamAppAuth";
    private final String username="eamAppAuth";
    private final String password="XVSxQaTeFhNpHLhn";

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
	    retrieveAccessControl();
	}

	return authenticatedUser;

    }

    /**
     * METHOD: RETRIEVE USER DETAILS
     * @param u username, p password
     * Validates username/password, creates an AuthenticatedUser instance
     */
    private void retrieveUserDetails(String u, String p) {

	//query statement
	final String userQuery = "SELECT userid, username, emailAddress FROM users WHERE (username='"+u+"' AND password='"+p+"')";

	try{

	    //init connection and statement
	    connection = getConnection();
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
     * METHOD: RETRIEVE ACCESS CONTROL
     * Creates AccessControl instances, added them to the authenticatedUser instance
     */
    private void retrieveAccessControl() {

	ArrayList<AccessControl> accessControl = new ArrayList<AccessControl>();
	
	//query statement
	final String accessQuery = "SELECT roles.rolename, resources.resourcename FROM access LEFT JOIN roles ON access.roleid=roles.roleid LEFT JOIN resources ON access.resourceid=resources.resourceid WHERE userid='"+authenticatedUser.getUserID()+"' ORDER BY resources.resourceid ASC";

	try{

	    //init connection and statement
	    connection = getConnection();
	    statement = connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(accessQuery);
	    results = statement.getResultSet();

	    if(results!=null){
		while (results.next()) {
		    accessControl.add(new AccessControl(authenticatedUser.getUserID(), results.getString(1), results.getString(2)));
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
	
	authenticatedUser.setAccessControl(accessControl);

    }

    /**
     * METHOD: GET CONNECTION
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException */
    private Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	return DriverManager.getConnection(url,username,password);
    }

}