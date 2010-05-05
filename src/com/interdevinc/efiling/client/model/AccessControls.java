package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AccessControls implements IsSerializable {

    private String userID;
    private String[] cabinetID;
    
    /**
     * CONSTRUCTOR: ACCESS CONTROLS
     */
    public AccessControls() {
    }
    
    /**
     * CONSTRUCTOR: ACCESS CONTROLS
     */
    public AccessControls(String uid, String[] cid) {
	setUserID(uid);
	setCabinetID(cid);
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return the cabinetID
     */
    public String[] getCabinetID() {
        return cabinetID;
    }

    /**
     * @param uid the userID to set
     */
    public void setUserID(String uid) {
        userID = uid;
    }

    /**
     * @param cid the cabinetID to set
     */
    public void setCabinetID(String[] cid) {
        cabinetID = cid;
    }
    
    
    
}
