package com.interdevinc.efiling.server.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.processor.AuthenticateUser;


public class AuthenticateUserImpl extends RemoteServiceServlet implements AuthenticateUser{

    private static final long serialVersionUID = 1L;

    private final String url="jdbc:mysql://192.168.11.6/eamAppAuth";
    private final String username="eamAppAuth";
    private final String password="XVSxQaTeFhNpHLhn";

    /**
     * CONSTRUCTOR: AUTHENTICATE USER IMPL (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public AuthenticateUserImpl() {
	
    }
    
    /**
     * METHOD: AUTHENTICATE USER
     */
    public AuthenticatedUser authenticateUser(String u, String p) {

	/*
	//user instance
	AuthenticatedUser authenticatedUser=null;

	//query statement
	final String userQuery = "SELECT userid, username, emailAddress FROM users WHERE (username='"+u+"' AND password='"+p+"')";

	//visual debug
	//System.out.println(userQuery);

	try{
	    //dynamic class load
	    Class.forName("com.mysql.jdbc.Driver").newInstance();

	    //init connection and statement
	    Connection connection=DriverManager.getConnection(url,username,password);
	    Statement statement=connection.createStatement();

	    //execute statement and retrieve resultSet
	    statement.execute(userQuery);
	    ResultSet results=statement.getResultSet();

	    if(results!=null){
		//if a single result exists
		if(results.next()){
		    System.out.println("creating user...");
		    //init authenticated user
		    authenticatedUser = new AuthenticatedUser(results.getString(1), results.getString(2), results.getString(3));
		}else{
		    return null;
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

	return authenticatedUser;
	*/
	return new AuthenticatedUser("0711","mweppler", "mweppler@interdevinc.com");
    }
    
    private void retrieveAccessControls(String uid) {
	
	final String accessQuery = "SELECT roles.rolename, resources.resourcename FROM access LEFT JOIN roles ON access.roleid=roles.roleid LEFT JOIN resources ON access.resourceid=resources.resourceid WHERE userid='"+uid+"'";
	
	
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