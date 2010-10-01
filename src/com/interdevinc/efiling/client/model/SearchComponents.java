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
	 * METHOD: RETURN BROKER BY REP NUMBER
	 * @param rn
	 * @return broker
	 */
	public Broker returnBrokerByRepNumber(String rn) {
		Broker broker = null;
		for (Broker br : brokerList) {
			if (br.getRepNumber().equals(rn)) {
				broker = br;
			}
		}
		return broker;
	}
	
	/**
	 * METHOD: RETURN CLIENT BY ACCOUNT NUMBER
	 * @param an
	 * @return client
	 */
	public Client returnClientByAccountNumber(String an) {
		Client client = null;
		for (Client cl : clientList) {
			if (cl.getAccountNumber().equals(an)) {
				client = cl;
			}
		}
		return client;
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
