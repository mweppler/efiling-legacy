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
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.client.processor.ClientInformationService;
import com.interdevinc.efiling.client.processor.ClientInformationServiceAsync;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class EditClientInfo implements ChangeHandler, ClickHandler {

    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;
    
    // Panels
    private VerticalPanel mainPanel;
    private TabPanel addEditClientPanel;
    private VerticalPanel addClientPanel;
    private VerticalPanel editClientPanel;
    private InformationDialogBox idb;
    
    // Add Client Componenets
    private FlexTable addComponentsTable;
    private TextBox clientFirstNameTextbox;
    private TextBox clientLastNameTextbox;
    private TextBox clientAccountNumberTextbox;
    private TextBox clientRepNumberTextbox;
    private Button addClientButton;

    // Delete/Edit Client Components.
    private FlexTable editComponentsTable;
    private ListBox existingClientListbox;
    private TextBox editClientFirstNameTextbox;
    private TextBox editClientLastNameTextbox;
    private TextBox editClientAccountNumberTextbox;
    private TextBox editClientRepNumberTextbox;
    private Button editClientButton;
    private Button deleteClientButton;

    // RPC Workers
    private ClientInformationServiceAsync clientInformationAsync;
    private AddClientInformationHandler addClientInformationHandler;
    private EditClientInformationHandler editClientInformationHandler;
    private DeleteClientInformationHandler deleteInformationHandler;
    
    /**
     * CONSTRUCTOR: EDIT CLIENT INFO
     * @param au the AuthenticatedUser to set
     * @param fc the FileCabinet to set
     * @param mp the VerticalPanel to set
     * @param sc the SearchComponents to set
     */
    public EditClientInfo(AuthenticatedUser au, FileCabinet fc, VerticalPanel mp, SearchComponents sc) {
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

	if (existingClientListbox.getSelectedIndex() > 0) {
	    
	    String clientFirstName = searchComponents.getClientList().get(existingClientListbox.getSelectedIndex() - 1).getFirstName();
	    String clientLastName = searchComponents.getClientList().get(existingClientListbox.getSelectedIndex() - 1).getLastName();
	    String clientAccountNumber = searchComponents.getClientList().get(existingClientListbox.getSelectedIndex() - 1).getAccountNumber();
	    String clientRepNumber = searchComponents.getClientList().get(existingClientListbox.getSelectedIndex() - 1).getRepNumber();
	    
	    editClientFirstNameTextbox.setText(clientFirstName);
	    editClientLastNameTextbox.setText(clientLastName);
	    editClientAccountNumberTextbox.setText(clientAccountNumber);
	    editClientRepNumberTextbox.setText(clientRepNumber);
	    
	    editComponentsTable.setText(1, 0, "First Name:");
	    editComponentsTable.setText(1, 1, "Last Name:");
	    editComponentsTable.setText(1, 2, "Account Number:");
	    editComponentsTable.setText(1, 3, "Rep Number:");
	    editComponentsTable.setWidget(2, 0, editClientFirstNameTextbox);
	    editComponentsTable.setWidget(2, 1, editClientLastNameTextbox);
	    editComponentsTable.setWidget(2, 2, editClientAccountNumberTextbox);
	    editComponentsTable.setWidget(2, 3, editClientRepNumberTextbox);
	    editComponentsTable.setWidget(2, 4, editClientButton);
	    editComponentsTable.setText(3, 0, clientFirstName);
	    editComponentsTable.setText(3, 1, clientLastName);
	    editComponentsTable.setText(3, 2, clientAccountNumber);
	    editComponentsTable.setText(3, 3, clientRepNumber);
	    editComponentsTable.setWidget(3, 4, deleteClientButton);
	    editClientButton.addClickHandler(this);
	    deleteClientButton.addClickHandler(this);
	    
	} else {
	    
	}

    }

    /**
     * METHOD: ON CLICK
     */
    @Override
    public void onClick(ClickEvent event) {

	if (event.getSource().equals(addClientButton)) {
	    if (!clientFirstNameTextbox.getText().equals("") && !clientLastNameTextbox.getText().equals("") && !clientAccountNumberTextbox.getText().equals("") && !clientRepNumberTextbox.getText().equals("")) {
		clientInformationAsync.addClientInformation(fileCabinet, clientFirstNameTextbox.getText(), clientLastNameTextbox.getText(), clientAccountNumberTextbox.getText(), clientRepNumberTextbox.getText(), addClientInformationHandler);
	    } else {
		idb.messageDialogBox(1, "Missing Information", "Please fill in all fields.");
	    }
	}

	if (event.getSource().equals(deleteClientButton)) {
	    if (existingClientListbox.getSelectedIndex() > 0) {
		String clientID = searchComponents.getClientList().get(existingClientListbox.getSelectedIndex() - 1).getClientID();
		clientInformationAsync.deleteClientInformation(fileCabinet, clientID, deleteInformationHandler);
	    }
	}

	if (event.getSource().equals(editClientButton)) {
	    if (!editClientAccountNumberTextbox.getText().equals("") && !editClientFirstNameTextbox.getText().equals("") && !editClientLastNameTextbox.getText().equals("") && !editClientRepNumberTextbox.getText().equals("")) {
		String clientID = searchComponents.getClientList().get(existingClientListbox.getSelectedIndex() - 1).getClientID();
		clientInformationAsync.editClientInformation(fileCabinet, editClientFirstNameTextbox.getText(), editClientLastNameTextbox.getText(), editClientAccountNumberTextbox.getText(), editClientRepNumberTextbox.getText(), clientID, editClientInformationHandler);
	    } else {
		idb.messageDialogBox(1, "Missing Information", "Please fill in all fields.");
	    }
	}

    }

    /**
     * METHOD: ASSEMBLE INITIAL COMPONENTS
     */
    private void assembleInitialComponents() {
	addEditClientPanel.add(addClientPanel, "Add Client");
	addEditClientPanel.add(editClientPanel, "Delete/Edit Client");

	addClientPanel.add(addComponentsTable);
	addComponentsTable.setText(0, 0, "First Name:");
	addComponentsTable.setText(0, 1, "Last Name:");
	addComponentsTable.setText(0, 2, "Account Number:");
	addComponentsTable.setText(0, 3, "Rep Number:");
	addComponentsTable.setWidget(1, 0, clientFirstNameTextbox);
	addComponentsTable.setWidget(1, 1, clientLastNameTextbox);
	addComponentsTable.setWidget(1, 2, clientAccountNumberTextbox);
	addComponentsTable.setWidget(1, 3, clientRepNumberTextbox);
	addComponentsTable.setWidget(1, 4, addClientButton);
	addClientButton.addClickHandler(this);

	editClientPanel.add(editComponentsTable);
	editComponentsTable.setWidget(0, 0, existingClientListbox);
	editComponentsTable.getFlexCellFormatter().setColSpan(0, 0, 2);
	
	existingClientListbox.addItem("Select a Client");
	for (Client clientInfo : searchComponents.getClientList()) {
	    existingClientListbox.addItem(clientInfo.getClientFullInfo());
	}
	existingClientListbox.addChangeHandler(this);

	mainPanel.add(addEditClientPanel);
    }

    /**
     * METHOD: INITIALIZE INITIAL COMPONENTS 
     */
    private void initializeInitialComponents() {
	addEditClientPanel = new TabPanel();
	addClientPanel = new VerticalPanel();
	editClientPanel = new VerticalPanel();

	editComponentsTable = new FlexTable();
	existingClientListbox = new ListBox();
	editClientFirstNameTextbox = new TextBox();
	editClientLastNameTextbox = new TextBox();
	editClientAccountNumberTextbox = new TextBox();
	editClientRepNumberTextbox = new TextBox();
	editClientButton = new Button("Edit Client");
	deleteClientButton = new Button("Delete Client");

	addComponentsTable = new FlexTable();
	clientFirstNameTextbox = new TextBox();
	clientLastNameTextbox = new TextBox();
	clientRepNumberTextbox = new TextBox();
	clientAccountNumberTextbox = new TextBox();
	addClientButton = new Button("Add Client");

    }

    /**
     * METHOD: INITIALIZE RPC WORKERS
     */
    private void initializeRpcWorkers() {

	// Define the service to call  
	clientInformationAsync = (ClientInformationServiceAsync) GWT.create(ClientInformationService.class); 

	// Initialize the RPC Classes.
	addClientInformationHandler = new AddClientInformationHandler();
	editClientInformationHandler = new EditClientInformationHandler();
	deleteInformationHandler = new DeleteClientInformationHandler();

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
     * PRIVATE CLASS: ADD CLIENT INFORMATION HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class AddClientInformationHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {
	    loadFileCabinetView(fileCabinet);
	    idb.messageDialogBox(2, "Add Client" , resultMessage);
	}

    }

    /**
     * PRIVATE CLASS: DELETE CLIENT INFORMATION HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class DeleteClientInformationHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {
	    loadFileCabinetView(fileCabinet);
	    idb.messageDialogBox(2, "Delete Client" , resultMessage);
	}

    }

    /**
     * PRIVATE CLASS: EDIT CLIENT INFORMATION HANDLER
     * @author mweppler
     * GWT AsyncCallback
     */
    private class EditClientInformationHandler implements AsyncCallback<String> {

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(String resultMessage) {
	    loadFileCabinetView(fileCabinet);
	    idb.messageDialogBox(2, "Edit Client" , resultMessage);
	}

    }
    
}
