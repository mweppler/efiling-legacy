package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AuthenticatedUser implements IsSerializable {

    private String emailAddress;
    private String password;
    private String userID;
    private String username;
    
    /**
     * CONSTRUCTOR: AUTHENTICATED USER
     */
    public AuthenticatedUser() {
    }
    
    /**
     * CONSTRUCTOR: AUTHENTICATED USER
     */
    public AuthenticatedUser(String uid, String uname, String pass, String email) {
	setUserID(uid);
	setUsername(uname);
	setPassword(pass);
	setEmailAddress(email);
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
