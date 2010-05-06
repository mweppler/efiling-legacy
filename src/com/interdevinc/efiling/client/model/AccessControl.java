package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AccessControl implements IsSerializable {

    private String resourceID;
    private String resourceName;
    private String roleID;
    private String roleName;
    private String userID;
    
    /**
     * CONSTRUCTOR: ACCESS CONTROLS (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public AccessControl() {
    }
    
    /**
     * CONSTRUCTOR: ACCESS CONTROL
     */
    public AccessControl(String resid, String resname, String rolid, String rolname, String uid) {
	setResourceID(resid);
	setResourceName(resname);
	setRoleID(rolid);
	setRoleName(rolname);
	setUserID(uid);
    }

    /**
     * @return the resourceID
     */
    public String getResourceID() {
        return resourceID;
    }
    
    /**
     * @return the resourceName
     */
    public String getResourceName() {
	return resourceName;
    }
    
    /**
     * @return the roleID
     */
    public String getRoleID() {
        return roleID;
    }
    
    /**
     * @return the roleName
     */
    public String getRoleName() {
	return roleName;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param resid the resourceID to set
     */
    public void setResourceID(String resid) {
        resourceID = resid;
    }
    
    /**
     * @param resname the resourceName to set
     */
    public void setResourceName(String resname) {
	resourceName = resname;
    }
    
    /**
     * @param rolid the roleID to set
     */
    public void setRoleID(String rolid) {
        roleID = rolid;
    }
    
    /**
     * @param rol the roleName to set
     */
    public void setRoleName(String rolname) {
	roleName = rolname;
    }

    /**
     * @param uid the userID to set
     */
    public void setUserID(String uid) {
        userID = uid;
    }
    
}
