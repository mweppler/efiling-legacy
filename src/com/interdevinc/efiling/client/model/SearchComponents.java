package com.interdevinc.efiling.client.model;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchComponents implements IsSerializable {

    private ArrayList<Broker> brokerList;
    private ArrayList<Client> clientList;
    private ArrayList<DocumentType> documentTypeList;
    
    /**
     * CONSTRUCTOR: SEARCH COMPONENTS (ZERO ARGUMENT IMPLEMENTATION - NEEDED BY GWT)
     */
    public SearchComponents() {
    }
    
    /**
     * @return the brokerList
     */
    public ArrayList<Broker> getBrokerList() {
	return brokerList;
    }

    /**
     * @return the clientList
     */
    public ArrayList<Client> getClientList() {
        return clientList;
    }

    /**
     * @return the documentTypeList
     */
    public ArrayList<DocumentType> getDocumentTypeList() {
        return documentTypeList;
    }
    
    /**
     * @param bList the brokerList to set
     */
    public void setBrokerList(ArrayList<Broker> bList) {
	brokerList = bList;
    }

    /**
     * @param cList the clientList to set
     */
    public void setClientList(ArrayList<Client> cList) {
        clientList = cList;
    }

    /**
     * @param dtList the documentTypeList to set
     */
    public void setDocumentTypeList(ArrayList<DocumentType> dtList) {
        documentTypeList = dtList;
    }
    
    
}
