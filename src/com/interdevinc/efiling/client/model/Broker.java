package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Broker implements IsSerializable {

    private String repNumber;
    private String firstName;
    private String lastName;
    
    public Broker() {
    }
    
    public Broker(String rn, String fn, String ln) {
	setFirstName(fn);
	setLastName(ln);
	setRepNumber(rn);
    }
    
    public void setFirstName(String fn) {
	firstName = fn;
    }
    
    public void setLastName(String ln) {
	lastName = ln;
    }
    
    public void setRepNumber(String rn) {
	repNumber = rn;
    }
    
    public String getFirstName() {
	return firstName;
    }
    
    public String getLastName() {
	return lastName;
    }
    
    public String getRepNumber() {
	return repNumber;
    }
    
}
