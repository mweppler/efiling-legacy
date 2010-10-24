package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ScannedDocument implements IsSerializable {

	private String groupedBy;
	private String documentTypeAbbr;
	private String fileName;
	private String fileSize;
	private String fileType;
	private String uploadDate;
	private String uploadID;

	/**
	 * CONSTRUCTOR: SCANNED DOCUMENT (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
	 */
	public ScannedDocument() {
	}

	/**
	 * CONSTRUCTOR: SCANNED DOCUMENT
	 */
	public ScannedDocument(String uid, String fname, String fsize, String fType, String gb, String dta, String udate) {
		setGroupedBy(gb);
		setDocumentTypeAbbr(dta);
		setFileName(fname);
		setFileSize(fsize);
		setFileType(fType);
		setUploadDate(udate);
		setUploadID(uid);
	}

	/**
	 * @return the groupedBy
	 */
	public String getGroupedBy() {
		return groupedBy;
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
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
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
	 * @param can the groupedBy to set
	 */
	public void setGroupedBy(String can) {
		groupedBy = can;
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
	 * @param fType the fileType to set
	 */
	public void setFileType(String fType) {
		fileType = fType;
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
