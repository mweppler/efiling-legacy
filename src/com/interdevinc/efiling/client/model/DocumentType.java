package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentType implements IsSerializable {

    private String documentTypeID;
    private String documentTypeName;
    private String documentTypeAbbr;
    
    public DocumentType() {
    }
    
    public DocumentType(String dtid, String dtn, String dta) {
	
    }
    
    public void setDocumentTypeID(String dtid) {
	documentTypeID = dtid;
    }
    
    public void setDocumentTypeName(String dtn) {
	documentTypeName = dtn;
    }
    
    public void setDocumentTypeAbbr(String dta) {
	documentTypeAbbr = dta;
    }
    
    public String getDocumentTypeID() {
	return documentTypeID;
    }
    
    public String getDocumentTypeName() {
	return documentTypeName;
    }
    
    public String getDocumentTypeAbbr() {
	return documentTypeAbbr;
    }
    
}
