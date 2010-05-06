package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FileCabinet implements IsSerializable {

    private String cabinetID;
    private String cabinetName;
    private String resourceID;
    
    /**
     * CONSTRUCTOR: FILE CABINET (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public FileCabinet() {
    }
    
    /**
     * CONSTRUCTOR: FILE CABINET
     */
    public FileCabinet(String cid, String cname, String rid) {
	setCabinetID(cid);
	setCabinetName(cname);
	setResourceID(rid);
    }

    /**
     * @return the cabinetID
     */
    public String getCabinetID() {
        return cabinetID;
    }

    /**
     * @return the cabinetName
     */
    public String getCabinetName() {
        return cabinetName;
    }

    /**
     * @return resourceID
     */
    public String getResourceID() {
	return resourceID;
    }
    
    /**
     * @param cid the cabinetID to set
     */
    public void setCabinetID(String cid) {
        cabinetID = cid;
    }

    /**
     * @param cname the cabinetName to set
     */
    public void setCabinetName(String cname) {
        cabinetName = cname;
    }
    
    /**
     * @param rid the resourceID to set
     */
    public void setResourceID(String rid) {
	resourceID = rid;
    }
    
}
