package com.interdevinc.efiling.client.view;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
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
    private ListBox clientInfoList;
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
	searchComponentsTable.setWidget(row, 0, new Label("Quick Search"));
	searchComponentsTable.setWidget(row, 1, quickSearchTextbox);
	++row;
	
	searchComponentsTable.setWidget(row, 0, clientInfoList);
	searchComponentsTable.setWidget(row, 1, documentTypeList);
	
	for (Client clientInfo : searchComponents.getClientList()) {
	    clientInfoList.addItem(clientInfo.getClientFullInfo());
	}
	
	for (DocumentType documentTypeInfo : searchComponents.getDocumentTypeList()) {
	    documentTypeList.addItem(documentTypeInfo.getDocumentTypeFullInfo());
	}
	
	searchDocumentsPanel.add(searchComponentsTable);
	
    }
    
    private void initializeComponents() {
	
	searchComponentsTable = new FlexTable();
	quickSearchTextbox = new TextBox();
	clientInfoList = new ListBox();
	documentTypeList = new ListBox();
	
    }
    
}
