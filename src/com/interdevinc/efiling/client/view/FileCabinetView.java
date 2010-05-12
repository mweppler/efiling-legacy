package com.interdevinc.efiling.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.FileCabinetService;
import com.interdevinc.efiling.client.processor.FileCabinetServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class FileCabinetView {

    // Models
    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;
    
    // Panels
    private VerticalPanel mainPanel;
    private DecoratedTabPanel taskTabPanel;
    private VerticalPanel searchDocumentsPanel;
    private VerticalPanel uploadDocumentPanel;
    private VerticalPanel editDocumentTypesPanel;
    private VerticalPanel editClientInfoPanel;
    private InformationDialogBox idb;
    
    private FileCabinetServiceAsync fileCabinetAsync;
    private SearchComponentsHandler searchComponentsHandler;
    
    /**
     * CONSTRUCTOR: FILE CABINET VIEW
     * @param au the authenticatedUser to set
     * @param fc the fileCabinet to set
     */
    public FileCabinetView(AuthenticatedUser au, FileCabinet fc) {
	authenticatedUser = au;
	fileCabinet = fc;
	mainPanel = new VerticalPanel();

	idb = new InformationDialogBox();
	idb.loadingDialogBox("Loading Contents into " + fileCabinet.getCabinetName());
	initializeRemoteProcedureWorkers();
	
    }

    /**
     * METHOD: SHOW VIEW
     * @return mainPanel
     */
    public Widget showView() {
	return mainPanel;
    }

    private void assembleComponents() {

	mainPanel.add(taskTabPanel);

	taskTabPanel.add(searchDocumentsPanel, "Search Documents");
	taskTabPanel.add(uploadDocumentPanel, "Upload Document");
	taskTabPanel.add(editDocumentTypesPanel, "Edit Document Types");
	if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
	    taskTabPanel.add(editClientInfoPanel, "Edit Client Information");
	}

    }
    
    private void initializeComponents() {

	taskTabPanel = new DecoratedTabPanel();
//	taskTabPanel.setWidth("700px");
	taskTabPanel.setAnimationEnabled(true);

	searchDocumentsPanel = new VerticalPanel();
	SearchDocuments searchDocuments = new SearchDocuments(authenticatedUser, fileCabinet, searchDocumentsPanel, searchComponents);

	uploadDocumentPanel = new VerticalPanel();
	UploadDocument uploadDocument = new UploadDocument(uploadDocumentPanel);

	editDocumentTypesPanel = new VerticalPanel();
	EditDocumentTypes editDocumentType = new EditDocumentTypes(editDocumentTypesPanel);

	if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
	    editClientInfoPanel = new VerticalPanel();
	    EditClientInfo editClientInfo = new EditClientInfo(editClientInfoPanel);
	}
	
    }
    
    /**
     * METHOD: INIT REMOTE PROCEDURE WORKERS
     * This method initializes all object associated with
     * creating a valid RPC call.	 */
    private void initializeRemoteProcedureWorkers() {
	//define the service to call  
	fileCabinetAsync = (FileCabinetServiceAsync) GWT.create(FileCabinetService.class);    

	//init RPC handler
	searchComponentsHandler = new SearchComponentsHandler();
	
	//execute authentication procedure
	fileCabinetAsync.retrieveSearchComponents(fileCabinet, searchComponentsHandler);
	    
    }
    
    private void loadInitialComponents() {
	initializeComponents();
	assembleComponents();
    }
    
    private class SearchComponentsHandler implements AsyncCallback<SearchComponents> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.destroyTimer();
	    idb.errorMessageDialogBox("RPC Failure" , "Load Search Components RPC Failure");
	}

	@Override
	public void onSuccess(SearchComponents sc) {
	    searchComponents = sc;
	    loadInitialComponents();
	    idb.destroyTimer();
	}

    }
    
}
