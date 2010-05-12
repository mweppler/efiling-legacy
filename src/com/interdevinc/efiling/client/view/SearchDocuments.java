package com.interdevinc.efiling.client.view;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.Broker;
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.DocumentType;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.FileCabinetServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class SearchDocuments {

    // Panels
    private VerticalPanel searchDocumentsPanel;
    private FlexTable searchComponentsTable;
    private InformationDialogBox idb;

    private TextBox quickSearchTextbox;
    private ListBox cabinetTypeInfoList;
    private ListBox documentTypeList;

    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;

    private FileCabinetServiceAsync fileCabinetAsync;    

    public SearchDocuments(AuthenticatedUser au, FileCabinet fc, VerticalPanel sdp, SearchComponents sc) {
	authenticatedUser = au;
	fileCabinet = fc;
	searchDocumentsPanel = sdp;
	searchComponents = sc;

	initializeComponents();
	assembleComponents();

    }

    private void assembleComponents() {

	int row = 0;
	searchComponentsTable.setWidget(row, 0, new Label("Quick Search:"));
	searchComponentsTable.setWidget(row, 1, quickSearchTextbox);
	++row;

	searchComponentsTable.setWidget(row, 0, cabinetTypeInfoList);
	searchComponentsTable.setWidget(row, 1, documentTypeList);
	
	// Test cabinet type.
	if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    // Build Listboxes.
	    cabinetTypeInfoList.addItem("Search by Broker Info");
	    for (Broker brokerInfo : searchComponents.getBrokerList()) {
		cabinetTypeInfoList.addItem(brokerInfo.getBrokerFullInfo());
	    }
	} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
	    // Build Listboxes.
	    cabinetTypeInfoList.addItem("Search by Client Info");
	    for (Client clientInfo : searchComponents.getClientList()) {
		cabinetTypeInfoList.addItem(clientInfo.getClientFullInfo());
	    }
	} else {
	    cabinetTypeInfoList.addItem("Cabinet not loaded!");
	}

	documentTypeList.addItem("Search by Document Type");
	for (DocumentType documentTypeInfo : searchComponents.getDocumentTypeList()) {
	    documentTypeList.addItem(documentTypeInfo.getDocumentTypeFullInfo());
	}
	++row;

	searchDocumentsPanel.add(searchComponentsTable);

    }

    private void initializeComponents() {

	searchComponentsTable = new FlexTable();
	quickSearchTextbox = new TextBox();
	cabinetTypeInfoList = new ListBox();
	documentTypeList = new ListBox();

    }

}
