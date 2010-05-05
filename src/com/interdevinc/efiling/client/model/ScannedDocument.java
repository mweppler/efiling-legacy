package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ScannedDocument implements IsSerializable {

    private String clientAccountNumber;
    private String documentTypeAbbr;
    private String fileName;
    private String fileSize;
    private String uploadDate;
    private String uploadID;
    
    public ScannedDocument() {
    }
    
    public ScannedDocument(String can, String dta, String fname, String fsize, String udate, String uid) {
	setClientAccountNumber(can);
	setDocumentTypeAbbr(dta);
	setFileName(fname);
	setFileSize(fsize);
	setUploadDate(udate);
	setUploadID(uid);
    }

    /**
     * @return the clientAccountNumber
     */
    public String getClientAccountNumber() {
        return clientAccountNumber;
    }

    /**
     * @return the documentTypeAbbr
     */
    public String getDocumentTypeAbbr() {
        return documentTypeAbbr;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the fileSize
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * @return the uploadDate
     */
    public String getUploadDate() {
        return uploadDate;
    }

    /**
     * @return the uploadID
     */
    public String getUploadID() {
        return uploadID;
    }

    /**
     * @param can the clientAccountNumber to set
     */
    public void setClientAccountNumber(String can) {
        clientAccountNumber = can;
    }

    /**
     * @param dta the documentTypeAbbr to set
     */
    public void setDocumentTypeAbbr(String dta) {
        documentTypeAbbr = dta;
    }

    /**
     * @param fname the fileName to set
     */
    public void setFileName(String fname) {
        fileName = fname;
    }

    /**
     * @param fsize the fileSize to set
     */
    public void setFileSize(String fsize) {
        fileSize = fsize;
    }

    /**
     * @param udate the uploadDate to set
     */
    public void setUploadDate(String udate) {
        uploadDate = udate;
    }

    /**
     * @param uid the uploadID to set
     */
    public void setUploadID(String uid) {
        uploadID = uid;
    }
    
}