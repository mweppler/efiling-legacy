package com.interdevinc.efiling.client.model;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AuthenticatedUser implements IsSerializable {

    private String emailAddress;
    private String password;
    private String userID;
    private String username;
    private ArrayList<AccessControl> accessControl;
    
    /**
     * CONSTRUCTOR: AUTHENTICATED USER (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public AuthenticatedUser() {
    }
    
    /**
     * CONSTRUCTOR: AUTHENTICATED USER
     */
    public AuthenticatedUser(String uid, String uname, String email) {
	setUserID(uid);
	setUsername(uname);
	setEmailAddress(email);
    }

    /**
     * @return accessControl
     */
    public ArrayList<AccessControl> getAccessControl() {
	return accessControl;
    }
    
    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param ac the accessControl to set
     */
    public void setAccessControl(ArrayList<AccessControl> ac) {
	accessControl = ac;
    }
    
    /**
     * @param uid the userID to set
     */
    public void setUserID(String uid) {
        userID = uid;
    }

    /**
     * @param uname the username to set
     */
    public void setUsername(String uname) {
        username = uname;
    }

    /**
     * @param pass the password to set
     */
    public void setPassword(String pass) {
        password = pass;
    }

    /**
     * @param email the emailAddress to set
     */
    public void setEmailAddress(String email) {
        emailAddress = email;
    }
    
}
