package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FileCabinet implements IsSerializable {

    private String cabinetID;
    private String cabinetName;
    
    /**
     * CONSTRUCTOR: FILE CABINET (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public FileCabinet() {
    }
    
    /**
     * CONSTRUCTOR: FILE CABINET
     */
    public FileCabinet(String cid, String cname) {
	setCabinetID(cid);
	setCabinetName(cname);
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
    
}
