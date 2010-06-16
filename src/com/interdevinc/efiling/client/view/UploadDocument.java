package com.interdevinc.efiling.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.interdevinc.efiling.client.model.AuthenticatedUser;
import com.interdevinc.efiling.client.model.Broker;
import com.interdevinc.efiling.client.model.Client;
import com.interdevinc.efiling.client.model.DocumentType;
import com.interdevinc.efiling.client.model.FileCabinet;
import com.interdevinc.efiling.client.model.SearchComponents;
import com.interdevinc.efiling.shared.view.InformationDialogBox;

public class UploadDocument implements ClickHandler, FormPanel.SubmitCompleteHandler, FormPanel.SubmitHandler {

    private AuthenticatedUser authenticatedUser;
    private FileCabinet fileCabinet;
    private SearchComponents searchComponents;
    private InformationDialogBox idb;

    // Panels
    private VerticalPanel mainPanel;
    private FormPanel uploadFormPanel;
    private VerticalPanel widgetPanel;

    // Form components
    private Button uploadFileButton;
    private FileUpload fileUploadWidget;
    private Hidden cabinetFormField;
    private Hidden associatedWithFormField;
    private Hidden authenticatedUserFormField;
    private Hidden documentTypeFormField;
    private ListBox associatedWithListbox;
    private ListBox documentTypeListbox;

    /**
     * CONSTRUCTOR: UPLOAD DOCUMENT
     * @param au the authenticatedUser to set
     * @param fc the fileCabinet to set
     * @param mp the mainPanel to set
     * @param sc the searchComponents to set
     * Sets components, initializes and assembles components.
     */
    public UploadDocument(AuthenticatedUser au, FileCabinet fc, VerticalPanel mp, SearchComponents sc) {
	authenticatedUser = au;
	fileCabinet = fc;
	searchComponents = sc;
	mainPanel = mp;
	idb = new InformationDialogBox();

	loadFormComponents();
	
    }

    /**
     * METHOD: ON CLICK
     * The on click method for the submit button. Calls onSubmit().
     */
    public void onClick(ClickEvent event) {
	uploadFormPanel.submit();
    }

    /**
     * METHOD: ON SUBMIT
     * The on submit method for the form submission. Checks for form data, sets the values, and adds the submit complete handler.
     */
    public void onSubmit(SubmitEvent event) {

	if (associatedWithListbox.getSelectedIndex() <= 0 || documentTypeListbox.getSelectedIndex() <= 0 || fileUploadWidget.getFilename().equals("")) {
	    idb.messageDialogBox(1, "Missing Information", "Please fill in all fields.");
	    event.cancel();
	}

//	if (documentTypeListbox.getSelectedIndex() <= 0) {
//	    Window.alert("Please choose a Document Type.");
//	    event.cancel();
//	}
//
//	if (fileUploadWidget.getFilename().equals("")) {
//	    Window.alert("Please select a file to upload.");
//	    event.cancel();
//	}

	if (!event.isCanceled()) {

	    authenticatedUserFormField.setValue(authenticatedUser.getUsername());
	    
	    if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
		associatedWithFormField.setValue(searchComponents.getBrokerList().get(associatedWithListbox.getSelectedIndex() - 1).getRepNumber());
	    } else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
		associatedWithFormField.setValue(searchComponents.getClientList().get(associatedWithListbox.getSelectedIndex() - 1).getAccountNumber());
	    }

	    documentTypeFormField.setValue(searchComponents.getDocumentTypeList().get(documentTypeListbox.getSelectedIndex() - 1).getDocumentTypeAbbr());

	    if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
		cabinetFormField.setValue("broker");
	    } else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
		cabinetFormField.setValue("client");
	    }

	    uploadFormPanel.addSubmitCompleteHandler(this);
	}

    }

    /**
     * METHOD: ON SUBMIT COMPLETE
     * The on submit complete method, waits for a response message and displays it in a dialog box.
     */
    public void onSubmitComplete(SubmitCompleteEvent event) {
	loadFormComponents();
	InformationDialogBox idb = new InformationDialogBox();
	idb.messageDialogBox(1, "File Upload Result", event.getResults());
    }

    /**
     * METHOD: ASSEMBLE INITIAL COMPONENTS
     */
    private void assembleInitialComponents() {

	// Point the FormPanel at a service.
	//uploadFormPanel.setAction(GWT.getModuleBaseURL() + "fileupload");
	uploadFormPanel.setAction(GWT.getHostPageBaseURL() + "efiling/fileupload");

	// Because we're going to add a FileUpload widget, we'll need to set the
	// form to use the POST method, and multipart MIME encoding.
	uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
	uploadFormPanel.setMethod(FormPanel.METHOD_POST);

	uploadFormPanel.addSubmitHandler(this);
	
	uploadFormPanel.setWidget(widgetPanel);
	
	if (fileCabinet.getCabinetName().equals("Broker Paperwork")) {
	    // Build Listboxes.
	    associatedWithListbox.addItem("Upload Broker Info");
	    for (Broker brokerInfo : searchComponents.getBrokerList()) {
		associatedWithListbox.addItem(brokerInfo.getBrokerFullInfo());
	    }
	} else if (fileCabinet.getCabinetName().equals("Client Paperwork")) {
	    // Build Listboxes.
	    associatedWithListbox.addItem("Upload Client Info");
	    for (Client clientInfo : searchComponents.getClientList()) {
		associatedWithListbox.addItem(clientInfo.getClientFullInfo());
	    }
	} else {
	    associatedWithListbox.addItem("Cabinet not loaded!");
	}
	widgetPanel.add(associatedWithListbox);

	documentTypeListbox.addItem("Upload Document Type");
	for (DocumentType documentTypeInfo : searchComponents.getDocumentTypeList()) {
	    documentTypeListbox.addItem(documentTypeInfo.getDocumentTypeFullInfo());
	}
	widgetPanel.add(documentTypeListbox);

	associatedWithFormField.setName("associatedWith");
	widgetPanel.add(associatedWithFormField);

	authenticatedUserFormField.setName("authenticatedUser");
	widgetPanel.add(authenticatedUserFormField);
	
	documentTypeFormField.setName("documentType");
	widgetPanel.add(documentTypeFormField);
	    
	cabinetFormField.setName("fileCabinet");
	widgetPanel.add(cabinetFormField);

	fileUploadWidget.setName("uploadForm");
	widgetPanel.add(fileUploadWidget);

	uploadFileButton.addClickHandler(this);
	widgetPanel.add(uploadFileButton);

	mainPanel.add(uploadFormPanel);
	
    }

    /**
     * METHOD: INITIALIZE INITIAL COMPONENTS
     */
    private void initializeInitialComponents() {
	
	// Create a FormPanel.
	uploadFormPanel = new FormPanel();
	
	// Create a panel to hold all of the form widgets.
	widgetPanel = new VerticalPanel();
	
	associatedWithListbox = new ListBox();
	documentTypeListbox = new ListBox();
	associatedWithFormField = new Hidden();
	authenticatedUserFormField = new Hidden();
	documentTypeFormField = new Hidden();
	cabinetFormField = new Hidden();
	// Create a FileUpload widget.
	fileUploadWidget = new FileUpload();
	// Add a 'submit' button.
	uploadFileButton = new Button("Upload File");
	
    }

    /**
     * METHOD: LOAD FORM COMPONENTS
     */
    private void loadFormComponents() {
	
	initializeInitialComponents();
	assembleInitialComponents();
	
    }
    
}
