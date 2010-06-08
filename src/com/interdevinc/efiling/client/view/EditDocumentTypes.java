package com.interdevinc.efiling.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.DocumentType;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.FileCabinetService;
import com.interdevinc.efiling.client.processor.FileCabinetServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class EditDocumentTypes implements ChangeHandler, ClickHandler {

    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;

    // Panels
    private VerticalPanel mainPanel;
    private TabPanel addEditTypePanel;
    private VerticalPanel addTypePanel;
    private VerticalPanel editTypePanel;
    private InformationDialogBox idb;
    
    // Add Document Type Componenets
    private FlexTable addComponentsTable;
    private TextBox documentTypeTextbox;
    private TextBox documentAbbrTextbox;
    private Button addDocumentTypeButton;

    // Delete/Edit Document Type Components.
    private FlexTable editComponentsTable;
    private ListBox existingTypeListbox;
    private TextBox editDocumentTypeTextbox;
    private TextBox editDocumentAbbrTextbox;
    private Button editDocumentTypeButton;
    private Button deleteDocumentTypeButton;

    // RPC Workers
    private FileCabinetServiceAsync documentTypeAsync;
    private AddDocumentTypeHandler addDocumentTypeHandler;
    private EditDocumentTypeHandler editDocumentTypeHandler;
    private DeleteDocumentTypeHandler deleteDocumentTypeHandler;

    /**
     * CONSTRUCTOR: EDIT DOCUMENT TYPES
     * @param au the AuthenticatedUser to set
     * @param fc the FileCabinet to set
     * @param mp the VerticalPanel to set
     * @param sc the SearchComponents to set
     */
    public EditDocumentTypes(AuthenticatedUser au, FileCabinet fc, VerticalPanel mp, SearchComponents sc) {
	authenticatedUser = au;
	fileCabinet = fc;
	mainPanel = mp;
	searchComponents = sc;
	idb = new InformationDialogBox();
	    
	initializeInitialComponents();
	assembleInitialComponents();

	initializeRpcWorkers();
    }

    /**
     * METHOD: ON CHANGE
     */
    @Override
    public void onChange(ChangeEvent event) {

	if (existingTypeListbox.getSelectedIndex() > 0) {
	    
	    String documentName = searchComponents.getDocumentTypeList().get(existingTypeListbox.getSelectedIndex() - 1).getDocumentTypeName();
	    String documentAbbr = searchComponents.getDocumentTypeList().get(existingTypeListbox.getSelectedIndex() - 1).getDocumentTypeAbbr();
	    
	    editDocumentTypeTextbox.setText(documentName);
	    editDocumentAbbrTextbox.setText(documentAbbr);
	    
	    editComponentsTable.setText(1, 0, "Document Name");
	    editComponentsTable.setText(1, 1, "Document Abbr");
	    editComponentsTable.setWidget(2, 0, editDocumentTypeTextbox);
	    editComponentsTable.setWidget(2, 1, editDocumentAbbrTextbox);
	    editComponentsTable.setWidget(2, 2, editDocumentTypeButton);
	    editComponentsTable.setText(3, 0, documentName);
	    editComponentsTable.setText(3, 1, documentAbbr);
	    editComponentsTable.setWidget(3, 2, deleteDocumentTypeButton);
	    editDocumentTypeButton.addClickHandler(this);
	    deleteDocumentTypeButton.addClickHandler(this);
	    
	}
    }

    /**
     * METHOD: ON CLICK
     */
    @Override
    public void onClick(ClickEvent event) {

	if (event.getSource().equals(addDocumentTypeButton)) {
	    if (!documentTypeTextbox.getText().equals("") && !documentAbbrTextbox.getText().equals("")) {
		documentTypeAsync.addDocumentType(fileCabinet, documentTypeTextbox.getText(), documentAbbrTextbox.getText(), addDocumentTypeHandler);
	    } else {
		idb.messageDialogBox("Missing Information", "Please fill in a Document Type, and Document Abbr.");
	    }
	}

	if (event.getSource().equals(deleteDocumentTypeButton)) {
	    if (existingTypeListbox.getSelectedIndex() > 0) {
		String documentTypeAbbr = searchComponents.getDocumentTypeList().get(existingTypeListbox.getSelectedIndex() - 1).getDocumentTypeAbbr();
		documentTypeAsync.deleteDocumentType(fileCabinet, documentTypeAbbr, deleteDocumentTypeHandler);
	    }
	}

	if (event.getSource().equals(editDocumentTypeButton)) {
	    if (existingTypeListbox.getSelectedIndex() > 0 && !editDocumentTypeTextbox.getText().equals("") && !editDocumentAbbrTextbox.getText().equals("")) {
		String documentTypeAbbr = searchComponents.getDocumentTypeList().get(existingTypeListbox.getSelectedIndex() - 1).getDocumentTypeAbbr();
		documentTypeAsync.editDocumentType(fileCabinet, editDocumentTypeTextbox.getText(), editDocumentAbbrTextbox.getText(), documentTypeAbbr, editDocumentTypeHandler);
	    }
	}

    }

    /**
     * METHOD: ASSEMBLE INITIAL COMPONENTS
     */
    private void assembleInitialComponents() {
	addEditTypePanel.add(addTypePanel, "Add Document Type");
	addEditTypePanel.add(editTypePanel, "Delete/Edit Document Type");

	addTypePanel.add(addComponentsTable);
	addComponentsTable.setText(0, 0, "Document Type:");
	addComponentsTable.setText(0, 1, "Document Abbr:");
	addComponentsTable.setWidget(1, 0, documentTypeTextbox);
	addComponentsTable.setWidget(1, 1, documentAbbrTextbox);
	addComponentsTable.setWidget(1, 2, addDocumentTypeButton);
	addDocumentTypeButton.addClickHandler(this);

	editTypePanel.add(editComponentsTable);
	editComponentsTable.setWidget(0, 0, existingTypeListbox);
	editComponentsTable.getFlexCellFormatter().setColSpan(0, 0, 2);
	
	existingTypeListbox.addItem("Select a Document Type");
	for (DocumentType documentTypeInfo : searchComponents.getDocumentTypeList()) {
	    existingTypeListbox.addItem(documentTypeInfo.getDocumentTypeFullInfo());
	}
	existingTypeListbox.addChangeHandler(this);

	mainPanel.add(addEditTypePanel);
    }

    /**
     * METHOD: INITIALIZE INITIAL COMPONENTS 
     */
    private void initializeInitialComponents() {
	addEditTypePanel = new TabPanel();
	addTypePanel = new VerticalPanel();
	editTypePanel = new VerticalPanel();

	editComponentsTable = new FlexTable();
	existingTypeListbox = new ListBox();
	editDocumentTypeTextbox = new TextBox();
	editDocumentAbbrTextbox = new TextBox();
	editDocumentTypeButton = new Button("Edit Document Type");
	deleteDocumentTypeButton = new Button("Delete Document Type");

	addComponentsTable = new FlexTable();
	documentTypeTextbox = new TextBox();
	documentAbbrTextbox = new TextBox();
	addDocumentTypeButton = new Button("Add Document Type");

    }

    /**
     * METHOD: INITIALIZE RPC WORKERS
     */
    private void initializeRpcWorkers() {

	// Define the service to call  
	documentTypeAsync = (FileCabinetServiceAsync) GWT.create(FileCabinetService.class); 

	// Initialize the RPC Classes.
	addDocumentTypeHandler = new AddDocumentTypeHandler();
	editDocumentTypeHandler = new EditDocumentTypeHandler();
	deleteDocumentTypeHandler = new DeleteDocumentTypeHandler();

    }

    /**
     * METHOD: LOAD FILE CABINET VIEW
     * Loads the file cabinet view.
     */
    private void loadFileCabinetView(FileCabinet fileCabinet) {
	RootPanel.get("main-container").clear();
	FileCabinetView fileCabinetView = new FileCabinetView(authenticatedUser, fileCabinet);
	RootPanel.get("main-container").add(fileCabinetView.showView());
    }
    
    /**
     * PRIVATE CLASS: ADD DOCUMENT TYPE HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class AddDocumentTypeHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {
	    loadFileCabinetView(fileCabinet);
	    idb.messageDialogBox("Add Document Type" , resultMessage);
	}

    }

    /**
     * PRIVATE CLASS: DELETE DOCUMENT TYPE HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class DeleteDocumentTypeHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {
	    loadFileCabinetView(fileCabinet);
	    idb.messageDialogBox("Delete Document Type" , resultMessage);
	}

    }

    /**
     * PRIVATE CLASS: EDIT DOCUMENT TYPE HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class EditDocumentTypeHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {
	    loadFileCabinetView(fileCabinet);
	    idb.messageDialogBox("Edit Document Type" , resultMessage);
	}

    }

}
