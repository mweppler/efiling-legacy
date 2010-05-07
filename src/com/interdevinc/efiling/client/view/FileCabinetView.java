package com.interdevinc.efiling.client.view;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.processor.FileCabinetServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class FileCabinetView {

    // Models
    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;

    // Panels
    private VerticalPanel mainPanel;
    private DecoratedTabPanel taskTabPanel;
    private VerticalPanel searchDocumentsPanel;
    private VerticalPanel uploadDocumentPanel;
    private VerticalPanel editDocumentTypesPanel;
    private VerticalPanel editClientInfoPanel;

    private FileCabinetServiceAsync fileCabinetAsync;
    private FileCabinetHandler fileCabinetHandler;
    private InformationDialogBox idb;

    /**
     * CONSTRUCTOR: FILE CABINET VIEW
     * @param au the authenticatedUser to set
     * @param fc the fileCabinet to set
     */
    public FileCabinetView(AuthenticatedUser au, FileCabinet fc) {
	authenticatedUser = au;
	fileCabinet = fc;
	idb = new InformationDialogBox();

	initializeComponents();
	assembleComponents();
    }

    /**
     * METHOD: SHOW VIEW
     * @return mainPanel
     */
    public Widget showView() {
	return mainPanel;
    }

    private void initializeComponents() {

	mainPanel = new VerticalPanel();
	taskTabPanel = new DecoratedTabPanel();
//	taskTabPanel.setWidth("700px");
	taskTabPanel.setAnimationEnabled(true);

	searchDocumentsPanel = new VerticalPanel();
	SearchDocuments searchDocuments = new SearchDocuments(searchDocumentsPanel);

	uploadDocumentPanel = new VerticalPanel();
	UploadDocument uploadDocument = new UploadDocument(uploadDocumentPanel);

	editDocumentTypesPanel = new VerticalPanel();
	EditDocumentTypes editDocumentType = new EditDocumentTypes(editDocumentTypesPanel);

	if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
	    editClientInfoPanel = new VerticalPanel();
	    EditClientInfo editClientInfo = new EditClientInfo(editClientInfoPanel);
	}
	
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

    private class FileCabinetHandler implements AsyncCallback<ArrayList<FileCabinet>> {

	@Override
	public void onFailure(Throwable caught) {
	    idb.destroyTimer();
	    idb.errorMessageDialogBox("RPC Failure" , "File Cabinet Selection RPC Failure");
	}

	@Override
	public void onSuccess(ArrayList<FileCabinet> fc) {
	    idb.destroyTimer();
	}

    }
}
