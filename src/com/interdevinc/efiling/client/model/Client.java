package com.interdevinc.efiling.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Client implements IsSerializable {

    private String accountNumber;
    private String firstName;
    private String lastName;
    
    public Client() {
    }
    
    public Client(String an, String fn, String ln) {
	setAccountNumber(an);
	setFirstName(fn);
	setLastName(ln);
    }
    
    public void setAccountNumber(String an) {
	accountNumber = an;
    }
    
    public void setFirstName(String fn) {
	firstName = fn;
    }
    
    public void setLastName(String ln) {
	lastName = ln;
    }
    
    public String getAccountNumber() {
	return accountNumber;
    }
    
    public String getFirstName() {
	return firstName;
    }
    
    public String getLastName() {
	return lastName;
    }
    
}
