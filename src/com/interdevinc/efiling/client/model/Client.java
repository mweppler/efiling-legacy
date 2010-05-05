package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Client implements IsSerializable {

    private String accountNumber;
    private String firstName;
    private String lastName;
    
    /**
     * CONSTRUCTOR: CLIENT
     */
    public Client() {
    }

    /**
     * CONSTRUCTOR: CLIENT
     * @param an Account Number, fn First Name, ln Last Name
     */    
    public Client(String an, String fn, String ln) {
	setAccountNumber(an);
	setFirstName(fn);
	setLastName(ln);
    }
    
    /**
     * @return accountNumber
     */
    public String getAccountNumber() {
	return accountNumber;
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
     * @param an the accountNumber to set
     */
    public void setAccountNumber(String an) {
	accountNumber = an;
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
    
}
