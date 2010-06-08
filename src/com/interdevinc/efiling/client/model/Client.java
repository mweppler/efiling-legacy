package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Client implements IsSerializable {

    private String accountNumber;
    private String clientFullInfo;
    private String clientFullInfoWRepNumber;
    private String clientID;
    private String firstName;
    private String lastName;
    private String repNumber;
    
    /**
     * CONSTRUCTOR: CLIENT (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public Client() {
    }

    /**
     * CONSTRUCTOR: CLIENT
     * @param an Account Number, fn First Name, ln Last Name
     */    
    public Client(String cid, String an, String fn, String ln, String rn) {
	setAccountNumber(an);
	setClientID(cid);
	setFirstName(fn);
	setLastName(ln);
	setRepNumber(rn);
	
	setClientFullInfo();
	setClientFullInfoWRepNumber();
    }
    
    /**
     * @return accountNumber
     */
    public String getAccountNumber() {
	return accountNumber;
    }
    
    /**
     * @return clientFullInfo
     */
    public String getClientFullInfo() {
	return clientFullInfo;
    }
    
    /**
     * @return clientFullInfoWRepNumber
     */
    public String getClientFullInfoWRepNumber() {
	return clientFullInfoWRepNumber;
    }
    
    /**
     * @return clientID
     */
    public String getClientID() {
	return clientID;
    }
    
    /**
     * @return firstName
     */
    public String getFirstName() {
	return firstName;
    }
    
    /**
     * @return lastName
     */
    public String getLastName() {
	return lastName;
    }

    /**
     * @return repNumber
     */
    public String getRepNumber() {
	return repNumber;
    }
    
    /**
     * @param an the accountNumber to set
     */
    public void setAccountNumber(String an) {
	accountNumber = an;
    }
    
    /**
     * clientFullInfo to set
     */
    public void setClientFullInfo() {
	clientFullInfo = lastName + ", " + firstName + " - " + accountNumber;
    }
    
    /**
     * clientFullInfoWRepNumber to set
     */
    public void setClientFullInfoWRepNumber() {
	clientFullInfoWRepNumber = lastName + ", " + firstName + " - " + accountNumber + " | " + repNumber;
    }
    
    /**
     * @param cid the clientID to set
     */
    public void setClientID(String cid) {
	clientID = cid;
    }
    
    /**
     * @param fn the firstName to set
     */
    public void setFirstName(String fn) {
	firstName = fn;
    }
    
    /**
     * @param ln the lastName to set
     */
    public void setLastName(String ln) {
	lastName = ln;
    }

    /**
     * @param rn the repNumber to set
     */
    public void setRepNumber(String rn) {
	repNumber = rn;
    }
    
}
