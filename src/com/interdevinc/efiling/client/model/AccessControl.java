package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AccessControl implements IsSerializable {

    private String userID;
    private String roleID;
    private String resourceID;
    
    /**
     * CONSTRUCTOR: ACCESS CONTROLS (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public AccessControl() {
    }
    
    /**
     * CONSTRUCTOR: ACCESS CONTROL
     */
    public AccessControl(String uid, String rol, String res) {
	setUserID(uid);
	setRoleID(rol);
	setResourceID(res);
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return the roleID
     */
    public String getRoleID() {
        return roleID;
    }

    /**
     * @return the resourceID
     */
    public String getResourceID() {
        return resourceID;
    }
    
    /**
     * @param uid the userID to set
     */
    public void setUserID(String uid) {
        userID = uid;
    }

    /**
     * @param rol the roleID to set
     */
    public void setRoleID(String rol) {
        roleID = rol;
    }

    /**
     * @param res the resourceID to set
     */
    public void setResourceID(String res) {
        resourceID = res;
    }
    
}
