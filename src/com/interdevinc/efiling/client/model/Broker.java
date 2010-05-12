package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Broker implements IsSerializable {

    private String brokerFullInfo;
    private String firstName;
    private String lastName;
    private String repNumber;
    
    /**
     * CONSTRUCTOR: BROKER (ZERO ARGUMENT IMPLEMENTATION- NEEDED BY GWT)
     */
    public Broker() {
    }
    
    /**
     * CONSTRUCTOR: BROKER
     */
    public Broker(String fn, String ln, String rn) {
	setFirstName(fn);
	setLastName(ln);
	setRepNumber(rn);
	
	setBrokerFullInfo();
    }
   
    /**
     * @return brokerFullInfo
     */
    public String getBrokerFullInfo() {
	return brokerFullInfo;
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
     * brokerFullInfo to set
     */
    public void setBrokerFullInfo() {
	brokerFullInfo = repNumber + " - " + lastName + ", " + firstName;
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
