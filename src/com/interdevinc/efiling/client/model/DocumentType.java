package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentType implements IsSerializable {

    private String documentTypeID;
    private String documentTypeName;
    private String documentTypeAbbr;
    
    /**
     * CONSTRUCTOR: DOCUMENT TYPE
     */
    public DocumentType() {
    }
    
    /**
     * CONSTRUCTOR: DOCUMENT TYPE
     * @param dtid Document Type ID, dtn Document Type Name, dta Document Type Abbr
     */
    public DocumentType(String dtid, String dtn, String dta) {
	setDocumentTypeID(dtid);
	setDocumentTypeName(dtn);
	setDocumentTypeAbbr(dta);
    }
    
    /**
     * @return documentTypeID
     */
    public String getDocumentTypeID() {
	return documentTypeID;
    }
    
    /**
     * @return documentTypeName
     */
    public String getDocumentTypeName() {
	return documentTypeName;
    }
    
    /**
     * @return documentTypeAbbr
     */
    public String getDocumentTypeAbbr() {
	return documentTypeAbbr;
    }
    
    /**
     * @param dtid the documentTypeID to set
     */
    public void setDocumentTypeID(String dtid) {
	documentTypeID = dtid;
    }
    
    /**
     * @param dtn the documentTypeName to set
     */
    public void setDocumentTypeName(String dtn) {
	documentTypeName = dtn;
    }
    
    /**
     * @param dta the documentTypeAbbr to set
     */
    public void setDocumentTypeAbbr(String dta) {
	documentTypeAbbr = dta;
    }
    
}
