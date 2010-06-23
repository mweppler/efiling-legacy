package com.interdevinc.efiling.client.model;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ActivityLetter implements IsSerializable {

    private String letterID;
    private String accountNumber;
    private Date dateSent;
    private Date dateReceived;
    private String scannedDocument;
    private String misc;
    
    /**
     * CONSTRUCTOR: ACTIVITY LETTER (Needed by GWT for Serialization)
     */
    public ActivityLetter() {
    }
    
    /**
     * CONSTRUCTOR: ACTIVITY LETTER
     * @param lid
     * @param an
     * @param ds
     * Most likey used when creating a new Activity Letter record in the database.
     */
    public ActivityLetter(String lid, String an, Date ds) {
	
	// Some fields may not be set initially.
	setLetterID(lid);
	setAccountNumber(an);
	setDateSent(ds);
	
    }
    
    /**
     * CONSTRUCTOR: ACTIVITY LETTER
     * @param lid the letterID
     * @param an the accountNumber
     * @param ds the dateSent
     * @param dr the dateReceived
     * @param sd the scannedDocument
     * @param m the misc
     * Most likey used when retrieving an existing Activity Letter from the database for review.
     */
    public ActivityLetter(String lid, String an, Date ds, Date dr, String sd, String m) {
	setLetterID(lid);
	setAccountNumber(an);
	setDateSent(ds);
	
	// May not be set initially.
	setDateReceived(dr);
	setScannedDocument(sd);
	setMisc(m);

    }
    
    /**
     * @return the letterID
     */
    public String getLetterID() {
        return letterID;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @return the dateSent
     */
    public Date getDateSent() {
        return dateSent;
    }
    
    /**
     * @return database formatted date as a string (example: "19800711")
     */
    public String getDateSentAsDatabaseString() {
	DateTimeFormat datebaseFormat = DateTimeFormat.getFormat("yyyyMMdd");
	return datebaseFormat.format(dateSent);
    }

    /**
     * @return date formatted as a human readable string (example: "July 11 1980") 
     */
    public String getDateSentFormattedString() {
	DateTimeFormat humanReadableFormat = DateTimeFormat.getFormat("MMM dd yyyy");
	return humanReadableFormat.format(dateSent);
    }
    
    /**
     * @return the dateReceived
     */
    public Date getDateReceived() {
        return dateReceived;
    }
    
    /**
     * @return database date formatted as string, (example: "19800711")
     */
    public String getDateReceivedAsDatabaseString() {
	DateTimeFormat humanReadableFormat = DateTimeFormat.getFormat("yyyyMMdd");
	return humanReadableFormat.format(dateReceived);
    }

    /**
     * @return date formatted as string, example: "July 11 1980"
     */
    public String getDateReceivedFormattedString() {
	DateTimeFormat humanReadableFormat = DateTimeFormat.getFormat("MMM dd yyyy");
	return humanReadableFormat.format(dateReceived);
    }
    
    /**
     * @return the scannedDocument
     */
    public String getScannedDocument() {
        return scannedDocument;
    }

    /**
     * @return the misc
     */
    public String getMisc() {
        return misc;
    }

    /**
     * @param lid the letterID to set
     */
    public void setLetterID(String lid) {
        letterID = lid;
    }

    /**
     * @param an the accountNumber to set
     */
    public void setAccountNumber(String an) {
        accountNumber = an;
    }

    /**
     * @param ds the dateSent to set
     */
    public void setDateSent(Date ds) {
        dateSent = ds;
    }

    /**
     * @param dr the dateReceived to set
     */
    public void setDateReceived(Date dr) {
        dateReceived = dr;
    }

    /**
     * @param sd the scannedDocument to set
     */
    public void setScannedDocument(String sd) {
        scannedDocument = sd;
    }

    /**
     * @param m the misc to set
     */
    public void setMisc(String m) {
        misc = m;
    }

}
