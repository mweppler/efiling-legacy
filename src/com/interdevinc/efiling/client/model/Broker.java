package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Broker implements IsSerializable {

    private String repNumber;
    private String firstName;
    private String lastName;
    
    /**
     * CONSTRUCTOR: BROKER
     */
    public Broker() {
    }
    
    /**
     * CONSTRUCTOR: BROKER
     */
    public Broker(String rn, String fn, String ln) {
	setFirstName(fn);
	setLastName(ln);
	setRepNumber(rn);
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
